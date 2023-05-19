package bot.dataservice.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class CommandExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "command_execution_gen")
    @SequenceGenerator(name = "command_execution_gen", sequenceName = "command_execution_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime executedAt;

    @ManyToOne
    @JoinColumn(name = "command_id")
    private BotCommand command;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }

    public BotCommand getCommand() {
        return command;
    }

    public void setCommand(BotCommand command) {
        this.command = command;
    }
}