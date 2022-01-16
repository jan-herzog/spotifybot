package de.notecho.spotify.database.user.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenPair {

    @Id
    @GeneratedValue
    private long id;

    private String accessToken;

    private String refreshToken;

}
