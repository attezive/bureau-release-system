package bureau.release.system.dal;

import bureau.release.system.model.Firmware;
import bureau.release.system.model.Release;
import bureau.release.system.model.ReleaseContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReleaseContentDao extends JpaRepository<ReleaseContent, Long> {
    @Query("SELECT l.firmware FROM ReleaseContent l WHERE l.release.id = :releaseId")
    List<Firmware> findFirmwareByReleaseId(@Param("releaseId") Long releaseId);

    @Query("SELECT l.release FROM ReleaseContent l WHERE l.firmware.id = :firmwareId")
    List<Release> findReleasesByFirmwareId(@Param("firmwareId") Long firmwareId);
}
