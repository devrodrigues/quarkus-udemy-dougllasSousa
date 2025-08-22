package io.github.devrodrigues.quarkussocial.rest.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreatePostRequest {

    private String text;
    private LocalDateTime dataTime;


}
