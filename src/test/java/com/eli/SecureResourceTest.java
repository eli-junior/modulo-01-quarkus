package com.eli;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class SecureResourceTest extends HttpTestSupport {

    private static final String ISSUER = "https://quarkus.io/using-jwt-rbac";

    @TestHTTPResource("/api/v1/secure/claim")
    URI secureClaimUri;

    @Test
    void shouldRejectRequestWithoutToken() throws Exception {
        assertEquals(401, get(secureClaimUri).statusCode());
    }

    @Test
    void shouldRejectTokenWithoutRequiredRole() throws Exception {
        String token = token("jdoe", "Tester");

        assertEquals(403, get(secureClaimUri, token).statusCode());
    }

    @Test
    void shouldReturnPreferredUsernameFromValidToken() throws Exception {
        String token = token("jdoe", "Subscriber");

        var response = get(secureClaimUri, token);

        assertEquals(200, response.statusCode());
        assertEquals("jdoe", response.body());
    }

    private String token(String username, String... groups) throws Exception {
        long now = Instant.now().getEpochSecond();
        String header = "{\"alg\":\"RS256\",\"typ\":\"JWT\"}";
        String payload = "{"
                + "\"iss\":\"" + ISSUER + "\","
                + "\"sub\":\"" + username + "\","
                + "\"upn\":\"" + username + "@quarkus.io\","
                + "\"preferred_username\":\"" + username + "\","
                + "\"groups\":[" + groupsJson(groups) + "],"
                + "\"iat\":" + now + ","
                + "\"exp\":" + (now + 3600)
                + "}";
        String signingInput = base64Url(header.getBytes(StandardCharsets.UTF_8))
                + "."
                + base64Url(payload.getBytes(StandardCharsets.UTF_8));

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey());
        signature.update(signingInput.getBytes(StandardCharsets.UTF_8));

        return signingInput + "." + base64Url(signature.sign());
    }

    private String groupsJson(String... groups) {
        return Stream.of(groups)
                .map(group -> "\"" + group + "\"")
                .collect(Collectors.joining(","));
    }

    private PrivateKey privateKey() throws Exception {
        try (var inputStream = getClass().getResourceAsStream("/secure-test-private.pem")) {
            String pem = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(pem);
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        }
    }

    private String base64Url(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
