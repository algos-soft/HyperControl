package it.technocontrolsystem.hypercontrol.domain;

import it.technocontrolsystem.hypercontrol.database.Tables;

/**
 *
 */
public enum AreaFields {
    ID("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
    IDPLANT("idplant","INTEGER REFERENCES "+ Tables.PLANTS.getName()+"("+PlantFields.ID.getName()+") ON DELETE CASCADE"),
    NUMBER("number","INTEGER"),
    NAME("name", "TEXT");

    private String name;
    private String type;

    AreaFields(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getName() {

        return name;
    }

}
