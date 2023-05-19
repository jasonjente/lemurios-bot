package bot.dataservice.leveling.repositories;

import bot.dataservice.model.CommandExecution;
import bot.dataservice.model.DiscordServer;
import bot.dataservice.model.HistoryEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryEntryRepository extends CrudRepository<HistoryEntry,Long> {
    List<HistoryEntry> findOrderedByDiscordServerOrderByEntryId(DiscordServer server);

    List<HistoryEntry> findHistoryEntryByCommandExecutionAndDiscordServer(CommandExecution commandExecution, DiscordServer discordServer);
}
