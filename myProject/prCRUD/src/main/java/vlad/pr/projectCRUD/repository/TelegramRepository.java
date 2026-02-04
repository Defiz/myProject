package vlad.pr.projectCRUD.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vlad.pr.projectCRUD.model.Telegram;

@Repository
public interface TelegramRepository extends JpaRepository<Telegram, Integer> {
}
