package bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class Command {

    /**
     * This method is the base method of all commands that can be executed. It takes as input a slash command event,
     * which will call the appropriate bean.
     *
     * @param event the slash command interaction event that was invoked.
     */
    public abstract void execute(SlashCommandInteractionEvent event);

    public abstract String getCommandDescription();
    public abstract String getCommandName();
}
