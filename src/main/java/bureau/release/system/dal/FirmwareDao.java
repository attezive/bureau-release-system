package bureau.release.system.dal;

import bureau.release.system.model.Firmware;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FirmwareDao extends JpaRepository<Firmware, Long> {
}
