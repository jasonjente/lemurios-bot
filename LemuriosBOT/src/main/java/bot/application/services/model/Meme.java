package bot.application.services.model;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "MEME")
@Data
public class Meme {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meme_gen")
    @SequenceGenerator(name = "meme_gen", sequenceName = "meme_seq")
    @Column(name = "id", nullable = false)
    private Long memeId;

    @Column(name = "FILENAME", unique = true)
    private String filename;
    @Lob
    @Column(name = "IMAGE_DATA", nullable = false)
    private byte[] imageData;

    @Column(name = "CREATED_ON", nullable = false)
    private Timestamp createdOn;

}
