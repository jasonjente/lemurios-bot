package bot.leveling.service.impl;

import bot.constants.Commands;
import bot.leveling.model.*;
import bot.leveling.repositories.BotCommandRepository;
import bot.leveling.repositories.CommandExecutionRepository;
import bot.leveling.repositories.DiscordServerRepository;
import bot.leveling.repositories.ServerUserRepository;
import bot.leveling.service.LevelingService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class LevelingServiceImpl implements LevelingService {

    private final ServerUserRepository serverUserRepository;

    private final DiscordServerRepository discordServerRepository;

    private final CommandExecutionRepository commandExecutionRepository;
    private final BotCommandRepository botCommandRepository;

    public LevelingServiceImpl(ServerUserRepository serverUserRepository, DiscordServerRepository discordServerRepository, CommandExecutionRepository commandExecutionRepository, BotCommandRepository botCommandRepository) {
        this.serverUserRepository = serverUserRepository;
        this.discordServerRepository = discordServerRepository;
        this.commandExecutionRepository = commandExecutionRepository;
        this.botCommandRepository = botCommandRepository;
    }

    @Override
    public void earnPoints(SlashCommandInteractionEvent event){
        //Find what command was used to calculate the earned points and create an entry in the CommandExecution table.
        String commandUsed = event.getFullCommandName();
        Commands command = CommandsReverseLookup.getCommand(commandUsed);
        Integer pointsEarned = command.getPoints();

        CommandExecution commandExecution = createCommandExecutionObject(event);
        //Find if the discord server exists, if not create it
        DiscordServer discordServer = createDiscordServerObject(event);
        String tag = event.getUser().getAsTag();
        //find if the user exists, if not create that user and persist in the database
        createServerUserObject(tag,discordServer, commandExecution, pointsEarned);
    }

    @Override
    public List<LeaderboardResult> getLeaderboardForGuild(SlashCommandInteractionEvent event){
        List<LeaderboardResult> ret = new LinkedList<>();
        List<ServerUser> serverUsers = serverUserRepository.findAllByServer(createDiscordServerObject(event));
        if(!serverUsers.isEmpty()){
            for (ServerUser user:serverUsers){
                ret.add(new LeaderboardResult(user.getTag(), user.getPoints()));
            }
            ret.sort((o1, o2) -> o1.getPoints() > o2.getPoints() ? o2.getPoints() : o1.getPoints());
        }


        return ret;
    }

    private ServerUser createServerUserObject(String tag, DiscordServer discordServer, CommandExecution commandExecution, Integer pointsEarned) {
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

    private CommandExecution createCommandExecutionObject(SlashCommandInteractionEvent event) {
        CommandExecution ret = new CommandExecution();
        BotCommand botCommand = new BotCommand();
        botCommand.setName(event.getCommandString());
        botCommandRepository.save(botCommand);
        ret.setExecutedAt(LocalDateTime.now());
        ret.setCommand(botCommand);
        return commandExecutionRepository.save(ret);
    }

    private DiscordServer createDiscordServerObject(SlashCommandInteractionEvent event) {
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
    private static class CommandsReverseLookup {
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
