package de.quoss.camel.quarkus.jms.xa.route;

import de.unioninvestment.md.dp.basis.narayana.ConnectionFactoryProxy;
import de.unioninvestment.md.dp.basis.narayana.NarayanaTransactionHelper;
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
        component.setConnectionFactory(new ConnectionFactoryProxy(new ActiveMQXAConnectionFactory(), new NarayanaTransactionHelper(tm)));

        from(jms("topic:foo::bar")).routeId(ROUTE_ID)
                .to(log("boo"));

    }

}
