package bot.commands;

import bot.application.services.leveling.LevelingService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class Command {

    /**
     * This field represents an instance of the leveling service that can be invoked by the children services.
     */
    @Autowired
    private LevelingService levelingService;

    /**
     * This method is
     * @param event
     */
    public abstract void execute(SlashCommandInteractionEvent event);

    public void earnPoints(SlashCommandInteractionEvent event){
        levelingService.earnPoints(event);
    }
    public void earnPoints(SlashCommandInteractionEvent event, Integer points){
        levelingService.earnPoints(event, points);
    }

    public abstract String getCommandDescription();
    public abstract String getCommandName();
}
