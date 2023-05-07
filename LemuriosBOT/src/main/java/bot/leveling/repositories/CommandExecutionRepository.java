package bot.leveling.repositories;

import bot.leveling.model.CommandExecution;
import org.springframework.data.repository.CrudRepository;

public interface CommandExecutionRepository extends CrudRepository<CommandExecution, Long> {
}
