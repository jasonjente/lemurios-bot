package bot.dataservice.meme;

import bot.dataservice.model.Meme;

import javax.persistence.*;

@Entity
@Table(name = "MEME_SERVER_RECORD")
public class MemeServerRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meme_server_record_gen")
    @SequenceGenerator(name = "meme_server_record_gen", sequenceName = "meme_server_record_seq")
    @Column(name = "meme_record_id", nullable = false, insertable = false)
    private Long memeId;

    @ManyToOne
    @JoinColumn(name = "meme_id")
    private Meme meme;

    @Column(name = "discord_server_id")
    private String discordServerId;

    public Long getMemeId() {
        return memeId;
    }

    public void setMemeId(Long memeId) {
        this.memeId = memeId;
    }


    public Meme getMeme() {
        return meme;
    }

    public void setMeme(Meme meme) {
        this.meme = meme;
    }

    public String getDiscordServerId() {
        return discordServerId;
    }

    public void setDiscordServerId(String discordServerId) {
        this.discordServerId = discordServerId;
    }

}
