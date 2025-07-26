package bureau.release.system.dal;

import bureau.release.system.model.FirmwareVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FirmwareVersionDao extends JpaRepository<FirmwareVersion, Long> {
}
