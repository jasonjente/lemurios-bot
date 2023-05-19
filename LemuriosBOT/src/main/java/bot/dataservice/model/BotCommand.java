package bot.dataservice.model;

import javax.persistence.*;
import java.util.List;

@Entity
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


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CommandExecution> getCommandExecutions() {
        return commandExecutions;
    }

    public void setCommandExecutions(List<CommandExecution> commandExecutions) {
        this.commandExecutions = commandExecutions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
