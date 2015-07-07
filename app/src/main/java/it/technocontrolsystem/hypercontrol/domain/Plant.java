package it.technocontrolsystem.hypercontrol.domain;

import java.util.ArrayList;

import it.technocontrolsystem.hypercontrol.database.DB;

/**
 * Domain Class for a single plant
 */
public class Plant {
    private int id;
    private int idSite;
    private int number;
    private String name;
    private ArrayList<Area> areas=new ArrayList<Area>();

    public void setId(int id) {
        this.id = id;
    }

    public void setIdSite(int idSite) {
        this.idSite = idSite;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getIdSite() {
        return idSite;
    }

    public Site getSite(){
        return DB.getSite(idSite);
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

     public Area[] getAreas() {
        return areas.toArray(new Area[0]);
    }

    public void addArea(Area area){
        areas.add(area);
    }
}
