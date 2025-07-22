package bureau.release.system.dal;

import bureau.release.system.model.Hardware;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HardwareDao extends JpaRepository<Hardware, Long> {
}
