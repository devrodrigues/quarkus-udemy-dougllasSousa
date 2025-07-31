package io.github.devrodrigues.quarkussocial.rest.dto;

public class FieldError {

    private String field; //qual campo que deu erro?
    private String message; //qual foi o erro?

    public FieldError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
