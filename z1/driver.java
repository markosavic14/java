// Marko Savic e1 15/2024

public class Driver implements Comparable<Driver> {
    private String name;
    private int ranking;
    private String specialSkill;
    private boolean eligibleToRace;
    private int accumulatedTime;
    private int accumulatedPoints;
    private int lapCount;
    private boolean isWinterTireUsed;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public String getSpecialSkill() {
        return specialSkill;
    }

    public void setSpecialSkill(String specialSkill) {
        this.specialSkill = specialSkill;
    }

    public boolean isEligibleToRace() {
        return eligibleToRace;
    }

    public void setEligibleToRace(boolean eligibleToRace) {
        this.eligibleToRace = eligibleToRace;
    }

    public int getAccumulatedTime() {
        return accumulatedTime;
    }

    public void setAccumulatedTime(int accumulatedTime) {
        this.accumulatedTime = accumulatedTime;
    }

    public int getAccumulatedPoints() {
        return accumulatedPoints;
    }

    public void setAccumulatedPoints(int accumulatedPoints) {
        this.accumulatedPoints = accumulatedPoints;
    }

    public void incrementLapCount() {
        this.lapCount++;
    }

    public boolean getWinterTireUsed() {
        return isWinterTireUsed;
    }

    public void setWinterTireUsed(boolean winterTireUsed) {
        this.isWinterTireUsed = winterTireUsed;
    }

    public Driver(String name, int ranking, String specialSkill) {
        this.name = name;
        this.ranking = ranking;
        this.specialSkill = specialSkill;
        this.eligibleToRace = true;
        this.accumulatedTime = 0;
        this.accumulatedPoints = 0;
        this.lapCount = 0;
        this.isWinterTireUsed = false;
    }

    public void useSpecialSkill() {
        if (!eligibleToRace) {
            return; // Cannot use skill if not eligible
        } else if (specialSkill.equals("Overtaking") && lapCount % 3 == 0) {
            RNG rng = new RNG(10, 20);
            accumulatedTime -= rng.getRandomValue();
        } else if (specialSkill.equals("Braking") || specialSkill.equals("Cornering")) {
            RNG rng = new RNG(1, 8);
            accumulatedTime -= rng.getRandomValue();
        }
    }

    @Override
    public int compareTo(Driver o) {
        return Integer.compare(this.ranking, o.ranking);
    }

}