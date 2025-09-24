// Marko Savic e1 15/2024

import java.util.ArrayList;

public class Championship{
    private ArrayList<Driver> drivers;
    private ArrayList<Venue> venues;

    public static final int MINOR_MECHANICAL_FAULT = 5;
    public static final int MAJOR_MECHANICAL_FAULT = 3;
    public static final int UNRECOVERABLE_MECHANICAL_FAULT = 1;

    public ArrayList<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(ArrayList<Driver> drivers) {
        this.drivers = drivers;
    }

    public ArrayList<Venue> getVenues() {
        return venues;
    }

    public void setVenues(ArrayList<Venue> venues) {
        this.venues = venues;
    }

    public Championship() {
        this.drivers = new ArrayList<>();
        this.venues = new ArrayList<>();
    }

    public Championship(String drivers_path, String venues_path) {
        this.drivers = readDriversFromFile(drivers_path);
        this.venues = readVenuesFromFile(venues_path);
    }

    private ArrayList<Driver> readDriversFromFile(String drivers_path) {
        ArrayList<Driver> ret = new ArrayList<>();
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(drivers_path))) {
            String line;
            while ((line = br.readLine()) != null) {
                // each line contains driver data in a specific format: "name,ranking,specialSkill"
                String[] parts = line.split(",");
                String name = parts[0].trim();
                int ranking = Integer.parseInt(parts[1].trim());
                String specialSkill = parts[2].trim();
                Driver driver = new Driver(name, ranking, specialSkill);
                ret.add(driver);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
    }
    return ret;
    }

    private ArrayList<Venue> readVenuesFromFile(String venues_path) {
        ArrayList<Venue> ret = new ArrayList<>();
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(venues_path))) {
            String line;
            while ((line = br.readLine()) != null) {
                // each line contains venue data in a specific format: "name,numberOfLaps,averageLapTime, chanceOfRain"
                String[] parts = line.split(",");
                String name = parts[0].trim();
                int numberOfLaps = Integer.parseInt(parts[1].trim());
                int averageLapTime = Integer.parseInt(parts[2].trim());
                double chanceOfRain = Double.parseDouble(parts[3].trim());
                Venue venue = new Venue(name, numberOfLaps, averageLapTime, chanceOfRain);
                ret.add(venue);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void prepareForTheRace() {
        for (Driver driver : drivers) {
            driver.setEligibleToRace(true);
            driver.setAccumulatedTime(0);
            driver.setAccumulatedPoints(0);
            driver.setWinterTireUsed(false);
        }
    }

    public void driveAverageLapTime(int lapTime){
        for (Driver driver : drivers) {
            if (!driver.isEligibleToRace()) {
                continue; // Skip drivers who are not eligible
            }
            driver.setAccumulatedTime(driver.getAccumulatedTime() + lapTime);
            driver.incrementLapCount();
        }
    }

    public void applySpecialSkill(){
        for (Driver driver : drivers) {
            driver.useSpecialSkill();
        }
    }

    public void checkMechanicalProblem(){
        for (Driver driver : drivers) {
            RNG rng = new RNG(1, 100);
            if (rng.getRandomValue() <= MINOR_MECHANICAL_FAULT) {
                driver.setAccumulatedTime(driver.getAccumulatedTime() + 20); // add penalty
                System.out.println(driver.getName() + " je imao manji mehanicki kvar i dobija kaznu od 20 sekundi.");
            } else if(rng.getRandomValue() <= MAJOR_MECHANICAL_FAULT){
                driver.setAccumulatedTime(driver.getAccumulatedTime() + 120);
                System.out.println(driver.getName() + " je imao veci mehanicki kvar i dobija kaznu od 120 sekundi.");
            } else if(rng.getRandomValue() <= UNRECOVERABLE_MECHANICAL_FAULT){
                driver.setEligibleToRace(false);
                System.out.println(driver.getName() + " je imao nepopravljiv mehanicki kvar i izbacen je iz trke.");
            }
        }
    }

    public void checkForRain(double chanceOfRain, int lapCount){
        RNG rng = new RNG(1, 100);
        RNG rng2 = new RNG(0, 1);
        boolean isRaining = rng.getRandomValue() <= chanceOfRain * 100;
        for (Driver driver : drivers) {
            if(lapCount == 2 && rng2.getRandomValue() == 1 && !driver.getWinterTireUsed()){
                driver.setWinterTireUsed(true);
                driver.setAccumulatedTime(driver.getAccumulatedTime() + 10);
                System.out.println(driver.getName() + " je presao na zimske gume. Dodato 10 sekundi vremena.");
            }
        }

        if (isRaining) {
            System.out.println("Pada kisa! Nadamo se da vozaci imaju pneumatike za kisu.");
            for (Driver driver : drivers) {
                if (!driver.getWinterTireUsed()) {
                    driver.setAccumulatedTime(driver.getAccumulatedTime() + 5); // penalty for switching to winter tires
                    System.out.println(driver.getName() + " nema zimske gume i dobio je 5 sekundi kazne.");
                }
            }
        } else {
            System.out.println("Nema kise ovaj krug.");
        }
    }

    public void printLeader(int lap){
        Driver leader = null;
        int minTime = Integer.MAX_VALUE;
        for (Driver driver : drivers) {
            if (driver.getAccumulatedTime() < minTime) {
                minTime = driver.getAccumulatedTime();
                leader = driver;
            }
        }
        if (leader != null) {
            System.out.println("Prvi nakon " + lap + ". kruga je: " + leader.getName() + " (Vreme: " + leader.getAccumulatedTime() + ")");
        } else {
            System.out.println("Nema pobednika trenutno.");
        }
    }

    public void printWinnersAfterRace(String venueName){
            // TODO ispiši imena pobednika
            // (četiri najbolje rangirana vozača) na stazi venueName
    }

    public void printChampion(int numOfRaces){
        // TODO  ispisati poruku o tome ko je
        // šampion na kraju šampionata, tj. nakon numOfRaces odvozanih trka
    }
}