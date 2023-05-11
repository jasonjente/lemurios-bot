package bot.dataservice.leveling.repositories;

import bot.dataservice.leveling.model.BotCommand;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotCommandRepository extends CrudRepository<BotCommand, Long> {
}
