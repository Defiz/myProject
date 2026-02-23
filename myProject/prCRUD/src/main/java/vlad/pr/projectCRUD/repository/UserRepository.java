package vlad.pr.projectCRUD.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vlad.pr.projectCRUD.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByName(String userName);

    Optional<User> findByTgChatId(Integer tgChatId);

    boolean existsByName(String name);

    @Query("select un.user FROM UserNotification un " +
            "where un.nextNotificationUnix IS NULL OR un.nextNotificationUnix <= :now")
    List<User> findAllUsersWithNotificationDue(@Param("now") long now);
}
