package io.github.devrodrigues.quarkussocial.rest;

import io.github.devrodrigues.quarkussocial.domain.model.Follower;
import io.github.devrodrigues.quarkussocial.domain.model.Post;
import io.github.devrodrigues.quarkussocial.domain.model.User;
import io.github.devrodrigues.quarkussocial.domain.repository.FollowerRepository;
import io.github.devrodrigues.quarkussocial.domain.repository.PostRepository;
import io.github.devrodrigues.quarkussocial.domain.repository.UserRepository;
import io.github.devrodrigues.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;
    @Inject
    PostRepository postRepository;

    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;

    @BeforeEach
    @Transactional
    public void setUP(){
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();

        //postagem do usuário
        Post post = new Post();
        post.setText("Olá");
        post.setUser(user);
        postRepository.persist(post);

        var userNotFollower = new User();
        userNotFollower.setAge(28);
        userNotFollower.setName("Cicrano");
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        var userFollower = new User();
        userFollower.setAge(29);
        userFollower.setName("Beltrano");
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);

    }

    @Test
    @DisplayName("deve criar um post para um usuário")
    public void createPostTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        given().contentType(ContentType.JSON).body(postRequest)
                .pathParam("userId", userId)
                .when().post().then().statusCode(201);
    }

    @Test
    @DisplayName("deve retornar 404 quando tentar fazer um post para um usuário inexistente")
    public void postForAnInexistentUserTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        //user inexistente na base
        var inexistenteUserId = 999;

        given().contentType(ContentType.JSON).body(postRequest)
                .pathParam("userId", inexistenteUserId)
                .when().post().then().statusCode(404);
    }

    @Test
    @DisplayName("deve retornar 404 quando usuário não existir")
    public void listPostUserNotFoundTest(){
        var inexistentUserId = 999;

        given().pathParam("userId", inexistentUserId)
                .when().get().then().statusCode(404);
    }

    @Test
    @DisplayName("deve retornar 400 quando followerId no header não estiver presente")
    public void listPostFollowerHeaderNotSentTest(){
        given().pathParam("userId", userId)
                .when().get().then().statusCode(400)
                .body(Matchers.is("followerId não enviado"));
    }

    @Test
    @DisplayName("deve retornar 400 quando o seguidor não existir")
    public void listPostFollowerNotFoundTest(){
        var inexistentFollowerId = 999;

        given().pathParam("userId", userId)
                .header("followerId", inexistentFollowerId)
                .when().get().then().statusCode(400)
                .body(Matchers.is("followerId inexistente"));
    }

    @Test
    @DisplayName("deve retornar 403 quando follower recebido não for seguidor do usuário")
    public void listPostNotAFollower(){
        given().pathParam("userId", userId)
                .header("followerId", userNotFollowerId)
                .when().get().then().statusCode(403)
                .body(Matchers.is("Você não pode acessar esses posts."));
    }

    @Test
    @DisplayName("deve retornar os posts")
    public void listPostsTest(){
        given().pathParam("userId", userId)
                .header("followerId", userFollowerId)
                .when().get().then().statusCode(200)
                .body("size()", Matchers.is(1));
    }

}