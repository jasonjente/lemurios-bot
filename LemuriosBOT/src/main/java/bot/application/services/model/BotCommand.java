package bot.application.services.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class BotCommand {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bot_command_gen")
    @SequenceGenerator(name = "bot_command_gen", sequenceName = "bot_command_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "command")
    private List<CommandExecution> commandExecutions;

}
