package io.github.devrodrigues.quarkussocial.rest;

import io.github.devrodrigues.quarkussocial.rest.dto.CreateUserRequest;
import io.github.devrodrigues.quarkussocial.rest.dto.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    //para evitar repetição de código
    @TestHTTPResource("/users")
    URL apiURL;

    @Test
    @DisplayName("deve criar um usuário com sucesso")
    @Order(1)
    public void createUserTest(){
        var user = new CreateUserRequest();
        user.setName("Fulano");
        user.setAge(30);

        //fazendo a requisição do teste
        var response = given().contentType(ContentType.JSON).body(user)
                        .when().post(apiURL)
                        .then().extract().response();

        //asserts
        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().getString("id"));
    }

    @Test
    @DisplayName("deve retornar erro quando o JSON não for válido")
    @Order(2)
    public void createUserValidationErrorTest(){
        var user = new CreateUserRequest();
        user.setName(null);
        user.setAge(null);

        var response = given().contentType(ContentType.JSON).body(user)
                        .when().post("/users")
                        .then().extract().response();

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));

        List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(1).get("message"));
        /* caso queira especificar a mensagem esperada:
        assertEquals("Name is Required", errors.get(0).get("message"));
        assertEquals("Age is Required", errors.get(1).get("message"));
         */
    }

    @Test
    @DisplayName("deve listar todos os usuários")
    @Order(3)
    public void listAllUsersTest(){

        given().contentType(ContentType.JSON)
                .when().get(apiURL)
                .then().statusCode(200)
                .body("size()", Matchers.is(1));

    }
  
}