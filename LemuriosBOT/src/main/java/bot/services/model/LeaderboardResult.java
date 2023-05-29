package bot.services.model;

public class LeaderboardResult {
    private String userTag;
    private Integer points;
    private Integer level;

    public LeaderboardResult(String userTag, Integer points, Integer level) {
        this.userTag = userTag;
        this.points = points;
        this.level = level;
    }

    public String getUserTag() {
        return userTag;
    }

    public void setUserTag(String userTag) {
        this.userTag = userTag;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
