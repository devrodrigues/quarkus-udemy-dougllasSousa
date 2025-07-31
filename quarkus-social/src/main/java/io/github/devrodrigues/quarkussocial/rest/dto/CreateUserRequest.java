package io.github.devrodrigues.quarkussocial.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

//classe criada para respeitar o contrato definido na api: objeto criado somente com essas duas informações:
public class CreateUserRequest {

    @NotBlank(message = "Name is Required")
    private String name;
    @NotNull(message = "Age is Required")
    private Integer age;

    public CreateUserRequest(){

    }

    public CreateUserRequest(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}

