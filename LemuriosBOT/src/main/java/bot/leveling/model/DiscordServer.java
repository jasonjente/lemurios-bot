package bot.leveling.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class DiscordServer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "discord_server_gen")
    @SequenceGenerator(name = "discord_server_gen", sequenceName = "discord_server_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String guildId;

    @OneToMany(mappedBy = "server")
    private List<ServerUser> users;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String name) {
        this.guildId = name;
    }

    public List<ServerUser> getUsers() {
        return users;
    }

    public void setUsers(List<ServerUser> users) {
        this.users = users;
    }
}
