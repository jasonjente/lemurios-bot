package bot.dataservice;

import bot.dataservice.leveling.model.CommandExecution;
import bot.dataservice.leveling.model.DiscordServer;
import bot.dataservice.leveling.model.ServerUser;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface DataService {
    ServerUser createServerUserObject(String tag, DiscordServer discordServer, CommandExecution commandExecution, Integer pointsEarned);

    CommandExecution createCommandExecutionObject(SlashCommandInteractionEvent event);

    DiscordServer createDiscordServerObject(SlashCommandInteractionEvent event);

}
