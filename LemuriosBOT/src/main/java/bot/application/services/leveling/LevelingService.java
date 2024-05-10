package bot.application.services.leveling;

import bot.application.services.data.DataService;
import bot.application.services.model.LeaderboardResult;
import bot.application.services.model.ServerUser;
import bot.application.services.repositories.ServerUserRepository;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class LevelingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LevelingService.class);

    private final ServerUserRepository serverUserRepository;
    private final DataService dataService;

    public LevelingService(final ServerUserRepository serverUserRepository,final  DataService dataService) {
        this.serverUserRepository = serverUserRepository;
        this.dataService = dataService;
    }

    public void earnPoints(final SlashCommandInteractionEvent event){
        //Find what command was used to calculate the earned points and create an entry in the CommandExecution table.
        var commandUsed = event.getFullCommandName();
        var command = DataService.CommandsReverseLookup.getCommand(commandUsed);
        var pointsEarned = command.getPoints();
        LOGGER.info("earnPoints() - Enter - Command: {}, points earned: {} for user: {}.",
                commandUsed, pointsEarned, event.getUser().getName());
        //Find if the discord server exists, if not create it
        var discordServer = dataService.findOrCreateDiscordServerObject(event);
        var tag = event.getUser().getName();
        //find if the user exists, if not create that user and persist in the database
        dataService.createServerUserObject(tag,discordServer, pointsEarned);
        LOGGER.info("earnPoints() - LEAVE : user: {} earned : {} ", event.getUser().getName(), pointsEarned);
    }

    public List<LeaderboardResult> getLeaderboardForGuild(final SlashCommandInteractionEvent event){
        LOGGER.info("getLeaderboardForGuild() - Enter - GuildId: {}", event.getGuild().getId());
        var ret = new LinkedList<LeaderboardResult>();
        var serverUsers = serverUserRepository.
                findOrderedByServerOrderByPointsDesc(dataService.findOrCreateDiscordServerObject(event));
        if (!serverUsers.isEmpty()){
            for (ServerUser user:serverUsers){
                Integer userLevel = dataService.calculateLevel(user);
                ret.add(new LeaderboardResult(user.getTag(), user.getPoints(), userLevel));
            }
            ret.sort((o1, o2) -> o1.getPoints() > o2.getPoints() ? o2.getPoints() : o1.getPoints());
        }

        LOGGER.info("getLeaderboardForGuild() - Enter - GuildId: {}, total results: {}",
                event.getGuild().getId(), ret.size());
        return ret;
    }

    public void earnPoints(final SlashCommandInteractionEvent event, final Integer points) {
        //Find what command was used to calculate the earned points and create an entry in the CommandExecution table.
        var commandUsed = event.getFullCommandName();
        LOGGER.info("earnPoints() HIGH-ROLLER - Enter - Command: {}, points earned: {} for user: {}.",
                commandUsed, points, event.getUser().getName());
        //Find if the discord server exists, if not create it
        var discordServer = dataService.findOrCreateDiscordServerObject(event);
        var tag = event.getUser().getName();
        //find if the user exists, if not create that user and persist in the database
        dataService.createServerUserObject(tag,discordServer, points);
        LOGGER.info("earnPoints() - LEAVE -  points earned: {}, user: {}", points, event.getUser().getName());
    }

}
