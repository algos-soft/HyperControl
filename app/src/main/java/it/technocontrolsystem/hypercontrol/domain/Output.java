package it.technocontrolsystem.hypercontrol.domain;

import java.util.ArrayList;

import it.technocontrolsystem.hypercontrol.database.DB;

/**
 *
 */
public class Output {

    private int id;
    private int number;
    private String name;

    private ArrayList<PlantEntry> plantEntries = new ArrayList();

//    private  int idSite;
    private int[] areaIds=new int[0];

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public int getIdSite() {
//        return idSite;
//    }
//
//    public void setIdSite(int idSite) {
//        this.idSite = idSite;
//    }

//    public Area[] getAreas() {
//        return DB.getAreasBySensor(getId());
//    }

    /**
     * Ritorna gli id di tutte le aree
     */
    public int[] getAreaIds() {
        return areaIds;
    }

    public void setAreaIds(int[] areaIds) {
        this.areaIds = areaIds;
    }

//    /**
//     * Aggiunge un id di area
//     */
//    public void addAreaId(int areaid){
//        areaIds.add(areaid);
//    }


    /**
     * Aggiunge un impianto
     */
    public PlantEntry addPlantEntry(int numPlant) {
        PlantEntry entry = new PlantEntry(numPlant);
        plantEntries.add(entry);
        return entry;
    }


    /**
     * Classe interna per immagazzinare la struttura impianti e aree
     */
    public class PlantEntry {
        int plantNumber;
        ArrayList<Integer> areaNumbers = new ArrayList<>();

        PlantEntry(int plantNumber) {
            this.plantNumber = plantNumber;
        }

        public void addArea(int numArea){
            areaNumbers.add(numArea);
        }

    }


}
