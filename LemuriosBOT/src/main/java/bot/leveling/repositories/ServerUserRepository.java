package bot.leveling.repositories;

import bot.leveling.model.DiscordServer;
import bot.leveling.model.ServerUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServerUserRepository extends CrudRepository<ServerUser, Long> {
    Boolean existsServerUserByTagAndServer(String tag, DiscordServer discordServer);
    ServerUser findServerUserByTagAndServer(String tag, DiscordServer server);
    List<ServerUser> findAllByServer(DiscordServer server);
}
