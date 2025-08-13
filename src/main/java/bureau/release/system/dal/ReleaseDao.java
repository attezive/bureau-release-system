package bureau.release.system.dal;

import bureau.release.system.model.Release;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReleaseDao extends JpaRepository<Release, Long> {
    @Query("SELECT r FROM Release r WHERE r.mission.id = :id")
    Page<Release> findByMission(@Param("id") Integer missionId, Pageable pageable);
}
