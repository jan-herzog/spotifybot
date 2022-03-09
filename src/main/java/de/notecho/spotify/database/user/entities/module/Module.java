package de.notecho.spotify.database.user.entities.module;

import de.notecho.spotify.module.ModuleType;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<ModuleEntry> entries;

    @Enumerated(EnumType.STRING)
    private ModuleType moduleType;

    public ModuleEntry getEntry(String key) {
        return entries.stream().filter(entry -> entry.getEntryKey().equals(key)).findAny().orElse(null);
    }

}
