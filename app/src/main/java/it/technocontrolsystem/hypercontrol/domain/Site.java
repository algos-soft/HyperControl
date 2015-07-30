package it.technocontrolsystem.hypercontrol.domain;

import it.technocontrolsystem.hypercontrol.database.DB;

/**
 * Class rappresenting a single site
 */
public class Site {
    private int id;
    private String name;
    private String address;
    private String username;
    private String password;
    private String uuid;

    private int port;
    private int version;//federico

    public Site(String name, String address, int port, String username, String password, String uuid, int version) {//federico
        this.name = name;
        this.address = address;
        this.username = username;
        this.password = password;
        this.uuid = uuid;
        this.port = port;
        this.version = version;//federico
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setVersion(int version) {//federico
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getPort() {
        return port;
    }

    public int getVersion() {
        return version;
    }//federico

    public Plant[] getPlants() {
        return DB.getPlants(getId());
    }

    public Board[] getBoards() {
        return DB.getBoards(getId());
    }

    public Menu[] getMenus() {
        return DB.getMenus(getId());
    }

}
