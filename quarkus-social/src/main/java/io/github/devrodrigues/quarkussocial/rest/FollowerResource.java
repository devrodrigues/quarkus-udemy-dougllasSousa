package io.github.devrodrigues.quarkussocial.rest;

import io.github.devrodrigues.quarkussocial.domain.model.Follower;
import io.github.devrodrigues.quarkussocial.domain.repository.FollowerRepository;
import io.github.devrodrigues.quarkussocial.domain.repository.UserRepository;
import io.github.devrodrigues.quarkussocial.rest.dto.FollowerRequest;
import io.github.devrodrigues.quarkussocial.rest.dto.FollowerResponse;
import io.github.devrodrigues.quarkussocial.rest.dto.FollowersPerUserResponse;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private final FollowerRepository repository;
    private final UserRepository userRepository;

    @Inject
    public FollowerResource(FollowerRepository repository, UserRepository userRepository){
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @PUT
    @Transactional
    public Response followerUser(@PathParam("userId") Long userId, FollowerRequest request){
        //usuario não segue a si mesmo
        if(userId.equals(request.getFollowerId())){
            return Response.status(Response.Status.CONFLICT)
                    .entity("não é possível seguir a si mesmo")
                    .build();
        }

        var user = userRepository.findById(userId);

        if (user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var follower = userRepository.findById(request.getFollowerId());

        boolean follows = repository.follows(follower, user);
        //se não seguir
        if(!follows){
            var entity = new Follower();
            entity.setUser(user);
            entity.setFollower(follower);

            repository.persist(entity);
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.status(Response.Status.OK).entity("já é seguidor").build();

    }

    @GET
    public Response listFollowers(@PathParam("userId") Long userId){
        var user = userRepository.findById(userId);

        if (user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }


        var list = repository.findByUser(userId);
        FollowersPerUserResponse responseObject = new FollowersPerUserResponse();
        responseObject.setFollowersCount(list.size());

        //mapeando para uma lista de FollowerResponse
        var followerList = list.stream()
                .map(FollowerResponse::new)
                .collect(Collectors.toList());

        responseObject.setContent(followerList);
        return Response.ok(responseObject).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser(
            @PathParam("userId") Long userId,
            @QueryParam("followerId") Long followerId){
        var user = userRepository.findById(userId);

        if (user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        long deleted = repository.deleteByFollowerAndUser(followerId, userId);

        if (deleted == 0) {
            // Não existia relação para deletar
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        repository.deleteByFollowerAndUser(followerId, userId);

        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
