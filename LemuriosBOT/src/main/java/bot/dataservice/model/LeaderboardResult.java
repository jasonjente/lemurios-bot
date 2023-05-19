package bot.dataservice.model;

public class LeaderboardResult {
    private String userTag;
    private Integer points;

    public LeaderboardResult(String userTag, Integer points) {
        this.userTag = userTag;
        this.points = points;
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
}
