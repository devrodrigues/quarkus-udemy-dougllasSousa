package io.github.devrodrigues.quarkussocial.rest;

import io.github.devrodrigues.quarkussocial.domain.model.User;
import io.github.devrodrigues.quarkussocial.domain.repository.UserRepository;
import io.github.devrodrigues.quarkussocial.rest.dto.CreateUserRequest;
import io.github.devrodrigues.quarkussocial.rest.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Set;


@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private UserRepository repository;
    private Validator validator;

    @Inject
    public UserResource(UserRepository repository, Validator validator){
        this.repository = repository;
        this.validator = validator;
    }

    @POST
    @Transactional
    public Response createUser(CreateUserRequest userRequest) {

        //para que possamos tratar campos inválidos
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);
        if(!violations.isEmpty()){
            /*
            ConstraintViolation<CreateUserRequest> error = violations.stream().findAny().get();
            String errorMessage = error.getMessage();
            */

            return ResponseError.createFromValidation(violations).
                    withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        User user = new User();
        user.setAge(userRequest.getAge());
        user.setName(userRequest.getName());

        //user.persist(); //salva a entidade no BD
        repository.persist(user);

        return Response.status(Response.Status.CREATED.getStatusCode()).entity(user).build();
    }

    @GET
    public Response listAllUsers(){
        //PanacheQuery<User> query = User.findAll();
        PanacheQuery<User> query = repository.findAll();

        return Response.ok(query.list()).build(); //status 200
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id){
        //User user = User.findById(id);
        User user = repository.findById(id);

        if(user != null){
            //user.delete();
            repository.delete(user);

            //return Response.ok().build();
            return Response.noContent().build(); //não preciso de conteúdo se deletei o usuário
        }

        //retorna 404 caso não encontre a id
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userData){
        //User user = User.findById(id);
        User user = repository.findById(id);

        if(user != null){
            user.setName(userData.getName());
            user.setAge(userData.getAge());
            //user.update();
            //repository.update(user); - não é necessário devido ao @Transactional
            return Response.noContent().build();
        }

        //retorna 404 caso não encontre a id
        return Response.status(Response.Status.NOT_FOUND).build();
    }

}
