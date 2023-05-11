package bot.dataservice.leveling.repositories;

import bot.dataservice.leveling.model.BotCommand;
import bot.dataservice.leveling.model.CommandExecution;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommandExecutionRepository extends CrudRepository<CommandExecution, Long> {
    List<CommandExecution> findAllByCommand(BotCommand botCommand);
}
