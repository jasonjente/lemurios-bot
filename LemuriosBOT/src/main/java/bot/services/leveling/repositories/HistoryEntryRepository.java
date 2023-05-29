package bot.services.leveling.repositories;

import bot.services.model.CommandExecution;
import bot.services.model.DiscordServer;
import bot.services.model.HistoryEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryEntryRepository extends CrudRepository<HistoryEntry,Long> {
    List<HistoryEntry> findOrderedByDiscordServerOrderByEntryId(DiscordServer server);

    List<HistoryEntry> findHistoryEntryByCommandExecutionAndDiscordServer(CommandExecution commandExecution, DiscordServer discordServer);
}
