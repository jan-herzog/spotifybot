package de.notecho.spotify.database.user.entities;

import de.notecho.spotify.module.TokenType;
import lombok.*;

import javax.persistence.*;

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

    @Column(length = 100000)
    private String accessToken;

    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

}
