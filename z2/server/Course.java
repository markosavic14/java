
import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

public class Course implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private Map<String, String> kategorija; // TODO update - int to String to store "points:minPoints"

    public String getName() {
        return name;
    }

    public Map<String, String> getKategorija() {
        return kategorija;
    }

    public Course(String name) {
        this.name = name;
        this.kategorija = new HashMap<>();
    }

    public void dodajKategoriju(String naziv, int points, int minPoints) {
        kategorija.put(naziv, points + ":" + minPoints);
    }

    public void dodajKategoriju(String naziv, int tezina) {
        kategorija.put(naziv, tezina + ":0"); // Default minPoints to 0
    }

    public int getTotalPoints() {
        int total = 0;
        for (String pointsData : kategorija.values()) {
            String[] parts = pointsData.split(":");
            if (parts.length >= 1) {
                try {
                    total += Integer.parseInt(parts[0]);
                } catch (NumberFormatException e) {
                    // Skip invalid entries
                }
            }
        }
        return total;
    }
}
