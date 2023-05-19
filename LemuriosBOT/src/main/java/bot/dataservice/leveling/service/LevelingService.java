package bot.dataservice.leveling.service;

import bot.dataservice.model.LeaderboardResult;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public interface LevelingService {
    void earnPoints(SlashCommandInteractionEvent event);

    List<LeaderboardResult> getLeaderboardForGuild(SlashCommandInteractionEvent event);
}
