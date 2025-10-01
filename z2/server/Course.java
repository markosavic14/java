
import java.util.Map;

public class Course {
    private String name;
    private Map<String, Integer> kategorija;

    public String getName() {
        return name;
    }

    public Course(String name) {
        this.name = name;
    }

    public void dodajKategoriju(String naziv, int tezina) {
        kategorija.put(naziv, tezina); // TODO provera da li tezina == 100
    }

}
