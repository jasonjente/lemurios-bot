package bot.dataservice.leveling.repositories;

import bot.dataservice.leveling.model.DiscordServer;
import bot.dataservice.leveling.model.HistoryEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryEntryRepository extends CrudRepository<HistoryEntry,Long> {
    List<HistoryEntry> findOrderedByDiscordServerOrderByEntryId(DiscordServer server);
}
