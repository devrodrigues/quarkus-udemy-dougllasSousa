package io.github.devrodrigues.quarkussocial.domain.repository;

import io.github.devrodrigues.quarkussocial.domain.model.Follower;
import io.github.devrodrigues.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//para que possa ser injetado
@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public boolean follows(User follower, User user){
        var params = Parameters.with("follower", follower).and("user", user).map();

        PanacheQuery<Follower> query = find("follower = :follower and user = :user", params);
        Optional<Follower> result = query.firstResultOptional();

        //se já seguir o usuario, retorna true
        return result.isPresent();
    }

    public List<Follower> findByUser(Long userId){
        PanacheQuery<Follower> query = find("user.id", userId);
        return query.list();
    }

    public long deleteByFollowerAndUser(Long followerId, Long userId) {
        var params = Parameters.with("userId", userId).and("followerId", followerId).map();

        //seguidor de id X deixa de seguir usuario de id Y
        return delete("follower.id =: followerId and user.id =: userId", params);
    }
}
