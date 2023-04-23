package bot;

import bot.commands.concrete.*;
import bot.commands.history.HistoryEntry;
import bot.commands.history.HistoryEntryRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.sql.Timestamp;
import java.time.Instant;

import static bot.constants.Constants.*;

@Component
public class LemuriosBOT extends ListenerAdapter {
    public static final String API_TOKEN = "mytoken-1-12--312-313";
    private static final Logger LOGGER = LoggerFactory.getLogger(LemuriosBOT.class);
    private HistoryEntryRepository repository;
    private AssemblemursCommand assemblemursCommand;
    private AvailableNamesCommand availableNamesCommand;
    private CreditsCommand creditsCommand;
    private DetectImageEdgesCommand detectImageEdgesCommand;
    private HelpCommand helpCommand;
    private HistoryCommand historyCommand;
    private MemeCommand memeCommand;
    private UploadMemeCommand uploadMemeCommand;


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        LOGGER.info("Message received from {} - Content: {}", event.getAuthor().getAsTag(), event.getMessage().getContentDisplay());
        if (ASSEMLEMURS_COMMAND.getValue().equals(event.getMessage().getContentRaw())) {
            assemblemursCommand.execute(event);
        } else if (HELP_COMMAND.getValue().equals(event.getMessage().getContentRaw())) {
            helpCommand.execute(event);
        } else if (CREDITS_COMMAND.getValue().equals(event.getMessage().getContentRaw())) {
            creditsCommand.execute(event);
        } else if (HISTORY_COMMAND.getValue().equals(event.getMessage().getContentRaw())){
            historyCommand.execute(event);
        } else if (MEME_COMMAND.getValue().equals(event.getMessage().getContentRaw())){
            memeCommand.execute(event);
        } else if(AVAILABLE_NAMES.getValue().equals(event.getMessage().getContentRaw())){
            availableNamesCommand.execute(event);
        } else if(event.getMessage().getContentRaw().startsWith(UPLOAD_MEME_COMMAND.getValue())){
            uploadMemeCommand.execute(event);
        } else if(event.getMessage().getContentRaw().startsWith(DETECT_IMAGE_EDGES.getValue())){
            detectImageEdgesCommand.execute(event);
        } else if (event.getMessage().getContentRaw().startsWith(PLAY_COMMAND.getValue())){
            playCommand(event);
        } else if (PAUSE_COMMAND.getValue().equals(event.getMessage().getContentRaw())){
            pauseCommand(event);
        } else if (SKIP_COMMAND.getValue().equals(event.getMessage().getContentRaw())){
            skipCommand(event);
        } else if (STOP_COMMAND.getValue().equals(event.getMessage().getContentRaw())) {
            stopCommand(event);
        }
    }

    private void stopCommand(MessageReceivedEvent event) {
        functionalityNotReadyYet(event);
    }

    private void skipCommand(MessageReceivedEvent event) {
        functionalityNotReadyYet(event);
    }

    private void playCommand(MessageReceivedEvent event) {
        functionalityNotReadyYet(event);
    }


    private void pauseCommand(MessageReceivedEvent event) {
        functionalityNotReadyYet(event);
    }
    private void functionalityNotReadyYet(MessageReceivedEvent event) {
        createHistoryEntry(event);
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("LEMURIOS BOT Help Center.")
                .setDescription(HELLO.getValue()+ event.getAuthor().getName() + ", unfortunately this functionality has not been setup yet :/.")
                .setColor(Color.MAGENTA)
                .setFooter("NOW GTFO HERE!\n With Best Regards Lemurios BOT-DEV Team.");
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();

        LOGGER.info("functionalityNotReadyYet - LEAVE");
    }

    private void createHistoryEntry(MessageReceivedEvent event){
        LOGGER.info("createHistoryEntry - ENTER");
        HistoryEntry entry = new HistoryEntry();
        entry.setCommandIssued(event.getMessage().getContentRaw());
        entry.setFullTag(event.getAuthor().getAsTag());
        entry.setCreatedOn(Timestamp.from(Instant.now()));
        repository.save(entry);
        LOGGER.info("createHistoryEntry - LEAVE");

    }

    @Autowired
    public void setAssemblemursCommand(AssemblemursCommand assemblemursCommand) {
        this.assemblemursCommand = assemblemursCommand;
    }

    @Autowired
    public void setAvailableNamesCommand(AvailableNamesCommand availableNamesCommand) {
        this.availableNamesCommand = availableNamesCommand;
    }

    @Autowired
    public void setCreditsCommand(CreditsCommand creditsCommand) {
        this.creditsCommand = creditsCommand;
    }

    @Autowired
    public void setDetectImageEdgesCommand(DetectImageEdgesCommand detectImageEdgesCommand) {
        this.detectImageEdgesCommand = detectImageEdgesCommand;
    }

    @Autowired
    public void setHelpCommand(HelpCommand helpCommand) {
        this.helpCommand = helpCommand;
    }

    @Autowired
    public void setHistoryCommand(HistoryCommand historyCommand) {
        this.historyCommand = historyCommand;
    }

    @Autowired
    public void setMemeCommand(MemeCommand memeCommand) {
        this.memeCommand = memeCommand;
    }

    @Autowired
    public void setUploadMemeCommand(UploadMemeCommand uploadMemeCommand) {
        this.uploadMemeCommand = uploadMemeCommand;
    }

    @Autowired
    public void setRepository(HistoryEntryRepository repository) {
        this.repository = repository;
    }
}
