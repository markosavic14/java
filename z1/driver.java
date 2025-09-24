// Marko Savic e1 15/2024

public class Driver {
    private String name;
    private int ranking;
    private String specialSkill;
    private boolean eligibleToRace;
    private int accumulatedTime;
    private int accumulatedPoints;

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
    public Driver() {
        // Default constructor
    }

    public Driver(String name, int ranking, String specialSkill, boolean eligibleToRace, int accumulatedTime, int accumulatedPoints) {
        this.name = name;
        this.ranking = ranking;
        this.specialSkill = specialSkill;
        this.eligibleToRace = eligibleToRace;
        this.accumulatedTime = accumulatedTime;
        this.accumulatedPoints = accumulatedPoints;
    }

    public void useSpecialSkill() {
        // TODO Implement special skill usage logic
    }

}