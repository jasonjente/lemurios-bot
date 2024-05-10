package bot.application.services.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
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

}
