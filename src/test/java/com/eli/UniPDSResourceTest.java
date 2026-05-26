package com.eli;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class UniPDSResourceTest extends HttpTestSupport {

    @TestHTTPResource("/api/v1/unipds")
    URI unipdsUri;

    @Test
    void shouldUpdateCounter() throws Exception {
        assertEquals(204, put(unipdsUri, "7", "text/plain").statusCode());
        assertEquals("7", get(unipdsUri).body());
    }

    @Test
    void shouldIncrementCounter() throws Exception {
        assertEquals(204, delete(unipdsUri, "", "text/plain").statusCode());
        assertEquals(204, post(unipdsUri, "", "text/plain").statusCode());
        assertEquals("1", get(unipdsUri).body());
    }

    @Test
    void shouldResetCounter() throws Exception {
        assertEquals(204, put(unipdsUri, "7", "text/plain").statusCode());
        assertEquals(204, delete(unipdsUri, "", "text/plain").statusCode());
        assertEquals("0", get(unipdsUri).body());
    }
}
