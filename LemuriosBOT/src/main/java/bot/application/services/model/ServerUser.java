package bot.application.services.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "SERVER_USER")
@Data
public class ServerUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "server_user_gen")
    @SequenceGenerator(name = "server_user_gen", sequenceName = "server_user_seq")
    @Column(name = "SERVER_USER_ID", nullable = false)
    private Long id;

    @Column(name = "USER_TAG")
    private String tag;

    @ManyToOne
    @JoinColumn(name = "server_id")
    private DiscordServer server;

    @Column(nullable = false)
    private Integer points;

    @Column(name = "level")
    private Integer level;
}
