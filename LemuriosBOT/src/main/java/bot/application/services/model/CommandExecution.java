package bot.application.services.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
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

}