package bot.leveling.repositories;

import bot.leveling.model.DiscordServer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscordServerRepository extends CrudRepository<DiscordServer, Long> {
    DiscordServer findDiscordServerByGuildId(String guildId);
    Boolean existsByGuildId(String guildId);
}
