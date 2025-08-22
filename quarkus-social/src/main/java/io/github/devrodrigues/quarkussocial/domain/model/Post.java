package io.github.devrodrigues.quarkussocial.domain.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "posts")
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_text")
    private String text;

    @Column(name = "dateTime")
    private LocalDateTime dateTime;

    //muitas postagens para um usuário
    @ManyToOne
    @JoinColumn(name = "user_id") //pois há relacionamento
    private User user;

    //antes de persistir, executará este método
    @PrePersist
    public void prePersist(){
        setDateTime(LocalDateTime.now());
    }

}
