// Marko Savic e1 15/2024

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Simulate {
    public static void main(String[] args) {
        final String BASE_PATH = "C:\\Users\\marko.miloradsavic@infineon.com\\Documents\\java\\z1\\";
        Scanner scanner = new Scanner(System.in);

        Championship championship = new Championship(BASE_PATH + "vozaci.txt", BASE_PATH + "staze.txt");

        System.out.print("Unesite broj trka u sezoni: ");
        int number = scanner.nextInt();
        // Validacija broja trka
        if (number < 3 || number > 5) {
            while (number < 3 || number > 5) {
            System.out.println("Uneli ste broj van opsega 3-5: " + number);
            System.out.print("Unesite broj trka u sezoni: ");
            number = scanner.nextInt();
            }
        }
        System.out.println("Uneli ste broj: " + number);

        // Odabir staza za svaku trku
        List<Venue> availableVenues = new ArrayList<>(championship.getVenues());
        List<Venue> selectedVenues = new ArrayList<>();

        for (int i = 1; i <= number; i++) {
            System.out.println("Dostupne staze za trku " + i + ":");
            for (int j = 0; j < availableVenues.size(); j++) {
            System.out.println((j + 1) + ". " + availableVenues.get(j).getVenueName());
            }
            System.out.print("Izaberite redni broj staze za trku " + i + ": ");
            int venueIndex = scanner.nextInt() - 1;
            while (venueIndex < 0 || venueIndex >= availableVenues.size()) {
            System.out.print("Pogrešan unos. Izaberite ponovo: ");
            venueIndex = scanner.nextInt() - 1;
            }
            Venue chosenVenue = availableVenues.remove(venueIndex);
            selectedVenues.add(chosenVenue);
        }
        System.out.println("Izabrane staze za sezonu:");
        for (Venue venue : selectedVenues) {
            System.out.println("- " + venue.getVenueName());
        }
        scanner.close();

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
                championship.printLeader(i + 1); // indeks staze (+1) + indeks kruga (+1)
                numOfRaces += 1;
            }
            championship.printWinnersAfterRace(venue.getVenueName());
        }
        championship.printChampion(numOfRaces);
}
}