package it.technocontrolsystem.hypercontrol.domain;

import java.util.ArrayList;

import it.technocontrolsystem.hypercontrol.database.DB;

/**
 * Superclasse astratta di Sensori e Uscite
 */
public abstract class AbsInOut {
    private int id;
    private int number;
    private String name;
    private int idSite;

    private int[] areaIds = new int[0];

    protected ArrayList<PlantEntry> plantEntries = new ArrayList();

    /**
     * Aggiunge un impianto
     */
    public PlantEntry addPlantEntry(int numPlant) {
        PlantEntry entry = new PlantEntry(numPlant);
        plantEntries.add(entry);
        return entry;
    }

    /**
     * Ritorna gli id di tutte le aree
     */
    public int[] getAreaIds() {
        return areaIds;
    }

    public void setAreaIds(int[] areaIds) {
        this.areaIds = areaIds;
    }

    public int getIdSite() {
        return idSite;
    }

    public void setIdSite(int idSite) {
        this.idSite = idSite;
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

    public void setNumber(int number) {
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Risolve le aree a cui questo oggetto appartiene.
     * In base al contenuto delle struttura PlantEntry, recupera
     * gli id delle aree e li mette nell'apposito array
     */
    public void resolveAreas() throws Exception {

        if (idSite > 0) {
            ArrayList<Integer> aids = new ArrayList();
            aids.clear();
            for (PlantEntry entry : plantEntries) {
                int plantNum = entry.getPlantNumber();
                ArrayList<Integer> areaNumbers = entry.getAreaNumbers();
                for (int areaNum : areaNumbers) {
                    Plant plant = DB.getPlantBySiteAndNumber(idSite, plantNum);
                    Area area = DB.getAreaByIdPlantAndAreaNumber(plant.getId(), areaNum);
                    aids.add(area.getId());
                }
            }

            // to int array
            int[] ints = new int[aids.size()];
            for(int i=0; i<aids.size();i++){
                ints[i]=aids.get(i);
            }
            setAreaIds(ints);

        } else {
            throw new Exception("id site not assigned to object - cannot resolve areas");
        }
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

        public void addArea(int numArea) {
            areaNumbers.add(numArea);
        }

        public int getPlantNumber() {
            return plantNumber;
        }

        public ArrayList<Integer> getAreaNumbers() {
            return areaNumbers;
        }
    }

}
