package de.quoss.camel.quarkus.jms.xa.route;

import de.quoss.narayana.helper.ConnectionFactoryProxy;
import de.quoss.narayana.helper.NarayanaTransactionHelper;
import io.smallrye.common.constraint.Assert;
import org.apache.activemq.artemis.jms.client.ActiveMQXAConnectionFactory;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

@ApplicationScoped
public class Main extends EndpointRouteBuilder {

    static final String ROUTE_ID = "main";

    private final TransactionManager tm;

    private final UserTransaction ut;

    public Main(final TransactionManager tm, final UserTransaction ut) {
        this.tm = Assert.assertNotNull(tm);
        this.ut = Assert.assertNotNull(ut);
    }

    public void configure() {

        final JmsComponent component = ((JmsComponent) getContext().getComponent("jms"));
        component.setTransactionManager(new JtaTransactionManager(ut, tm));
        ActiveMQXAConnectionFactory cf = new ActiveMQXAConnectionFactory();
        // cf.setClientID("client-id");
        component.setConnectionFactory(new ConnectionFactoryProxy(cf, new NarayanaTransactionHelper(tm)));
        component.getConfiguration().setSynchronous(true);

        from(jms("topic:foo").durableSubscriptionName("durable-subscription").subscriptionShared(true)
                .advanced().receiveTimeout(1000L)).routeId(ROUTE_ID)
                .to(log("boo")).id(Main.ROUTE_ID + ".log");

    }

}
