package bot.dataservice.leveling.repositories;

import bot.dataservice.model.BotCommand;
import bot.dataservice.model.CommandExecution;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandExecutionRepository extends CrudRepository<CommandExecution, Long> {
    List<CommandExecution> findAllByCommand(BotCommand botCommand);
}
