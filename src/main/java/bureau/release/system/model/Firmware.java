package bureau.release.system.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "firmware")
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
    private FirmwareType firmwareType;

    @ManyToMany(mappedBy = "firmwareList")
    private List<Hardware> hardwareList =  new ArrayList<>();
}
