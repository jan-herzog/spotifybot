package de.notecho.spotify.database.user.repository;

import de.notecho.spotify.database.user.entities.BotUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<BotUser, Long> {

    BotUser findByTwitchId(String twitchId);

    boolean existsByTwitchId(String twitchId);

    List<BotUser> findAllByOrderByIdAsc();

}
