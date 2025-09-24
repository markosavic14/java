// Marko Savic e1 15/2024

import java.util.Scanner;

public class Simulate {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Championship championship = new Championship();
        try {
            championship.readDriversFromFile("drivers.txt");
            championship.readVenuesFromFile("venues.txt");
        } catch (IOException e) {
            System.err.println("Error reading files: " + e.getMessage());
            return;
        }

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
        List<String> availableVenues = new ArrayList<>(championship.getVenues());
        List<String> selectedVenues = new ArrayList<>();

        for (int i = 1; i <= number; i++) {
            System.out.println("Dostupne staze za trku " + i + ":");
            for (int j = 0; j < availableVenues.size(); j++) {
            System.out.println((j + 1) + ". " + availableVenues.get(j));
            }
            System.out.print("Izaberite redni broj staze za trku " + i + ": ");
            int venueIndex = scanner.nextInt() - 1;
            while (venueIndex < 0 || venueIndex >= availableVenues.size()) {
            System.out.print("Pogrešan unos. Izaberite ponovo: ");
            venueIndex = scanner.nextInt() - 1;
            }
            String chosenVenue = availableVenues.remove(venueIndex);
            selectedVenues.add(chosenVenue);
            System.out.println("Izabrali ste stazu: " + chosenVenue);
        }

        championship.setSeasonVenues(selectedVenues);
        scanner.close();

        championship.prepareForTheRace();
}