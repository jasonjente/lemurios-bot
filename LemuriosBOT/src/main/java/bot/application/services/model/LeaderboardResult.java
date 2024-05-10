package bot.application.services.model;

import lombok.Data;

@Data
public class LeaderboardResult {
    private String userTag;
    private Integer points;
    private Integer level;

    public LeaderboardResult(final String userTag,final Integer points, final Integer level) {
        this.userTag = userTag;
        this.points = points;
        this.level = level;
    }

}
