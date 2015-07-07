package it.technocontrolsystem.hypercontrol.domain;

import it.technocontrolsystem.hypercontrol.database.Tables;

/**
 *
 */
public enum PlantFields {
    ID("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
    IDSITE("idsite","INTEGER REFERENCES "+ Tables.SITES.getName()+"("+SiteFields.ID.getName()+") ON DELETE CASCADE"),
    NUMBER("number","INTEGER"),
    NAME("name", "TEXT");

    private String name;
    private String type;

    PlantFields(String name, String type) {
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
