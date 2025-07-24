package bureau.release.system.dal;

import bureau.release.system.model.FirmwareType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FirmwareTypeDao extends JpaRepository<FirmwareType, Integer> {
    @Query("SELECT ft.id FROM FirmwareType ft WHERE ft.name = :name")
    Optional<Integer> findIdByName(@Param("name") String name);
}
