/*
package it.technocontrolsystem.hypercontrol;

import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.domain.Plant;
import it.technocontrolsystem.hypercontrol.domain.Sensor;
import it.technocontrolsystem.hypercontrol.domain.Site;

*/
/**
 *
 *//*

public class DemoData {


    public static void create(int idSite) {
        createPlants(idSite);

        createCrossSensors();

    }

    private static void createPlants(int idSite) {
        Plant plant;
        int idPlant;

        plant = new Plant();
        plant.setName("antifurto");
        plant.setIdSite(idSite);
        plant.setNumber(1);
        idPlant = DB.savePlant(plant);
        createAreas(idPlant);

        plant = new Plant();
        plant.setName("anti-incendio");
        plant.setIdSite(idSite);
        plant.setNumber(2);
        idPlant = DB.savePlant(plant);
        createAreas(idPlant);

        plant = new Plant();
        plant.setName("irrigazione");
        plant.setIdSite(idSite);
        plant.setNumber(3);
        idPlant = DB.savePlant(plant);
        createAreas(idPlant);
    }


    private static void createAreas(int idPlant) {

        Area area;
        int idArea;

        area = new Area();
        area.setName("Primo piano");
        area.setIdPlant(idPlant);
        area.setNumber(1);
        idArea = DB.saveArea(area);
        createSensors(idArea);

        area = new Area();
        area.setName("Secondo piano");
        area.setIdPlant(idPlant);
        area.setNumber(2);
        idArea = DB.saveArea(area);
        createSensors(idArea);

        area = new Area();
        area.setName("Giardino");
        area.setIdPlant(idPlant);
        area.setNumber(3);
        idArea = DB.saveArea(area);
        createSensors(idArea);

        area = new Area();
        area.setName("Garage");
        area.setIdPlant(idPlant);
        area.setNumber(4);
        idArea = DB.saveArea(area);
        createSensors(idArea);


    }

    private static void createSensors(int idArea) {

        Sensor sensor;
        int[] areas = {idArea};

        sensor = new Sensor();
        sensor.setName("Volumetrico");
        sensor.setNumber(1005);
        sensor.setAreaIds(areas);
        DB.saveSensor(sensor);

        sensor = new Sensor();
        sensor.setName("Magnetico");
        sensor.setNumber(1044);
        sensor.setAreaIds(areas);
        DB.saveSensor(sensor);

        sensor = new Sensor();
        sensor.setName("Passaggio");
        sensor.setNumber(2001);
        sensor.setAreaIds(areas);
        DB.saveSensor(sensor);

        sensor = new Sensor();
        sensor.setName("Barriera");
        sensor.setNumber(2002);
        sensor.setAreaIds(areas);
        DB.saveSensor(sensor);


    }

    private static void createCrossSensors(){
        Sensor sensor;

        sensor = new Sensor();
        int[] areas1={1,2};
        sensor.setName("Barriera cross");
        sensor.setNumber(5000);
        sensor.setAreaIds(areas1);
        DB.saveSensor(sensor);


        sensor = new Sensor();
        int[] areas2={2,3,4};
        sensor.setName("Volumetrico cross");
        sensor.setNumber(6000);
        sensor.setAreaIds(areas2);
        DB.saveSensor(sensor);
    }
}
*/
