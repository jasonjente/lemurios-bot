package bot.dataservice.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SERVER_USER")
public class ServerUser {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "server_user_gen")
    @SequenceGenerator(name = "server_user_gen", sequenceName = "server_user_seq")
    @Column(name = "SERVER_USER_ID", nullable = false)
    private Long id;

    @Column(name = "USER_TAG")
    private String tag;

    @OneToMany
    private List<CommandExecution> commandExecutions;

    @ManyToOne
    @JoinColumn(name = "server_id")
    private DiscordServer server;

    @Column(nullable = false)
    private Integer points;

    @Column(name = "level")
    private Integer level;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<CommandExecution> getCommandExecutions() {
        if(commandExecutions == null){
            commandExecutions = new ArrayList<>();
        }
        return commandExecutions;
    }

    public void setCommandExecutions(List<CommandExecution> commandExecutions) {
        this.commandExecutions = commandExecutions;
    }

    public DiscordServer getServer() {
        return server;
    }

    public void setServer(DiscordServer server) {
        this.server = server;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
