package bureau.release.system.dal;

import bureau.release.system.model.ReleaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReleaseStatusDao extends JpaRepository<ReleaseStatus, Integer> {
}
