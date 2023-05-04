package bot.leveling.repositories;

import bot.leveling.model.HistoryEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryEntryRepository extends CrudRepository<HistoryEntry,Long> {
}
