package bot.services.leveling.repositories;

import bot.services.model.BotCommand;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotCommandRepository extends CrudRepository<BotCommand, Long> {
}
