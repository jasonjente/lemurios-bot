package bot.services.leveling;

public class Jackpot {
    private int points;
    private boolean won;

    public Jackpot(int points, boolean won) {
        this.points = points;
        this.won = won;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won) {
        this.won = won;
    }
}
