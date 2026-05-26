package com.eli;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(StarWarsTestResource.class)
class StarWarsResourceTest extends HttpTestSupport {

    @TestHTTPResource("/api/v1/starwars/starships")
    URI starshipsUri;

    @TestHTTPResource("/q/health/ready")
    URI readinessUri;

    @Test
    void shouldReturnStarshipsFromRestClient() throws Exception {
        HttpResponse<String> response = get(starshipsUri);

        assertEquals(200, response.statusCode());
        assertEquals("[{\"name\":\"Millennium Falcon\"}]", response.body());
    }

    @Test
    void shouldReportReadyWhenStarWarsClientWorks() throws Exception {
        HttpResponse<String> response = get(readinessUri);

        assertEquals(200, response.statusCode());
        assertUpStatus(response.body());
        assertTrue(response.body().contains("I'm ready"));
    }
}
