package bot.dataservice.leveling.repositories;

import bot.dataservice.leveling.model.CommandExecution;
import bot.dataservice.leveling.model.DiscordServer;
import bot.dataservice.leveling.model.HistoryEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryEntryRepository extends CrudRepository<HistoryEntry,Long> {

    List<HistoryEntry> getAllByDiscordServerOrderByEntryIdDesc(DiscordServer discordServer);

    List<HistoryEntry> getHistoryEntriesByCommandExecutionAndDiscordServerOrderByEntryIdDesc(CommandExecution commandExecution, DiscordServer discordServer);

    List<HistoryEntry> findHistoryEntriesByCommandExecutionStartingWith(String startingWith);
}
