package com.eli;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class HealthResourceTest extends HttpTestSupport {

    @TestHTTPResource("/q/health/live")
    URI livenessUri;

    @Test
    void shouldReportLive() throws Exception {
        HttpResponse<String> response = get(livenessUri);

        assertEquals(200, response.statusCode());
        assertUpStatus(response.body());
        assertTrue(response.body().contains("I'm alive"));
    }
}
