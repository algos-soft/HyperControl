package it.technocontrolsystem.hypercontrol.domain;

import it.technocontrolsystem.hypercontrol.database.DB;

/**
 * Domain Class for a single area.
 * An Area belongs to a Plant.
*/
public class Area {
    private int id;
    private int idPlant;
    private int number;
    private String name;

    private int[] sensorsIds =new int[0];


    public int getIdPlant() {
        return idPlant;
    }



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

    public void setIdPlant(int idPlant) {
        this.idPlant = idPlant;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Plant getPlant(){
        return DB.getPlant(idPlant);
    }

//    public void setSensors(Sensor[] sensors){
//
//    }

    public Sensor[] getSensors() {

        return DB.getSensorsByArea(getId());
    }

    public int[] getSensorsIds() {
        return sensorsIds;
    }

    public void setSensorsIds(int[] sensorsIds) {
        this.sensorsIds = sensorsIds;
    }


}
