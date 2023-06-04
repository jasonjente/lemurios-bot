package bot.services.model;
import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "MEME")
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

//todo delete
    //    @JoinColumn(name = "UserId", nullable = false)
//    @OneToOne
//    private ServerUser caller;


    public Long getMemeId() {
        return memeId;
    }

    public void setMemeId(Long memeId) {
        this.memeId = memeId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

//    public ServerUser getCaller() {
//        return caller;
//    }
//
//    public void setCaller(ServerUser caller) {
//        this.caller = caller;
//    }
}
