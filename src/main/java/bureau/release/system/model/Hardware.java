package bureau.release.system.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "hardware")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hardware {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToMany(mappedBy = "hardwareSet")
    private Set<Mission> missions = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "firmware_to_hardware",
            joinColumns = @JoinColumn(name = "hardware_id"),
            inverseJoinColumns = @JoinColumn(name = "firmware_id")
    )
    private Set<Firmware> firmwareSet = new HashSet<>();
}