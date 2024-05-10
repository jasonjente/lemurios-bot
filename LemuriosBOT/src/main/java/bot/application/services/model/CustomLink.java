package bot.application.services.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "CUSTOM_RADIO_LINK")
@Data
public class CustomLink {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "discord_server_gen")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "custom_url", length = 10000)
    private String url;
    @Column(name = "DISCORD_GUILD_ID", nullable = false)
    private String discordServer;

    @Column(name = "genre", nullable = false, unique = true)
    private String genre;

    public CustomLink(final String url, final String discordServer, final String genre) {
        this.url = url;
        this.discordServer = discordServer;
        this.genre = genre;
    }

}
