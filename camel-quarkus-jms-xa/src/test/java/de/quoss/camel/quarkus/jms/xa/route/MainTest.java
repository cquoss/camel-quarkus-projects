package de.quoss.camel.quarkus.jms.xa.route;

import io.quarkus.artemis.test.ArtemisTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(ArtemisTestResource.class)
class MainTest {

    @Test
    void testMain() {

    }

}
