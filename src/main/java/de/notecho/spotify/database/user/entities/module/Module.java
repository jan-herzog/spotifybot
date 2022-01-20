package de.notecho.spotify.database.user.entities.module;

import de.notecho.spotify.module.ModuleType;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Module {

    @Id
    @GeneratedValue
    private long id;

    @OneToMany
    private List<ModuleEntry> entries;

    private ModuleType moduleType;



}
