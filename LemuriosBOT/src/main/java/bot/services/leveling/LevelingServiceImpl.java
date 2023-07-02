package bot.services.leveling;

import bot.constants.Commands;
import bot.services.dataservice.DataService;
import bot.services.model.CommandExecution;
import bot.services.model.DiscordServer;
import bot.services.model.LeaderboardResult;
import bot.services.model.ServerUser;
import bot.services.leveling.repositories.ServerUserRepository;
import bot.services.dataservice.DataServiceImpl;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class LevelingServiceImpl implements LevelingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LevelingServiceImpl.class);

    private final ServerUserRepository serverUserRepository;
    private final DataService dataService;

    public LevelingServiceImpl(ServerUserRepository serverUserRepository, DataService dataService) {
        this.serverUserRepository = serverUserRepository;
        this.dataService = dataService;
    }

    @Override
    public void earnPoints(SlashCommandInteractionEvent event){
        //Find what command was used to calculate the earned points and create an entry in the CommandExecution table.
        String commandUsed = event.getFullCommandName();
        Commands command = DataServiceImpl.CommandsReverseLookup.getCommand(commandUsed);
        Integer pointsEarned = command.getPoints();
        LOGGER.info("earnPoints() - Enter - Command: {}, points earned: {} for user: {}.", commandUsed, pointsEarned, event.getUser().getName());
        CommandExecution commandExecution = dataService.createCommandExecutionObject(event);
        //Find if the discord server exists, if not create it
        DiscordServer discordServer = dataService.findOrCreateDiscordServerObject(event);
        String tag = event.getUser().getName();
        //find if the user exists, if not create that user and persist in the database
        dataService.createServerUserObject(tag,discordServer, commandExecution, pointsEarned);
        LOGGER.info("earnPoints() - LEAVE : user: {} earned : {} ", event.getUser().getName(), pointsEarned);
    }

    @Override
    public List<LeaderboardResult> getLeaderboardForGuild(SlashCommandInteractionEvent event){
        LOGGER.info("getLeaderboardForGuild() - Enter - GuildId: {}", event.getGuild().getId());
        List<LeaderboardResult> ret = new LinkedList<>();
        List<ServerUser> serverUsers = serverUserRepository.findOrderedByServerOrderByPointsDesc(dataService.findOrCreateDiscordServerObject(event));
        if(!serverUsers.isEmpty()){
            for (ServerUser user:serverUsers){
                Integer userLevel = dataService.calculateLevel(user);
                ret.add(new LeaderboardResult(user.getTag(), user.getPoints(), userLevel));
            }
            ret.sort((o1, o2) -> o1.getPoints() > o2.getPoints() ? o2.getPoints() : o1.getPoints());
        }

        LOGGER.info("getLeaderboardForGuild() - Enter - GuildId: {}, total results: {}",event.getGuild().getId(), ret.size());
        return ret;
    }

    @Override
    public void earnPoints(SlashCommandInteractionEvent event, Integer points) {
        //Find what command was used to calculate the earned points and create an entry in the CommandExecution table.
        String commandUsed = event.getFullCommandName();
        LOGGER.info("earnPoints() HIGH-ROLLER - Enter - Command: {}, points earned: {} for user: {}.", commandUsed, points, event.getUser().getName());
        CommandExecution commandExecution = dataService.createCommandExecutionObject(event);
        //Find if the discord server exists, if not create it
        DiscordServer discordServer = dataService.findOrCreateDiscordServerObject(event);
        String tag = event.getUser().getName();
        //find if the user exists, if not create that user and persist in the database
        dataService.createServerUserObject(tag,discordServer, commandExecution, points);
        LOGGER.info("earnPoints() - LEAVE -  points earned: {}, user: {}", points, event.getUser().getName());
    }

}
