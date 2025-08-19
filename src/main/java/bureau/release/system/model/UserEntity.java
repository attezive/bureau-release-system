package bureau.release.system.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
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
