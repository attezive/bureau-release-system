package bureau.release.system.dal;

import bureau.release.system.model.ReleaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReleaseStatusDao extends JpaRepository<ReleaseStatus, Integer> {
    @Query("SELECT rs FROM ReleaseStatus rs WHERE rs.name = :name")
    Optional<ReleaseStatus> findByName(@Param("name") String name);
}
