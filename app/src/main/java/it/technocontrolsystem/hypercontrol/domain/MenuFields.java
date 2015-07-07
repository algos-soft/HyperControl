package it.technocontrolsystem.hypercontrol.domain;

import it.technocontrolsystem.hypercontrol.database.Tables;

/**
 * Created by Federico on 30/03/2015.
 */
public enum MenuFields {
    ID("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
    IDSITE("idsite","INTEGER REFERENCES "+ Tables.SITES.getName()+"("+SiteFields.ID.getName()+") ON DELETE CASCADE"),
    NUMBER("number","INTEGER"),
    NAME("name", "TEXT"),
    ACTION("action", "INTEGER"),
    PAGE("page", "INTEGER");

    private String name;
    private String type;

    MenuFields(String name, String type) {
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
