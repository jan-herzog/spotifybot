package de.notecho.spotify.database.user.entities.module;

import de.notecho.spotify.module.UserLevel;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleEntry {

    @Id
    @GeneratedValue
    private long id;

    private String entryKey;

    private String entryValue;

    @Enumerated(EnumType.STRING)
    private UserLevel userLevel;

}
