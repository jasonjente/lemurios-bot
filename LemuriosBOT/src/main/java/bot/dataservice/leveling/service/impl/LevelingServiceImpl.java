package bot.dataservice.leveling.service.impl;

import bot.constants.Commands;
import bot.dataservice.leveling.model.CommandExecution;
import bot.dataservice.leveling.model.DiscordServer;
import bot.dataservice.leveling.model.LeaderboardResult;
import bot.dataservice.leveling.model.ServerUser;
import bot.dataservice.leveling.repositories.ServerUserRepository;
import bot.dataservice.leveling.service.LevelingService;
import bot.dataservice.DataServiceImpl;
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
    private final DataServiceImpl dataServiceImpl;

    public LevelingServiceImpl(ServerUserRepository serverUserRepository, DataServiceImpl dataServiceImpl) {
        this.serverUserRepository = serverUserRepository;
        this.dataServiceImpl = dataServiceImpl;
    }

    @Override
    public void earnPoints(SlashCommandInteractionEvent event){
        //Find what command was used to calculate the earned points and create an entry in the CommandExecution table.
        String commandUsed = event.getFullCommandName();
        Commands command = DataServiceImpl.CommandsReverseLookup.getCommand(commandUsed);
        Integer pointsEarned = command.getPoints();
        LOGGER.info("earnPoints() - Enter - Command: {}, points earned: {} for user: {}.", commandUsed, pointsEarned, event.getUser().getAsTag());
        CommandExecution commandExecution = dataServiceImpl.createCommandExecutionObject(event);
        //Find if the discord server exists, if not create it
        DiscordServer discordServer = dataServiceImpl.createDiscordServerObject(event);
        String tag = event.getUser().getAsTag();
        //find if the user exists, if not create that user and persist in the database
        dataServiceImpl.createServerUserObject(tag,discordServer, commandExecution, pointsEarned);
        LOGGER.info("earnPoints()");
    }

    @Override
    public List<LeaderboardResult> getLeaderboardForGuild(SlashCommandInteractionEvent event){
        LOGGER.info("getLeaderboardForGuild() - Enter - GuildId: {}", event.getGuild().getId());
        List<LeaderboardResult> ret = new LinkedList<>();
        List<ServerUser> serverUsers = serverUserRepository.findAllByServer(dataServiceImpl.createDiscordServerObject(event));
        if(!serverUsers.isEmpty()){
            for (ServerUser user:serverUsers){
                ret.add(new LeaderboardResult(user.getTag(), user.getPoints()));
            }
            ret.sort((o1, o2) -> o1.getPoints() > o2.getPoints() ? o2.getPoints() : o1.getPoints());
        }

        LOGGER.info("getLeaderboardForGuild() - Enter - GuildId: {}, total results: {}",event.getGuild().getId(), ret.size());
        return ret;
    }

}
