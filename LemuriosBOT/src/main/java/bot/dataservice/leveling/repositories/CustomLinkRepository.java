package bot.dataservice.leveling.repositories;

import bot.dataservice.model.CustomLink;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomLinkRepository extends CrudRepository<CustomLink, Long> {
    CustomLink findCustomLinkByDiscordServerAndGenre(String discordServer, String genre);
    void deleteCustomLinksByDiscordServer(String discordServer);
    void deleteCustomLinkByDiscordServerAndGenre(String discordServer, String genre);
    List<CustomLink> getCustomLinksByDiscordServer(String discordServer);
    CustomLink getCustomLinkByDiscordServerAndGenre(String discordServer, String genre);

}
