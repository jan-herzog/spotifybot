package de.notecho.spotify.database.user.entities;

import de.notecho.spotify.database.user.entities.module.Module;
import de.notecho.spotify.module.TokenType;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BotUser {

    @Id @GeneratedValue
    private long id;

    private String twitchId;

    @OneToMany
    private List<Module> modules;

    @OneToMany
    private List<TokenPair> tokenPairs;

    public TokenPair twitchTokens() {
        return tokenPairs.stream().filter(tokenPair -> tokenPair.getTokenType().equals(TokenType.TWITCH)).findAny().orElse(null);
    }

    public TokenPair spotifyTokens() {
        return tokenPairs.stream().filter(tokenPair -> tokenPair.getTokenType().equals(TokenType.SPOTIFY)).findAny().orElse(null);
    }

    public TokenPair chatAccountTokens() {
        return tokenPairs.stream().filter(tokenPair -> tokenPair.getTokenType().equals(TokenType.CHATACCOUNT)).findAny().orElse(null);
    }

}
