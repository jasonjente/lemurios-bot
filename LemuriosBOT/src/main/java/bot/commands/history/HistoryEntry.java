package bot.commands.history;

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

    @Column(name = "COMMAND_ISSUED", nullable = false, length = 32)
    private String commandIssued;

    @Column(name = "CREATED_ON", nullable = false)
    private Timestamp createdOn;

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
}
