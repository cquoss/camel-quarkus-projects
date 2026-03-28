package de.quoss.camel.quarkus.jms.xa.route;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.jms.ConnectionFactory;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.component.jms.JmsComponent;

import jakarta.transaction.TransactionManager;
import org.apache.activemq.artemis.jms.client.ActiveMQXAConnectionFactory;
import org.springframework.transaction.jta.JtaTransactionManager;

@ApplicationScoped
public class MainRoute extends EndpointRouteBuilder {

    static final String ROUTE_ID = "main";

    private final ConnectionFactory cf;
    
    private final TransactionManager tm;

    public MainRoute(ConnectionFactory cf, TransactionManager tm) {
        this.cf = cf;
        this.tm = tm;
    }
    
    @Override
    public void configure() {

        var component = ((JmsComponent) getContext().getComponent("jms"));
        component.setTransactionManager(new JtaTransactionManager(tm));
        component.setConnectionFactory(cf);
        // component.getConfiguration().setSynchronous(true);

        from(jms("topic:foo").durableSubscriptionName("durable-subscription").subscriptionShared(true)
                .advanced().receiveTimeout(1000L)).routeId(ROUTE_ID)
                .to(log("boo")).id(MainRoute.ROUTE_ID + ".log");

    }

}
