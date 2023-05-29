package bot.services.leveling.repositories;

import bot.services.model.BotCommand;
import bot.services.model.CommandExecution;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandExecutionRepository extends CrudRepository<CommandExecution, Long> {
    List<CommandExecution> findAllByCommand(BotCommand botCommand);
}
