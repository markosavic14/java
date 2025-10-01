
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Student {
    private String username;
    private String name;
    private String lastName;
    private String indexNum;
    private String jmbg;
    private ArrayList<Course> courses;

    public String getName(){
        return this.name;
    }

    public String getIndexNum(){
        return this.indexNum;
    }

    public ArrayList<Course> getCourses(){
        return this.courses;
    }

    public String getUsername(){
        return this.username;
    }

    public Student(String username, String name, String lastName, String indexNum, String jmbg) {
        this.username = username;
        this.name = name;
        this.lastName = lastName;
        if (!indexNum.matches("^[Ee][1-3][/-](200[0-9]|201[0-9]|202[0-3])$")) {
            throw new IllegalArgumentException("Nevaljan format broja indeksa. Ocekivani format: E1-2015 or e2/2019");
        }
        this.indexNum = indexNum;
        if (jmbg.length() != 13) {
            throw new IllegalArgumentException("JMBG nije 13 cifara.");
        }
        String day = jmbg.substring(0, 2);
        String month = jmbg.substring(2, 4);
        String year = jmbg.substring(10, 13);
        int dayInt = Integer.parseInt(day);
        int monthInt = Integer.parseInt(month);
        int yearInt = Integer.parseInt(year);
        if (dayInt < 1 || dayInt > 31) {
            throw new IllegalArgumentException("Nevalidan dan u JMBG.");
        }
        if (monthInt < 1 || monthInt > 12) {
            throw new IllegalArgumentException("Nevalidan mesec u JMBG.");
        }
        this.jmbg = jmbg;
    }

    public void dodajPredmet(Course course) {
        courses.add(course);
    }
}
