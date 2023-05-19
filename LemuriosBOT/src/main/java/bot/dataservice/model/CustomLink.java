package bot.dataservice.model;

import javax.persistence.*;

@Entity
@Table(name = "CUSTOM_RADIO_LINK")
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

    public CustomLink(String url, String discordServer, String genre) {
        this.url = url;
        this.discordServer = discordServer;
        this.genre = genre;
    }

    public CustomLink() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDiscordServer() {
        return discordServer;
    }

    public void setDiscordServer(String guildId) {
        this.discordServer = guildId;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
