package it.technocontrolsystem.hypercontrol.database;

/**
 * enum for database tables
 */
public enum Tables {
    SITES("sites"),
    PLANTS("plants"),
    AREAS("areas"),
    SENSORS("sensors"),
    AREA_SENSOR("areasensor"),
    BOARDS("boards"),
    MENUS("menus");

    private String name;

    Tables(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
