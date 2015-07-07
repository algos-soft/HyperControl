package it.technocontrolsystem.hypercontrol.domain;

import it.technocontrolsystem.hypercontrol.database.Tables;

/**
 *
 */
public enum AreaSensorFields {
    ID("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
    IDAREA("idarea","INTEGER REFERENCES "+ Tables.AREAS.getName()+"("+AreaFields.ID.getName()+") ON DELETE CASCADE"),
    IDSENSOR("idsensor","INTEGER");

    private String name;
    private String type;

    AreaSensorFields(String name, String type) {
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
