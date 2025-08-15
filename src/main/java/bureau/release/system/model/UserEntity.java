package bureau.release.system.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    private String username;

    private String password;

    private Boolean enabled;

    @JoinColumn(name = "username")
    @OneToMany(fetch = FetchType.EAGER)
    private List<Authority> authorities = new ArrayList<>();
}
