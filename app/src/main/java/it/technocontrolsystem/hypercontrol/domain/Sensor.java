package it.technocontrolsystem.hypercontrol.domain;

import java.util.ArrayList;

import it.technocontrolsystem.hypercontrol.database.DB;

/**
*Domain Class for a single area.
 * A plant can be present in many areas.
*/
public class Sensor {
    private int id;
    private int number;
    private String name;
    private  int tipo;
    private int idPlant;
    private int numPlant;
    private ArrayList<Integer> areaNums=new ArrayList<Integer>();
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

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public SensorType getSensorType(){
        return SensorType.get(getTipo());
    }

    public int getIdPlant() {
        return idPlant;
    }

    public void setIdPlant(int idPlant) {
        this.idPlant = idPlant;
    }

    public int getNumPlant() {
        return numPlant;
    }

    public void setNumPlant(int numPlant) {
        this.numPlant = numPlant;
    }

    public Area[] getAreas() {
        return DB.getAreasBySensor(getId());
    }

    public int[] getAreaIds() {
        return areaIds;
    }

    public void setAreaIds(int[] areaIds) {
        this.areaIds = areaIds;
    }

    public void addAreaNumber(int areaNumber){
        areaNums.add(areaNumber);
    }

    public Integer[] getAreaNums() {
        return areaNums.toArray(new Integer[0]);
    }
}
