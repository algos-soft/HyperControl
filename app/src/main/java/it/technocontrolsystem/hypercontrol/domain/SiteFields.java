package it.technocontrolsystem.hypercontrol.domain;

/**
 *
 */
public enum SiteFields {
    ID("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
    NAME("name", "TEXT"),
    ADDRESS("address", "TEXT"),
    PORT("port","INTEGER"),
    USER("user", "TEXT"),
    PASSWORD("password", "TEXT"),
    UUID("uuid", "TEXT"),
    VERSION("version","INTEGER");//federico

    private String name;
    private String type;

    SiteFields(String name, String type) {
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
