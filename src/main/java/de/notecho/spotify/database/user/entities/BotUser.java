package de.notecho.spotify.database.user.entities;

import de.notecho.spotify.database.user.entities.module.Module;
import de.notecho.spotify.module.TokenType;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BotUser {

    @Id
    @GeneratedValue
    private long id;

    private String twitchId;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Module> modules;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
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

    public void addTokenPair(TokenPair tokenPair) {
        this.tokenPairs = new ArrayList<>(tokenPairs);
        this.tokenPairs.add(tokenPair);
    }

}
