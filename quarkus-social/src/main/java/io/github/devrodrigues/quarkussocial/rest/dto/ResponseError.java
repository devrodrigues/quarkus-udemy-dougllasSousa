package io.github.devrodrigues.quarkussocial.rest.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.ws.rs.core.Response;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//objeto de retorno quando o erro ocorre
public class ResponseError {

    public static final int UNPROCESSABLE_ENTITY_STATUS = 422;

    private String message;
    private Collection<FieldError> errors;

    public ResponseError(String message, Collection<FieldError> errors) {
        this.message = message;
        this.errors = errors;
    }

    //cria um ResponseError de qualquer objeto
    public static<T> ResponseError createFromValidation(Set<ConstraintViolation<T>> violations ){
        //cada FieldError é capturado e colocado em uma lista
        List<FieldError> errors = violations
                                    .stream()
                                    .map(cv -> new FieldError(cv.getPropertyPath().toString(), cv.getMessage()))
                                    .collect(Collectors.toList());

        String message = "Validation Error";
        var responseError = new ResponseError(message, errors);
        return responseError;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Collection<FieldError> getErrors() {
        return errors;
    }

    public void setErrors(Collection<FieldError> errors) {
        this.errors = errors;
    }

    //criando status code 422
    public Response withStatusCode(int code){
        return Response.status(code).entity(this).build();
    }

}
