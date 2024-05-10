package bot.domain;

import bot.application.services.model.Meme;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "MEME_SERVER_RECORD")
@Data
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

}
