// Marko Savic e1 15/2024

public class RNG {
    private int minimumValue;
    private int maximumValue;
    Random rnd;

    public int getMinimumValue() {
        return minimumValue;
    }

    public void setMinimumValue(int minimumValue) {
        this.minimumValue = minimumValue;
    }

    public int getMaximumValue() {
        return maximumValue;
    }

    public void setMaximumValue(int maximumValue) {
        this.maximumValue = maximumValue;
    }

    public RNG() {
        this.minimumValue = 0;
        this.maximumValue = 100;
        this.rnd = new Random();
    }

    public RNG(int minimumValue, int maximumValue) {
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.rnd = new Random();
    }

    public int getRandomValue() {
        return rnd.nextInt((maximumValue - minimumValue) + 1) + minimumValue;
    }
}