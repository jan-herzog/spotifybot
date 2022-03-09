package de.notecho.spotify.database.user.repository;

import de.notecho.spotify.database.user.entities.TokenPair;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenPairRepository extends JpaRepository<TokenPair, Long> {

}
