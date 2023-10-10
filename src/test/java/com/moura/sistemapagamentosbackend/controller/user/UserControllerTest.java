package com.moura.sistemapagamentosbackend.controller.user;

import com.moura.sistemapagamentosbackend.model.user.User;
import com.moura.sistemapagamentosbackend.model.user.UserType;
import com.moura.sistemapagamentosbackend.service.user.UserService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void deveRetornarStatus400QuandoDadosDoPayloadParaCriacaoDoUsuarioEstiveremIncorretos() {
        try {
            URI uri = new URI("http://localhost:8093/api/v1/users");

            JSONObject payload = new JSONObject();
            payload.put("email", "valid@email.com");
            payload.put("password", "654321");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(payload.toString(), headers);

            Map<String, Object> result = testRestTemplate.postForObject(
                    uri,
                    request,
                    Map.class
            );

            assertThat(400).isEqualTo((int) result.get("status"));

            Object aux = result.get("errors");
            assertThat(aux).isNotNull();
            LinkedHashMap<String, Object> errors = (LinkedHashMap<String, Object>) aux;

            List<Object> fieldErrorsList = null;
            LinkedHashMap<String, Object> fieldError = null;

            assertThat(errors.size()).isEqualTo(3);

            assertThat(errors.containsKey("name")).isTrue();
            fieldErrorsList = (List<Object>) errors.get("name");
            fieldError = (LinkedHashMap<String, Object>) fieldErrorsList.get(0);
            assertThat(fieldErrorsList.size()).isEqualTo(1);
            assertThat(fieldError.get("code")).isEqualTo("NotBlank");

            assertThat(errors.containsKey("document")).isTrue();
            fieldErrorsList = (List<Object>) errors.get("document");
            fieldError = (LinkedHashMap<String, Object>) fieldErrorsList.get(0);
            assertThat(fieldErrorsList.size()).isEqualTo(1);
            assertThat(fieldError.get("code")).isEqualTo("NotBlank");

            assertThat(errors.containsKey("type")).isTrue();
            fieldErrorsList = (List<Object>) errors.get("type");
            fieldError = (LinkedHashMap<String, Object>) fieldErrorsList.get(0);
            assertThat(fieldErrorsList.size()).isEqualTo(1);
            assertThat(fieldError.get("code")).isEqualTo("NotNull");

        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }

    @Test
    public void deveRetornarStatus400QuandoTipoDoUsuarioForInvalido() {
        try {
            URI uri = new URI("http://localhost:8093/api/v1/users");

            JSONObject payload = new JSONObject();
            payload.put("name", "user");
            payload.put("document", "user");
            payload.put("type", "user");
            payload.put("email", "invalid@email.invalid");
            payload.put("password", "654321");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(payload.toString(), headers);

            Map<String, Object> result = testRestTemplate.postForObject(
                    uri,
                    request,
                    Map.class
            );

            assertThat(400).isEqualTo((int) result.get("status"));

            Object aux = result.get("errors");
            assertThat(aux).isNotNull();
            LinkedHashMap<String, Object> errors = (LinkedHashMap<String, Object>) aux;

            List<Object> fieldErrorsList = null;
            LinkedHashMap<String, Object> fieldError = null;

            assertThat(errors.size()).isEqualTo(1);

            assertThat(errors.containsKey("type")).isTrue();
            fieldErrorsList = (List<Object>) errors.get("type");
            fieldError = (LinkedHashMap<String, Object>) fieldErrorsList.get(0);
            assertThat(fieldErrorsList.size()).isEqualTo(1);
            assertThat(fieldError.get("code")).isEqualTo("InvalidFormatException");
            assertThat(fieldError.get("rejectedValue")).isEqualTo("user");

        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }

    @Test
    public void deveRetornarStatus400QuandoDocumentoOuEmailDoUsuarioForInvalido() {
        try {
            URI uri = new URI("http://localhost:8093/api/v1/users");

            JSONObject payload = new JSONObject();
            payload.put("name", "user");
            payload.put("document", "user");
            payload.put("type", UserType.COMMON);
            payload.put("email", "invalid@email.invalid");
            payload.put("password", "654321");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(payload.toString(), headers);

            Map<String, Object> result = testRestTemplate.postForObject(
                    uri,
                    request,
                    Map.class
            );

            assertThat(400).isEqualTo((int) result.get("status"));

            Object aux = result.get("errors");
            assertThat(aux).isNotNull();
            LinkedHashMap<String, Object> errors = (LinkedHashMap<String, Object>) aux;

            List<Object> fieldErrorsList = null;
            LinkedHashMap<String, Object> fieldError = null;

            assertThat(errors.size()).isEqualTo(2);

            List<String> codeList = List.of("Length", "Pattern");
            List<String> messageList = List.of("o comprimento deve ser entre 11 e 14", "documento inválido");

            assertThat(errors.containsKey("document")).isTrue();
            fieldErrorsList = (List<Object>) errors.get("document");
            fieldError = (LinkedHashMap<String, Object>) fieldErrorsList.get(0);
            assertThat(fieldErrorsList.size()).isEqualTo(2);
            assertThat(fieldError.get("rejectedValue")).isEqualTo("user");

            assertThat(codeList.contains(fieldError.get("code"))).isTrue();
            assertThat(messageList.contains(fieldError.get("message"))).isTrue();

            fieldError = (LinkedHashMap<String, Object>) fieldErrorsList.get(1);
            assertThat(codeList.contains(fieldError.get("code"))).isTrue();
            assertThat(messageList.contains(fieldError.get("message"))).isTrue();

            assertThat(errors.containsKey("email")).isTrue();
            fieldErrorsList = (List<Object>) errors.get("email");
            assertThat(fieldErrorsList.size()).isEqualTo(1);
            fieldError = (LinkedHashMap<String, Object>) fieldErrorsList.get(0);
            assertThat(fieldError.get("rejectedValue")).isEqualTo("invalid@email.invalid");
            assertThat(fieldError.get("code")).isEqualTo("Pattern");
            assertThat(fieldError.get("message")).isEqualTo("email inválido");

        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }

    @Test
    public void deveRetornarStatus400QuandoForDadosDuplicados() {
        try {
            URI uri = new URI("http://localhost:8093/api/v1/users");

            JSONObject payload = new JSONObject();
            payload.put("name", "user");
            payload.put("document", "12345678000109");
            payload.put("type", UserType.COMMON);
            payload.put("email", "email@email.com");
            payload.put("password", "654321");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(payload.toString(), headers);

            // Request 1
            ResponseEntity<User> result1 = testRestTemplate.postForEntity(
                    uri,
                    request,
                    User.class
            );
            User user = result1.getBody();

            assertThat(HttpStatus.CREATED).isEqualTo(result1.getStatusCode());
            assertThat(user).isNotNull();
            assertThat(user.getName()).isEqualTo("user");
            assertThat(user.getDocument()).isEqualTo("12345678000109");
            assertThat(user.getType()).isEqualTo(UserType.COMMON);
            assertThat(user.getEmail()).isEqualTo("email@email.com");
            assertThat(user.getPassword()).isEqualTo("654321");
            assertThat(user.getBalance()).isNull();

            // Request 2
            Map<String, Object> result2 = testRestTemplate.postForObject(
                    uri,
                    request,
                    Map.class
            );

            assertThat(500).isEqualTo((int) result2.get("status"));

            Object aux = result2.get("errors");
            assertThat(aux).isNotNull();
            LinkedHashMap<String, Object> errors = (LinkedHashMap<String, Object>) aux;

            List<Object> fieldErrorsList = null;
            LinkedHashMap<String, Object> fieldError = null;

            assertThat(errors.size()).isEqualTo(1);

            assertThat(errors.containsKey("internal")).isTrue();
            fieldErrorsList = (List<Object>) errors.get("internal");
            assertThat(fieldErrorsList.size()).isEqualTo(1);
            fieldError = (LinkedHashMap<String, Object>) fieldErrorsList.get(0);
            assertThat(fieldError.get("message")).isEqualTo("CPF/CNPJ e e-mails devem ser únicos no sistema");

        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }
}