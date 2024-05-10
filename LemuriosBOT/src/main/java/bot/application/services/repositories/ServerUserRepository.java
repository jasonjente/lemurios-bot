package bot.application.services.repositories;

import bot.application.services.model.ServerUser;
import bot.application.services.model.DiscordServer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServerUserRepository extends CrudRepository<ServerUser, Long> {
    Boolean existsServerUserByTagAndServer(String tag, DiscordServer discordServer);
    ServerUser findServerUserByTagAndServer(String tag, DiscordServer server);
    List<ServerUser> findAllByServer(DiscordServer server);

    List<ServerUser> findOrderedByServerOrderByPointsDesc(DiscordServer server);
}
