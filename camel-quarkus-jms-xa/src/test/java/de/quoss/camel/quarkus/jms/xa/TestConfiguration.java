package de.quoss.camel.quarkus.jms.xa;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.jms.Destination;

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
