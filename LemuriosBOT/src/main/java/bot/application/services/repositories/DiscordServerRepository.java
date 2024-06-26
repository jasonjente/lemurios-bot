package bot.application.services.repositories;

import bot.application.services.model.DiscordServer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscordServerRepository extends CrudRepository<DiscordServer, Long> {
    DiscordServer findDiscordServerByGuildId(String guildId);
    Boolean existsByGuildId(String guildId);
}
