package de.notecho.spotify.database.user.repository;

import de.notecho.spotify.database.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    long countAllByIdEqualsAndTestAsdEquals(long id, String sad);


}
