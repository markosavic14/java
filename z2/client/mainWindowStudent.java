import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

public class mainWindowStudent {
    private List<String> predmeti = new ArrayList<>();

    public mainWindowStudent(PrintWriter out, BufferedReader in) {
        javax.swing.JFrame frame = new javax.swing.JFrame("Predmeti");
        javax.swing.DefaultListModel<String> listModel = new javax.swing.DefaultListModel<>();
        javax.swing.JList<String> list = new javax.swing.JList<>(listModel);
        
        out.println("GET_SUBJECTS");
        try {
            String response = in.readLine();
            if (response != null && !response.isEmpty()) {
                String[] subjects = response.split(",");
                for (String subject : subjects) {
                    listModel.addElement(subject);
                }
            } else {
                listModel.addElement("No subjects available.");
            }
        } catch (Exception e) {
            listModel.addElement("Error fetching subjects.");
            e.printStackTrace();
        }

        frame.add(new javax.swing.JScrollPane(list));
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
