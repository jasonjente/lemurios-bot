package bot.dataservice.leveling.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity(name = "HistoryEntry")
public class HistoryEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entry_gen")
    @SequenceGenerator(name = "entry_gen", sequenceName = "entry_gen")
    @Column(name = "entry_id", nullable = false)
    private Long entryId;

    @Column(name = "FULL_TAG_NAME", nullable = false)
    private String fullTag;

    @Column(name = "COMMAND_ISSUED", nullable = false, length = 1000)
    private String commandIssued;

    @Column(name = "CREATED_ON", nullable = false)
    private Timestamp createdOn;

    @ManyToOne
    @JoinColumn(name = "DISCORD_SERVER", nullable = false)
    private DiscordServer discordServer;

    @OneToOne
    @JoinColumn(name = "COMMAND_EXECUTION", nullable = false)
    private CommandExecution commandExecution;

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    public String getFullTag() {
        return fullTag;
    }

    public void setFullTag(String fullTag) {
        this.fullTag = fullTag;
    }

    public String getCommandIssued() {
        return commandIssued;
    }

    public void setCommandIssued(String commandIssued) {
        this.commandIssued = commandIssued;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public DiscordServer getDiscordServer() {
        return discordServer;
    }

    public void setDiscordServer(DiscordServer discordServer) {
        this.discordServer = discordServer;
    }

    public CommandExecution getCommandExecution() {
        return commandExecution;
    }

    public void setCommandExecution(CommandExecution commandExecution) {
        this.commandExecution = commandExecution;
    }
}
