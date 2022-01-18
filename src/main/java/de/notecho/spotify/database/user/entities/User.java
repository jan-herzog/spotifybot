package de.notecho.spotify.database.user.entities;

import de.notecho.spotify.database.user.entities.module.Module;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id @GeneratedValue
    private long id;

    private String twitchId;

    @OneToMany
    private List<Module> modules;

}
