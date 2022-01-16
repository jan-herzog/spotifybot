package de.notecho.spotify.database.user.entities;

import de.notecho.spotify.database.user.entities.module.Module;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id @GeneratedValue
    private long id;

    private String twitchId;

    @OneToMany
    private List<Module> modules;

}
