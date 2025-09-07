package io.github.devrodrigues.quarkussocial.rest;

import io.github.devrodrigues.quarkussocial.domain.model.Follower;
import io.github.devrodrigues.quarkussocial.domain.model.User;
import io.github.devrodrigues.quarkussocial.domain.repository.FollowerRepository;
import io.github.devrodrigues.quarkussocial.domain.repository.UserRepository;
import io.github.devrodrigues.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long followerId;

    @BeforeEach
    @Transactional
    void setUp(){
        //usuário de teste
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();

        //seguidor
        var follower = new User();
        follower.setAge(31);
        follower.setName("Cicrano");
        userRepository.persist(follower);
        followerId = follower.getId();

        var followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followerRepository.persist(followerEntity);

    }

    @Test
    @DisplayName("deve retornar 409 quando followerId é igual à id de user")
    //@Order(1)
    public void sameUserAsFollowerTest(){
        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given().contentType(ContentType.JSON).body(body).pathParam("userId", userId)
                .when().put().then().statusCode(Response.Status.CONFLICT.getStatusCode())
                .body(Matchers.is("não é possível seguir a si mesmo"));
    }

    @Test
    @DisplayName("deve retornar 404 ao tentar seguir um usuário que não existe")
    //@Order(2)
    public void userNotFoundWhenTryingToFollowTest(){
        var body = new FollowerRequest();
        body.setFollowerId(userId);

        var inexistentUserId = 9999;

        given().contentType(ContentType.JSON).body(body).pathParam("userId", inexistentUserId)
                .when().put().then().statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("deve seguir um usuário")
    //@Order(3)
    public void followerUserTest(){
        var body = new FollowerRequest();
        body.setFollowerId(followerId);

        given().contentType(ContentType.JSON).body(body).pathParam("userId", userId)
                .when().put().then().statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    @DisplayName("deve retornar 404 ao listar os seguidores de um usuário que não existe")
    //@Order(4)
    public void userNotFoundWhenListingFollowersTest(){
        var inexistentUserId = 9999;

        given().contentType(ContentType.JSON).pathParam("userId", inexistentUserId)
                .when().then().statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("deve retornar uma lista de seguidores do usuário")
    //@Order(5)
    public void listFollowersTest(){
        var response = given().contentType(ContentType.JSON).pathParam("userId", userId)
                        .when().get().then().extract().response();

        var followersCount = response.jsonPath().get("followersCount");
        var followerContent = response.jsonPath().getList("content");

        assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
        assertEquals(1, followersCount);
        assertEquals(1, followerContent.size());
    }

    @Test
    @DisplayName("deve retornar 404 ao deixar de seguir um usuário")
    //@Order(4)
    public void userNotFoundWhenUnfolllowAUserTest(){
        var inexistentUserId = 9999;

        given()
                .pathParam("userId", inexistentUserId)
                .queryParam("followerId", followerId)
                .when().delete().then().statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("deve deixar de seguir um usuário")
    //@Order(4)
    public void unfollowerUserTest(){
        given()
                .pathParam("userId", userId)
                .queryParam("followerId", followerId)
                .when().delete().then().statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

}