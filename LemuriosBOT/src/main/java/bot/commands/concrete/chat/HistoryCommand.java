package bot.commands.concrete.chat;

import bot.LemuriosBOT;
import bot.commands.Command;
import bot.services.dataservice.DataService;
import bot.services.model.BotCommand;
import bot.services.model.CommandExecution;
import bot.services.model.DiscordServer;
import bot.services.model.HistoryEntry;
import bot.services.leveling.repositories.CommandExecutionRepository;
import bot.services.leveling.repositories.HistoryEntryRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static bot.constants.Commands.HISTORY_COMMAND;
import static bot.constants.Constants.GTFO_MESSAGE;

@Service
public class HistoryCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryCommand.class);
    private final HistoryEntryRepository repository;
    private final DataService dataService;
    private final CommandExecutionRepository commandExecutionRepository;

    public HistoryCommand(HistoryEntryRepository repository, DataService dataService, CommandExecutionRepository commandExecutionRepository) {
        this.repository = repository;
        this.dataService = dataService;
        this.commandExecutionRepository = commandExecutionRepository;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String sender = event.getUser().getName();
        createHistoryEntry(event);
        LOGGER.info("{} has requested the history of commands.", sender);
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Command History:")
                .setDescription("Last 25 commands executed:").setColor(Color.BLACK);
        String command = event.getCommandString();
        List<String> commandTrimmed = Arrays.asList(command.split(" "));
        if(commandTrimmed.size()>1){
            command = commandTrimmed.get(0);
        }
        if(event.getOptions().isEmpty()) {
            findAllCommandsExecuted(event, embedBuilder);
        } else if (LemuriosBOT.getCommands().containsKey(command)){
            findAllCommandsExecutedByCommandName(command, event);
        }
        embedBuilder.setFooter(GTFO_MESSAGE.getValue());
        earnPoints(event);
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }

    private List<HistoryEntry> findAllCommandsExecutedByCommandName(String command, SlashCommandInteractionEvent event) {
        BotCommand botCommand = new BotCommand();
        botCommand.setName(command);

        CommandExecution commandExecution = commandExecutionRepository.findAllByCommand(botCommand).get(0);
        DiscordServer discordServer = dataService.findOrCreateDiscordServerObject(event);
        return repository.findHistoryEntryByCommandExecutionAndDiscordServer(commandExecution, discordServer);
    }

    private void findAllCommandsExecuted(SlashCommandInteractionEvent event, EmbedBuilder embedBuilder) {
        try {
            List<HistoryEntry> historyEntryList = repository.findOrderedByDiscordServerOrderByEntryId(dataService.findOrCreateDiscordServerObject(event));
            int max = 0;
            for(HistoryEntry entry:historyEntryList){
                if(max == 25){
                    break;
                }
                embedBuilder.addField("Command: " + entry.getCommandIssued(), " On " + entry.getCreatedOn() + " by " + entry.getFullTag(),false);
                max++;
            }
        }catch (Exception e){
            LOGGER.error("Error connecting to History Archival ", e);
            embedBuilder.addField("Error connecting to History Archival :((", "If this error persist please contact our adminstrators @oso zw xatzo", true);
        }
    }

    @Override
    public String getCommandDescription() {
        return "View command history.";
    }

    @Override
    public String getCommandName() {
        return HISTORY_COMMAND.getCommandName();
    }
}
