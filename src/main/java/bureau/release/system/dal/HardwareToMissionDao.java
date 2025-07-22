package bureau.release.system.dal;

import bureau.release.system.model.Hardware;
import bureau.release.system.model.HardwareToMission;
import bureau.release.system.model.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HardwareToMissionDao extends JpaRepository<HardwareToMission, Long> {

    @Query("SELECT l.hardware FROM HardwareToMission l WHERE l.mission.id = :missionId")
    List<Hardware> findHardwareByMissionId(@Param("missionId") Integer missionId);

    @Query("SELECT l.mission FROM HardwareToMission l WHERE l.hardware.id = :hardwareId")
    List<Mission> findMissionsByHardwareId(@Param("hardwareId") Long hardwareId);
}
