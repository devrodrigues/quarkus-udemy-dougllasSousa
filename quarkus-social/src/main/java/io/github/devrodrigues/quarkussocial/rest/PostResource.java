package io.github.devrodrigues.quarkussocial.rest;

import io.github.devrodrigues.quarkussocial.domain.model.Post;
import io.github.devrodrigues.quarkussocial.domain.model.User;
import io.github.devrodrigues.quarkussocial.domain.repository.FollowerRepository;
import io.github.devrodrigues.quarkussocial.domain.repository.PostRepository;
import io.github.devrodrigues.quarkussocial.domain.repository.UserRepository;
import io.github.devrodrigues.quarkussocial.rest.dto.CreatePostRequest;
import io.github.devrodrigues.quarkussocial.rest.dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

//veja que um post não existe sem um usuário
@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private final UserRepository userRepository;
    private final PostRepository repository;
    private final FollowerRepository followerRepository;

    @Inject
    public PostResource(UserRepository userRepository,
                        PostRepository repository,
                        FollowerRepository followerRepository){
        this.userRepository = userRepository;
        this.repository = repository;
        this.followerRepository = followerRepository;
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest request){
        User user = userRepository.findById(userId);

        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setText(request.getText());
        //post.setDataTime(LocalDateTime.now()); -> criamos o metodo prePersist para isso
        post.setUser(user);

        repository.persist(post);

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response listPosts(
            @PathParam("userId") Long userId,
            @HeaderParam("followerId") Long followerId){

        User user = userRepository.findById(userId);

        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if(followerId == null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("followerId não enviado").build();
        }

        User follower = userRepository.findById(followerId);

        if(follower == null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("followerId inexistente").build();
        }


        boolean follows = followerRepository.follows(follower, user);

        if(!follows){
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Você não pode acessar esses posts.")
                    .build();
        }

        //sort.by retorna os posts mais recentes primeiro
        var query = repository.find("user", Sort.by("dateTime", Sort.Direction.Descending),user);

        var list = query.list();

        var postResponseList = list.stream()
                //.map(post -> PostResponse.fromEntity(post))
                .map(PostResponse::fromEntity) //method reference
                .collect(Collectors.toList());

        return Response.ok(postResponseList).build();
    }

}
