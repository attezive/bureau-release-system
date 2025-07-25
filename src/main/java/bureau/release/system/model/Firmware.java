package bureau.release.system.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "firmware")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Firmware {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "oci_name", nullable = false, length = 100)
    private String ociName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type", nullable = false)
    private FirmwareType type;

    @ManyToMany(mappedBy = "firmwareSet")
    private Set<Hardware> hardwareSet =  new HashSet<>();
}
