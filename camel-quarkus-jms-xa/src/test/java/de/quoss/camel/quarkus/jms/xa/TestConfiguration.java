package de.quoss.camel.quarkus.jms.xa;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQXAConnectionFactory;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.jms.Destination;

@Dependent
public class TestConfiguration {

    private static final ActiveMQConnectionFactory CF = new ActiveMQConnectionFactory("vm://localhost");

    public TestConfiguration() {
        // add configs here
    }

    @Produces
    public ActiveMQConnectionFactory cf() {
        return CF;
    }

    @Produces
    public Destination d() {
        return CF.createContext().createTopic("foo");
    }

}
