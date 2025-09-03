package io.github.devrodrigues.quarkussocial.rest.dto;

import io.github.devrodrigues.quarkussocial.domain.model.Follower;
import lombok.Data;

@Data
public class FollowerResponse {
    private Long id;
    private String name;

    public FollowerResponse(){}

    public FollowerResponse(Follower follower) {
         this(follower.getId(), follower.getFollower().getName());
         //caso queira que retorne o id do seguidor
        //this(follower..getFollower().getId(), follower.getFollower().getName());
    }

    public FollowerResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }


}
