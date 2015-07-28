package it.technocontrolsystem.hypercontrol.domain;

import it.technocontrolsystem.hypercontrol.database.Tables;

/**
 * Created by Federico on 27/07/2015.
 */
public enum OutputFields {
    ID("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
    NUMBER("number","INTEGER"),
    NAME("name", "TEXT"),
    TYPE("type", "INTEGER"),
    IDSITE("idsite","INTEGER REFERENCES "+ Tables.SITES.getName()+"("+SiteFields.ID.getName()+") ON DELETE CASCADE")
    ;

    private String name;
    private String type;

    OutputFields(String name, String type) {
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
