package bureau.release.system.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "firmware_to_release")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FirmwareVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "firmware_id", nullable = false)
    private Firmware firmware;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hardware_id")
    private Hardware hardware;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_id", nullable = false)
    private Release release;

    @Column(name = "firmware_version", nullable = false, length = 100)
    private String firmwareVersion;
}
