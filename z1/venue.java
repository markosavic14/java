// Marko Savic e1 15/2024

public class Venue {
    private int averageLapTime;
    private double chanceOfRain;
    private int numberOfLaps;
    private String venueName;

    public int getAverageLapTime() {
        return averageLapTime;
    }
    public void setAverageLapTime(int averageLapTime) {
        this.averageLapTime = averageLapTime;
    }
    public double getChanceOfRain() {
        return chanceOfRain;
    }
    public void setChanceOfRain(double chanceOfRain) {
        this.chanceOfRain = chanceOfRain;
    }
    public int getNumberOfLaps() {
        return numberOfLaps;
    }
    public void setNumberOfLaps(int numberOfLaps) {
        this.numberOfLaps = numberOfLaps;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public Venue() {
    }

    public Venue(String venueName, int averageLapTime, double chanceOfRain, int numberOfLaps) {
        this.venueName = venueName;
        this.averageLapTime = averageLapTime;
        this.chanceOfRain = chanceOfRain;
        this.numberOfLaps = numberOfLaps;
    }

}