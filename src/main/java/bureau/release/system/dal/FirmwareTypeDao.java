package bureau.release.system.dal;

import bureau.release.system.model.FirmwareType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FirmwareTypeDao extends JpaRepository<FirmwareType, Integer> {
    @Query("SELECT ft FROM FirmwareType ft WHERE ft.name = :name")
    Optional<FirmwareType> findByName(@Param("name") String name);
}
