package bureau.release.system.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "releases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Release {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "date", nullable = false, columnDefinition = "DATE")
    private LocalDate releaseDate;

    @Column(name = "oci_name", length = 100)
    private String ociName;

    @Column(length = 100)
    private String reference;

    @Column(length = 100)
    private String digest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status", nullable = false)
    private ReleaseStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission", nullable = false)
    private Mission mission;

    @OneToMany(mappedBy = "release", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FirmwareVersion> firmwareVersions = new ArrayList<>();
}
