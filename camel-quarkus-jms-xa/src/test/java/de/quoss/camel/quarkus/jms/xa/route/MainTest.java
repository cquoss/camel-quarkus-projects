package de.quoss.camel.quarkus.jms.xa.route;

import io.quarkus.artemis.test.ArtemisTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.builder.AdviceWith;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jms.core.JmsTemplate;

import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@QuarkusTest
@QuarkusTestResource(ArtemisTestResource.class)
class MainTest {

    private static final Logger LOGGER = Logger.getLogger(MainTest.class);

    @Inject
    CamelContext ctx;

    /**
     * Test that message remains in topic queue when error in route occurs.
     * @throws Exception in case of unexpected test setup errors.
     */
    @Test
    void testMainError() throws Exception {
        final String methodName = "testMainError()";
        LOGGER.tracef("%s start", methodName);
        // create connection factory
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
        // stop the main route / wait 1 second before doing so
        Assertions.assertNotNull(ctx);
        Thread.sleep(1000L);
        ctx.getRouteController().stopRoute(Main.ROUTE_ID);
        // add error producing processor before log node
        AdviceWith.adviceWith(ctx, Main.ROUTE_ID, a -> {
            a.weaveById(Main.ROUTE_ID + ".log").before().process(e -> {
                throw new RuntimeException("This happens on purpose");
            }).id(Main.ROUTE_ID + ".process");
        });
        // start the main route
        ctx.getRouteController().startRoute(Main.ROUTE_ID);
        // send message
        JmsTemplate template = new JmsTemplate(cf);
        template.setReceiveTimeout(1000L);
        Destination d = cf.createContext().createTopic("foo");
        template.convertAndSend(d, "test");
        try {
            Awaitility.waitAtMost(10, TimeUnit.SECONDS).until(new ReceiveMessage(cf));
        } catch (ConditionTimeoutException e) {
            Assertions.fail("Timed out while waiting for message to arrive in DLA.");
        }
        LOGGER.tracef("{} end", methodName);
    }

    private static class ReceiveMessage implements Callable<Boolean> {

        private final ConnectionFactory cf;

        ReceiveMessage(final ConnectionFactory cf) {
            this.cf = cf;
        }

        public Boolean call() {
            JmsTemplate template = new JmsTemplate(cf);
            Destination d = cf.createContext().createQueue("DLA");
            return template.receive(d) != null;
        }

    }

    @AfterEach
    void tearDown() throws Exception {
        // remove process endpoint from main route if configured
        List<Processor> l = ctx.getRoute(Main.ROUTE_ID).filter(Main.ROUTE_ID + ".process");
        if (l.isEmpty()) {
            // do nothing
        } else {
            // stop the main route
            Assertions.assertNotNull(ctx);
            // remove error producing processor
            AdviceWith.adviceWith(ctx, Main.ROUTE_ID, a -> {
                a.weaveById(Main.ROUTE_ID + ".process").remove();
            });
            // start the main route
            ctx.getRouteController().startRoute(Main.ROUTE_ID);
        }
    }

}
