package bureau.release.system.dal;

import bureau.release.system.model.Release;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReleaseDao extends JpaRepository<Release, Long> {
}
