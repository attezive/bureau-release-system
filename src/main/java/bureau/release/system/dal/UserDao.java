package bureau.release.system.dal;

import bureau.release.system.model.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserDao extends ListCrudRepository<UserEntity, String> {
    @Query("SELECT a.username FROM Authority a WHERE a.authority = :authority")
    List<String> findNamesByAuthorities(@Param("authority") String authority);
}
