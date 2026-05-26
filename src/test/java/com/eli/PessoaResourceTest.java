package com.eli;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class PessoaResourceTest extends HttpTestSupport {

    private static final Pattern ID_PATTERN = Pattern.compile("\"id\"\\s*:\\s*(\\d+)");

    @TestHTTPResource("/api/v1/pessoa")
    URI pessoaUri;

    @BeforeEach
    @Transactional
    void cleanDatabase() {
        Pessoa.deleteAll();
    }

    @Test
    void shouldListPessoas() throws Exception {
        createPessoa("Ana", 1998);

        HttpResponse<String> response = get(pessoaUri);

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"nome\":\"Ana\""));
        assertTrue(response.body().contains("\"anoNascimento\":1998"));
    }
    @Test
    void shouldListAllPessoas() throws Exception {
        createPessoa("Ana", 1998);
        createPessoa("Pedro", 1998);
        createPessoa("Joao", 1999);

        HttpResponse<String> response = get(pessoaUri);

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"nome\":\"Ana\""));
        assertTrue(response.body().contains("\"nome\":\"Pedro\""));
        assertTrue(response.body().contains("\"nome\":\"Joao\""));

    }

    @Test
    void shouldListPessoasByAnoNascimento() throws Exception {
        createPessoa("Ana", 1998);
        createPessoa("Bia", 2001);

        HttpResponse<String> response = get(URI.create(pessoaUri + "/ano" + "?anoNascimento=1998"));

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"nome\":\"Ana\""));
        assertTrue(response.body().contains("\"anoNascimento\":1998"));
        assertFalse(response.body().contains("\"nome\":\"Bia\""));
    }

    @Test
    void shouldCreatePessoa() throws Exception {
        HttpResponse<String> created = createPessoa("Ana", 1998);

        assertEquals(200, created.statusCode());
        assertTrue(created.body().contains("\"nome\":\"Ana\""));
        assertTrue(created.body().contains("\"anoNascimento\":1998"));
        extractId(created.body());
    }

    @Test
    void shouldUpdatePessoa() throws Exception {
        long id = extractId(createPessoa("Ana", 1998).body());

        HttpResponse<String> updated = put(pessoaUri, "{\"id\":" + id + ",\"nome\":\"Bia\",\"anoNascimento\":2001}", "application/json");

        assertEquals(200, updated.statusCode());
        assertTrue(updated.body().contains("\"nome\":\"Bia\""));
        assertTrue(updated.body().contains("\"anoNascimento\":2001"));
        assertFalse(updated.body().contains("\"nome\":\"Ana\""));
    }

    @Test
    void shouldDeletePessoa() throws Exception {
        long id = extractId(createPessoa("Ana", 1998).body());

        HttpResponse<String> deleted = delete(pessoaUri, String.valueOf(id), "application/json");

        assertEquals(204, deleted.statusCode());
        assertEquals("[]", get(pessoaUri).body());
    }

    private HttpResponse<String> createPessoa(String nome, int anoNascimento) throws Exception {
        return post(pessoaUri, "{\"nome\":\"" + nome + "\",\"anoNascimento\":" + anoNascimento + "}", "application/json");
    }

    private long extractId(String body) {
        var matcher = ID_PATTERN.matcher(body);
        assertTrue(matcher.find(), "Response body should contain an id: " + body);
        return Long.parseLong(matcher.group(1));
    }
}
