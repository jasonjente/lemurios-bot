package bot.commands.chat;


import bot.LemuriosBOTListenerAdapter;
import bot.commands.Command;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


import java.util.Map;

import static bot.application.constants.Commands.*;
import static bot.application.constants.MessageConstants.*;


@Slf4j
public class HelpCommand extends Command {
    

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        log.info("helpCommand - ENTER - {} called for help", event.getUser().getName());
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("LEMURIOS BOT Help Center.")
                .setDescription(HELLO.getValue()+ event.getUser().getName() + HELP_COMMENT.getValue());

        Map<String,Command> commandMap = LemuriosBOTListenerAdapter.getCommands();

        for (Command command:commandMap.values()){
            embedBuilder.addField(command.getCommandName(),command.getCommandDescription(), false);
        }

        embedBuilder.setColor(java.awt.Color.PINK).setFooter("NOW GTFO HERE!\n With Best Regards Lemurios BOT.");
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
        
        log.info("helpCommand - LEAVE");
    }

    @Override
    public String getCommandDescription() {
        return "Shows commands Help.";
    }

    @Override
    public String getCommandName() {
        return HELP_COMMAND.getCommandName();
    }
}
