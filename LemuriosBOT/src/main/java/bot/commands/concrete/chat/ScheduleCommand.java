package bot.commands.concrete.chat;

import bot.commands.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Service;

import static bot.constants.Commands.SCHEDULE_COMMAND;

@Service
public class ScheduleCommand extends Command {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        //in the making
        // /schedule 14-06-2023 12:34:56 -> ping all lemurs at that time.
    }

    @Override
    public String getCommandDescription() {
        return "Schedules an assemble for lemurs on a specified date";
    }

    @Override
    public String getCommandName() {
        return SCHEDULE_COMMAND.getCommandName();
    }
}
