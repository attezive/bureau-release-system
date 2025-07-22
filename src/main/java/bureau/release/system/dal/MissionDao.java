package bureau.release.system.dal;

import bureau.release.system.model.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionDao extends JpaRepository<Mission, Integer> {
}
