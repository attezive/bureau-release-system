package bureau.release.system.model;

import jakarta.persistence.*;
import lombok.*;

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
}
