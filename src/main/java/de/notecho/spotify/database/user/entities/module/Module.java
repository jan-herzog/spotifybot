package de.notecho.spotify.database.user.entities.module;

import de.notecho.spotify.module.ModuleType;
import lombok.*;

import javax.persistence.*;
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

    @OneToMany(cascade = CascadeType.ALL)
    private List<ModuleEntry> entries;

    @Enumerated(EnumType.STRING)
    private ModuleType moduleType;

    public ModuleEntry getEntry(String key) {
        return entries.stream().filter(entry -> entry.getEntryKey().equals(key)).findAny().orElse(null);
    }

}
