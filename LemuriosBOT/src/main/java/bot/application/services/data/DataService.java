package bot.application.services.data;

import bot.application.constants.Commands;
import bot.application.services.repositories.*;
import bot.application.services.model.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataService.class);

    /**
     * Server user repository.
     */
    private final ServerUserRepository serverUserRepository;

    /**
     * Discord server repository.
     */
    private final DiscordServerRepository discordServerRepository;

    /**
     * Custom link repository, used for persisting youtube links.
     */
    private final CustomLinkRepository customLinkRepository;

    public DataService(final ServerUserRepository serverUserRepository,
                       final DiscordServerRepository discordServerRepository,
                       final CustomLinkRepository customLinkRepository) {
        this.serverUserRepository = serverUserRepository;
        this.discordServerRepository = discordServerRepository;
        this.customLinkRepository = customLinkRepository;
    }

    /**
     * This method creates a server user entity and persists if it doesn't exist or adds the points earned if it
     * already exists.
     * @param tag           The caller tag.
     * @param discordServer The discord server object.
     * @param pointsEarned  The points earned by the command invocation.
     */
    public void createServerUserObject(final String tag,final DiscordServer discordServer,
                                             final Integer pointsEarned) {
        LOGGER.debug("createServerUserObject() - caller tag: {}, server: {}, points earned: {}"
                , tag, discordServer.getGuildId(), pointsEarned );
        ServerUser ret;
        if (Boolean.FALSE.equals(serverUserRepository.existsServerUserByTagAndServer(tag,discordServer))){
            ret = new ServerUser();
            ret.setServer(discordServer);
            ret.setPoints(pointsEarned);
            ret.setTag(tag);
        }else {
            ret = serverUserRepository.findServerUserByTagAndServer(tag, discordServer);
            ret.setPoints(ret.getPoints() + pointsEarned);
        }
        ret.setLevel(calculateLevel(ret));
        serverUserRepository.save(ret);
        LOGGER.debug(
                "createServerUserObject() - LEAVE: saving tag: {}, discord server: {}, points earned: {}, userId: {}",
                    tag, discordServer.getGuildId(), pointsEarned, ret.getId());
    }

    /**
     * This method updates the level value of the server user for a given amount of points earned.
     * The level value is calculated using the square root of the points.
     *
     * @param ret The server user that will be updated.
     * @return the level that the player
     */
    public int calculateLevel(final ServerUser ret) {
        LOGGER.info("calculateLevel() - ENTER - points: {}", ret.getPoints());
        int level = (int) Math.floor(Math.sqrt((ret.getPoints() + 1)));
        //both checks are in case the serveruser is a new user that hasn't been persisted yet
        if (ret.getLevel()==null){
            ret.setLevel(0);
        }
        if (ret.getPoints()==null){
            ret.setPoints(1);
        }
        if (ret.getLevel() != level){
            ret.setLevel(level);
            serverUserRepository.save(ret);
        }
        LOGGER.info("calculateLevel() - LEAVE - points: {}, level: {}", ret.getPoints(), level);
        return level;
    }

    /**
     * This method creates a discord server object if it doesn't exist.
     *
     * @param event the slash command interaction event that contains the information about the guild.
     * @return the discord server that either exists or is created.
     */
    public DiscordServer findOrCreateDiscordServerObject(final SlashCommandInteractionEvent event) {
        LOGGER.debug("createDiscordServerObject() - ENTER");
        DiscordServer ret;
        if (Boolean.FALSE.equals(discordServerRepository.existsByGuildId(event.getGuild().getId()))){
            ret = new DiscordServer();
            ret.setGuildId(event.getGuild().getId());
            discordServerRepository.save(ret);
        }else {
            ret = discordServerRepository.findDiscordServerByGuildId(event.getGuild().getId());
        }
        LOGGER.debug("createDiscordServerObject() - LEAVE - GuildId: {}, discordId: {}", event.getGuild().getId(), ret.getId());
        return ret;
    }

    /**
     * This method deletes a custom link for a given guild identifier.
     *
     * @param guildId The guild identifier.
     */
    public void deleteCustomLinksByDiscordServer(final String guildId) {
        LOGGER.debug("deleteCustomLinksByDiscordServer() - ENTER - GuildId {}", guildId);
        customLinkRepository.deleteCustomLinksByDiscordServer(guildId);        
        LOGGER.debug("deleteCustomLinksByDiscordServer() - LEAVE");
    }

    /**
     * This method deletes a custom link for a given guild identifier and a genre.
     *
     * @param guildId The guild identifier.
     * @param genre   The associated genre.
     */
    public void deleteCustomLinkByDiscordServerAndGenre(final String guildId, final String genre) {
        LOGGER.debug("deleteCustomLinkByDiscordServerAndGenre() - ENTER - Genre: {}, GuildID: {}", genre, guildId);
        customLinkRepository.deleteCustomLinkByDiscordServerAndGenre(guildId, genre);
        LOGGER.debug("deleteCustomLinkByDiscordServerAndGenre() - LEAVE");
    }

    /**
     * This method creates or updates a custom link.
     * @param customLink the custom link that will be saved.
     */
    public void saveCustomLink(final CustomLink customLink) {
        LOGGER.debug("saveCustomLink() - ENTER - Genre: {}, GuildID: {}", customLink.getGenre(), customLink.getDiscordServer());
        customLinkRepository.save(customLink);
        LOGGER.debug("saveCustomLink() - LEAVE - Genre: {}, GuildID: {}", customLink.getGenre(), customLink.getDiscordServer());
    }

    /**
     * This method finds a custom link for a discord server and a genre.
     *
     * @param id    The guild id.
     * @param genre The associated genre.
     * @return      The Custom link that matches the search criteria.
     */
    public CustomLink findCustomLinkByDiscordServerAndGenre(final String id, final String genre) {
        LOGGER.debug("findCustomLinkByDiscordServerAndGenre() - ENTER - Genre: {}, GuildID: {}", genre, id);
        var ret = customLinkRepository.findCustomLinkByDiscordServerAndGenre(id, genre);
        if (ret!=null){
            LOGGER.debug("findCustomLinkByDiscordServerAndGenre() - LEAVE - Genre: {}, GuildID: {}, id: {}", genre, id, ret.getId());
        }else {
            LOGGER.debug("No entry found for genre: {}", genre);
        }
        return ret;
    }

    /**
     * This method returns a list of custom links for a given guild identifier.
     *
     * @param guildId The guild identifier.
     * @return List of Custom Links.
     */
    public List<CustomLink> findCustomLinksByDiscordServer(final String guildId) {
        LOGGER.debug("findCustomLinkByDiscordServerAndGenre() - ENTER - GuildID: {}",guildId);
        var ret = customLinkRepository.getCustomLinksByDiscordServer(guildId);
        LOGGER.debug("findCustomLinkByDiscordServerAndGenre() - LEAVE - GuildID: {}, Custom links found: {}", guildId, ret.size());
        return ret;
    }

    /**
     * This class is used as a reverse look up map for the commands of the application.
     */
    public static class CommandsReverseLookup {
        private CommandsReverseLookup(){}
        private static final Map<String, Commands> reverseLookupMap = new HashMap<>();

        static {
            for (var command : Commands.values()) {
                reverseLookupMap.put(command.getCommandName(), command);
            }
        }

        public static Commands getCommand(final String commandValue) {
            return reverseLookupMap.get(commandValue);
        }
    }
}
