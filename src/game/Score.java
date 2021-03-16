package game;

public class Score {
    private double score;
    private int multiplier;
    private int multiplierTimer;

    public Score() {
        score = 0;
        multiplier = 1;
        multiplierTimer = 3;
    }

    public double getScore() {
        return score;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void update(double dt) {
        if (multiplierTimer >dt){
            addMultiplier(1);
            multiplierTimer -= 1;
            if (multiplierTimer == 0)
                addMultiplier(-1);
        }
    }

    private void addPoints(double points) {
        score += points*getMultiplier();
    }

    public void notifyAsteroidHit() {
        addPoints(10);

    }

    public void addMultiplier(int multiplier) {
        if (multiplier < 0)
            this.multiplier += Math.abs(multiplier);
       this.multiplier += multiplier;
    }

}