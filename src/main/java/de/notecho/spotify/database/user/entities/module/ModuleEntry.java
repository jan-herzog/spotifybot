package de.notecho.spotify.database.user.entities.module;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModuleEntry {

    @Id
    @GeneratedValue
    private long id;

    private String key;

    private String value;

    private UserLevel userLevel;

}
