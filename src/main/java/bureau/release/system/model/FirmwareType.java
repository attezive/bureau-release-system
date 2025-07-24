package bureau.release.system.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "firmware_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FirmwareType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Firmware> firmwareList = new ArrayList<>();
}
