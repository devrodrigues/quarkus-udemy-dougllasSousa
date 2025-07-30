package io.github.devrodrigues.quarkussocial.rest.dto;

//classe criada para respeitar o contrato definido na api: objeto criado somente com essas duas informações:
public class CreateUserRequest {
    private String name;
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

