// Marko Savic e1 15/2024

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

    public Championship(ArrayList<Driver> drivers, ArrayList<Venue> venues) {
        this.drivers = drivers;
        this.venues = venues;
    }

    public void prepareForTheRace() {
        for (Driver driver : drivers) {
            // TODO inicijalizacija atributa za svakog vozaca
        }
    }

    public void driveAverageLapTime(){
        for (Driver driver : drivers) {
            // TODO svakom vozaču koji vozi trku, dodeli srednje
            // vreme voženja kruga (određeno samom stazom)
        }
    }

    public void applySpecialSkill(){
        for (Driver driver : drivers) {
            // TODO  primeni specijalne veštine za svakog vozača u datom krugu
        }
    }

    public void checkMechanicalProblem(){
        for (Driver driver : drivers) {
            // TODO  proveri da li su neki vozači imali kvar (jedan od tri)
            // i ako treba ažuriraj im vreme voženja kruga, odnosno atribut
            // eligibleToRace
        }
    }

    public void printLeader(int lap){
        // TODO ispiši ko je na prvom mestu nakon kruga lap
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