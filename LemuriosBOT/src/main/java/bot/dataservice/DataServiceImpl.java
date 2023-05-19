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
        return serverUserRepository.save(ret);
    }

    @Override
    public CommandExecution createCommandExecutionObject(SlashCommandInteractionEvent event) {
        CommandExecution ret = new CommandExecution();
        BotCommand botCommand = new BotCommand();
        botCommand.setName(event.getCommandString());
        botCommandRepository.save(botCommand);
        ret.setExecutedAt(LocalDateTime.now());
        ret.setCommand(botCommand);
        return commandExecutionRepository.save(ret);
    }

    @Override
    public DiscordServer createDiscordServerObject(SlashCommandInteractionEvent event) {
        DiscordServer ret;
        if(Boolean.FALSE.equals(discordServerRepository.existsByGuildId(event.getGuild().getId()))){
            ret = new DiscordServer();
            ret.setGuildId(event.getGuild().getId());
            discordServerRepository.save(ret);
        }else {
            ret = discordServerRepository.findDiscordServerByGuildId(event.getGuild().getId());
        }
        return ret;
    }

    @Override
    public void deleteCustomLinksByDiscordServer(String guildId) {
        customLinkRepository.deleteCustomLinksByDiscordServer(guildId);
    }

    @Override
    public void deleteCustomLinkByDiscordServerAndGenre(String guildId, String genre) {
        customLinkRepository.deleteCustomLinkByDiscordServerAndGenre(guildId, genre);
    }

    @Override
    public void saveCustomLink(CustomLink customLink) {
        customLinkRepository.save(customLink);
    }

    @Override
    public CustomLink findCustomLinkByDiscordServerAndGenre(String id, String genre) {
        return customLinkRepository.findCustomLinkByDiscordServerAndGenre(id, genre);
    }

    @Override
    public List<CustomLink> findCustomLinksByDiscordServerAndGenre(String guildId) {
        return customLinkRepository.getCustomLinksByDiscordServer(guildId);
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
