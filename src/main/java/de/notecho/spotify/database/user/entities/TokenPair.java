package de.notecho.spotify.database.user.entities;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenPair {

    @Id
    @GeneratedValue
    private long id;

    private String accessToken;

    private String refreshToken;

}
