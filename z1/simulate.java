// Marko Savic e1 15/2024

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Simulate {
    public static void main(String[] args) {
        String workingDir = System.getProperty("user.dir") + "\\";
        System.out.print("Da li su .txt datoteke u " + workingDir + "? (y/n): ");
        
        Championship championship;
        List<Venue> selectedVenues;
        try (Scanner scanner = new Scanner(System.in)) {
            String response;
            while (true) {
                response = scanner.nextLine().trim().toLowerCase();
                if (response.equals("n")) {
                    System.out.print("Unesite putanju do .txt datoteka rucno: ");
                    workingDir = scanner.nextLine();
                    if (!workingDir.endsWith("\\")) {
                        workingDir += "\\";
                    }
                    break;
                } else if (response.equals("y")) {
                    System.out.println("Prihvacen radni direktorijum.");
                    break;
                } else {
                    System.out.print("Molimo unesite 'y' ili 'n': ");
                }
            }
            
            championship = new Championship(workingDir + "vozaci.txt", workingDir + "staze.txt");
            int number = 0;
            while (true) {
                System.out.print("Unesite broj trka u sezoni: ");
                if (scanner.hasNextInt()) {
                    number = scanner.nextInt();
                    if (number >= 3 && number <= 5) {
                        break;
                    } else {
                        System.out.println("Uneli ste broj van opsega 3-5: " + number);
                    }
                } else {
                    System.out.println("Neispravan unos. Molimo unesite ceo broj.");
                    scanner.next(); // consume invalid input
                }
            }
            System.out.println("Uneli ste broj: " + number);
            // Odabir staza za svaku trku
            List<Venue> availableVenues = new ArrayList<>(championship.getVenues());
            selectedVenues = new ArrayList<>();
            for (int i = 1; i <= number; i++) {
                System.out.println("Dostupne staze za trku " + i + ":");
                for (int j = 0; j < availableVenues.size(); j++) {
                    System.out.println((j + 1) + ". " + availableVenues.get(j).getVenueName());
                }
                int venueIndex = -1;
                while (true) {
                    System.out.print("Izaberite redni broj staze za trku " + i + ": ");
                    if (scanner.hasNextInt()) {
                        venueIndex = scanner.nextInt() - 1;
                        if (venueIndex >= 0 && venueIndex < availableVenues.size()) {
                            break;
                        } else {
                            System.out.println("Pogresan unos. Broj nije u opsegu.");
                        }
                    } else {
                        System.out.println("Neispravan unos. Molimo unesite ceo broj.");
                        scanner.next(); // consume invalid input
                    }
                }
                Venue chosenVenue = availableVenues.remove(venueIndex);
                selectedVenues.add(chosenVenue);
            }
            System.out.println("Izabrane staze za sezonu:");
            for (Venue venue : selectedVenues) {
                System.out.println("- " + venue.getVenueName());
            }
        }

        // Simulacija trka
        int numOfRaces = 0;
        for (Venue venue : selectedVenues) {
            System.out.println("#####################################");
            System.out.println("Simulacija " + (selectedVenues.indexOf(venue) + 1) + ". trke na stazi: " + venue.getVenueName());
            System.out.println("#####################################");

            championship.prepareForTheRace();

            for (int i = 0; i < venue.getNumberOfLaps(); i++) {
                System.out.println("Krug: " + (i + 1) + "/" + venue.getNumberOfLaps() + ".");
                championship.checkForRain(venue.getChanceOfRain(), i + 1);
                championship.driveAverageLapTime(venue.getAverageLapTime());
                championship.applySpecialSkill();
                championship.checkMechanicalProblem();
                championship.printLeader(i + 1);
                numOfRaces += 1;
            }
            championship.printWinnersAfterRace(venue.getVenueName());
        }
        championship.printChampion(numOfRaces);
}
}