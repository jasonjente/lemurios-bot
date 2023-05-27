package bot.dataservice;

import bot.constants.Commands;
import bot.dataservice.leveling.repositories.*;
import bot.dataservice.model.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataServiceImpl implements DataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataServiceImpl.class);

    private final ServerUserRepository serverUserRepository;

    private final DiscordServerRepository discordServerRepository;

    private final CommandExecutionRepository commandExecutionRepository;
    private final BotCommandRepository botCommandRepository;
    private final CustomLinkRepository customLinkRepository;

    public DataServiceImpl(ServerUserRepository serverUserRepository, DiscordServerRepository discordServerRepository, CommandExecutionRepository commandExecutionRepository, BotCommandRepository botCommandRepository, CustomLinkRepository customLinkRepository) {
        this.serverUserRepository = serverUserRepository;
        this.discordServerRepository = discordServerRepository;
        this.commandExecutionRepository = commandExecutionRepository;
        this.botCommandRepository = botCommandRepository;
        this.customLinkRepository = customLinkRepository;
    }

    @Override
    public ServerUser createServerUserObject(String tag, DiscordServer discordServer, CommandExecution commandExecution, Integer pointsEarned) {
        LOGGER.info("createServerUserObject() - caller tag: {}, server: {}, command {}, points earned: {}", tag, discordServer.getGuildId(), commandExecution.getCommand().getName(), pointsEarned );
        ServerUser ret;
        if(Boolean.FALSE.equals(serverUserRepository.existsServerUserByTagAndServer(tag,discordServer))){
            ret = new ServerUser();
            ret.setServer(discordServer);
            ret.getCommandExecutions().add(commandExecution);
            ret.setPoints(pointsEarned);
            ret.setTag(tag);
        }else {
            ret = serverUserRepository.findServerUserByTagAndServer(tag, discordServer);
            ret.setPoints(ret.getPoints() + pointsEarned);
        }
        ret.setLevel(calculateLevel(ret));
        serverUserRepository.save(ret);
        LOGGER.info("createServerUserObject() - LEAVE: saving tag: {}, discor derver: {}, points earned: {}, userId: {}", tag, discordServer.getGuildId(), pointsEarned, ret.getId());
        return ret;
    }

    @Override
    public int calculateLevel(ServerUser ret) {
        LOGGER.info("calculateLevel() - ENTER - points: {}", ret.getPoints());
        int level = (int) Math.floor(Math.sqrt((ret.getPoints() + 1)));
        //both checks are in case the serveruser is a new user that hasn't been persisted yet
        if(ret.getLevel()==null){
            ret.setLevel(0);
        }
        if(ret.getPoints()==null){
            ret.setPoints(1);
        }
        if(ret.getLevel() != level){
            ret.setLevel(level);
            serverUserRepository.save(ret);
        }
        LOGGER.info("calculateLevel() - LEAVE - points: {}, level: {}", ret.getPoints(), level);
        return level;
    }

    @Override
    public CommandExecution createCommandExecutionObject(SlashCommandInteractionEvent event) {
        LOGGER.info("createCommandExecutionObject() - ENTER");
        CommandExecution ret = new CommandExecution();
        BotCommand botCommand = new BotCommand();
        botCommand.setName(event.getCommandString());
        botCommandRepository.save(botCommand);
        ret.setExecutedAt(LocalDateTime.now());
        ret.setCommand(botCommand);
        commandExecutionRepository.save(ret);
        LOGGER.info("createCommandExecutionObject() - LEAVE: command execution id: {}", ret.getId());
        return ret;
    }

    @Override
    public DiscordServer createDiscordServerObject(SlashCommandInteractionEvent event) {
        LOGGER.info("createDiscordServerObject() - ENTER");
        DiscordServer ret;
        if(Boolean.FALSE.equals(discordServerRepository.existsByGuildId(event.getGuild().getId()))){
            ret = new DiscordServer();
            ret.setGuildId(event.getGuild().getId());
            discordServerRepository.save(ret);
        }else {
            ret = discordServerRepository.findDiscordServerByGuildId(event.getGuild().getId());
        }
        LOGGER.info("createDiscordServerObject() - LEAVE - GuildId: {}, discordId: {}", event.getGuild().getId(), ret.getId());
        return ret;
    }

    @Override
    public void deleteCustomLinksByDiscordServer(String guildId) {
        LOGGER.info("deleteCustomLinksByDiscordServer() - ENTER - GuildId {}", guildId);
        customLinkRepository.deleteCustomLinksByDiscordServer(guildId);        
        LOGGER.info("deleteCustomLinksByDiscordServer() - LEAVE");
    }

    @Override
    public void deleteCustomLinkByDiscordServerAndGenre(String guildId, String genre) {
        LOGGER.info("deleteCustomLinkByDiscordServerAndGenre() - ENTER - Genre: {}, GuildID: {}", genre, guildId);
        customLinkRepository.deleteCustomLinkByDiscordServerAndGenre(guildId, genre);
        LOGGER.info("deleteCustomLinkByDiscordServerAndGenre() - LEAVE");
    }

    @Override
    public void saveCustomLink(CustomLink customLink) {
        LOGGER.info("saveCustomLink() - ENTER - Genre: {}, GuildID: {}", customLink.getGenre(), customLink.getDiscordServer());
        customLinkRepository.save(customLink);
        LOGGER.info("saveCustomLink() - LEAVE - Genre: {}, GuildID: {}", customLink.getGenre(), customLink.getDiscordServer());
    }

    @Override
    public CustomLink findCustomLinkByDiscordServerAndGenre(String id, String genre) {
        LOGGER.info("findCustomLinkByDiscordServerAndGenre() - ENTER - Genre: {}, GuildID: {}", genre, id);
        CustomLink ret = customLinkRepository.findCustomLinkByDiscordServerAndGenre(id, genre);
        if(ret!=null){
            LOGGER.info("findCustomLinkByDiscordServerAndGenre() - LEAVE - Genre: {}, GuildID: {}, id: {}", genre, id, ret.getId());
        }else {
            LOGGER.info("No entry found for genre: {}", genre);
        }
        return ret;
    }

    @Override
    public List<CustomLink> findCustomLinksByDiscordServerAndGenre(String guildId) {
        LOGGER.info("findCustomLinkByDiscordServerAndGenre() - ENTER - GuildID: {}",guildId);
        List<CustomLink> ret = customLinkRepository.getCustomLinksByDiscordServer(guildId);
        LOGGER.info("findCustomLinkByDiscordServerAndGenre() - LEAVE - GuildID: {}, Custom links found: {}", guildId, ret.size());
        return ret;
    }

    public static class CommandsReverseLookup {
        private CommandsReverseLookup(){}
        private static final Map<String, Commands> reverseLookupMap = new HashMap<>();

        static {
            for (Commands command : Commands.values()) {
                reverseLookupMap.put(command.getCommandName(), command);
            }
        }

        public static Commands getCommand(String commandValue) {
            return reverseLookupMap.get(commandValue);
        }
    }
}
