package bureau.release.system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Release {
    @Id
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
