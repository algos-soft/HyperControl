package it.technocontrolsystem.hypercontrol.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.domain.AreaFields;
import it.technocontrolsystem.hypercontrol.domain.AreaSensorFields;
import it.technocontrolsystem.hypercontrol.domain.Board;
import it.technocontrolsystem.hypercontrol.domain.BoardFields;
import it.technocontrolsystem.hypercontrol.domain.Menu;
import it.technocontrolsystem.hypercontrol.domain.MenuFields;
import it.technocontrolsystem.hypercontrol.domain.Plant;
import it.technocontrolsystem.hypercontrol.domain.PlantFields;
import it.technocontrolsystem.hypercontrol.domain.Sensor;
import it.technocontrolsystem.hypercontrol.domain.SensorFields;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.domain.SiteFields;

/**
 *
 */
public class DB extends SQLiteOpenHelper {

    private static DB DATABASE;

    public static final String DBNAME = "hpdb";
    private static final int DATABASE_VERSION = 1;

    public DB() {
        super(HyperControlApp.getContext(), DBNAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getSitesTableCreateString());

        db.execSQL(getPlantsTableCreateString());

        db.execSQL(getAreasTableCreateString());

        db.execSQL(getSensorsTableCreateString());

        db.execSQL(getAreaSensorTableCreateString());

        db.execSQL(getBoardsTableCreateString());

        db.execSQL(getMenusTableCreateString());


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }


    private String getSitesTableCreateString() {

        String s;
        s = "CREATE TABLE " + Tables.SITES.getName() + " (";

        SiteFields field;
        SiteFields[] fields = SiteFields.values();
        for (int i = 0; i < fields.length; i++) {
            field = fields[i];
            s += field.getName() + " " + field.getType();
            if (i < fields.length - 1) {
                s += ", ";
            }
        }
        s += ");";

        return s;
    }


    /**
     * Crea o aggiunge un record al database
     *
     * @param table    la tavola
     * @param idField  l'id del field
     * @param values   i valori da scrivere
     * @param updateId 0 per creare nuovo, o l'id da aggiornare
     * @return l'id del record creato o modificato
     */
    private static int save(String table, String idField, ContentValues values, int updateId) {
        int id = 0;
        if (updateId == 0) {
            id = (int) getWritableDb().insert(table, null, values);
        } else {
            String where = idField + "=" + updateId;
            getWritableDb().update(table, values, where, null);
            id = updateId;
        }

        return id;

    }


    public static int getCount(String table) {
        String query = "SELECT COUNT(*) FROM " + table;
        return (int) DatabaseUtils.longForQuery(getReadableDb(), query, null);
    }


    // ================ END PARTE COMUNE ===================


    // ================ START SITES ===================


    public static int saveSite(Site site) throws Exception {
        ContentValues values = new ContentValues();
        values.put(SiteFields.NAME.getName(), site.getName());
        values.put(SiteFields.ADDRESS.getName(), site.getAddress());
        values.put(SiteFields.PORT.getName(), site.getPort());
        values.put(SiteFields.USER.getName(), site.getUsername());
        values.put(SiteFields.PASSWORD.getName(), site.getPassword());
        values.put(SiteFields.VERSION.getName(), site.getVersion());//federico
        return save(Tables.SITES.getName(), SiteFields.ID.getName(), values, site.getId());
    }

//    private static String getCreateSiteString(Site site) {
//        String sql = "INSERT INTO " + Tables.SITES.getName() + " (";
//        sql += SiteFields.NAME.getName() + ", ";
//        sql += SiteFields.ADDRESS.getName() + ", ";
//        sql += SiteFields.USER.getName() + ", ";
//        sql += SiteFields.PASSWORD.getName() + ") ";
//        sql += "VALUES (";
//        sql += "'" + site.getName() + "', ";
//        sql += "'" + site.getAddress() + "', ";
//        sql += "'" + site.getUsername() + "', ";
//        sql += "'" + site.getPassword() + "')";
//        return sql;
//    }


//    private static String getUpdateSiteString(Site site) {
//        String sql = "UPDATE " + Tables.SITES.getName() + " SET ";
//        sql += SiteFields.NAME.getName() + "='" + site.getName() + "', ";
//        sql += SiteFields.ADDRESS.getName() + "='" + site.getAddress() + "', ";
//        sql += SiteFields.USER.getName() + "='" + site.getUsername() + "', ";
//        sql += SiteFields.PASSWORD.getName() + "='" + site.getPassword() + "'";
//        sql += " WHERE " + SiteFields.ID.getName() + "=" + site.getId();
//        return sql;
//    }


    public static SQLiteDatabase getWritableDb() {
        return getDb().getWritableDatabase();
    }

    public static SQLiteDatabase getReadableDb() {
        return getDb().getReadableDatabase();
    }

    public static DB getDb() {
        if (DATABASE == null) {
            DATABASE = new DB();
        }
        return DATABASE;
    }

    public static Site[] getSites() {
        ArrayList<Site> sites = new ArrayList<Site>();
        String sql;
        sql = "SELECT " + SiteFields.ID.getName() + " FROM " + Tables.SITES.getName();
        Cursor cur = getReadableDb().rawQuery(sql, null);
        while (cur.moveToNext()) {
            int idx = cur.getColumnIndex(SiteFields.ID.getName());
            int id = cur.getInt(idx);
            Site site = getSite(id);
            sites.add(site);
        }

        Site[] aSites = sites.toArray(new Site[0]);
        return aSites;

    }

    public static Site getSite(int id) {
        Site site = null;
        String sql;
        sql = "SELECT * FROM " + Tables.SITES.getName();
        sql += " WHERE " + SiteFields.ID.getName() + "=" + id;
        Cursor cur = getReadableDb().rawQuery(sql, null);

        if (cur.getCount() > 0) {

            cur.moveToFirst();

            int idx;

            idx = cur.getColumnIndex(SiteFields.NAME.getName());
            String name = cur.getString(idx);

            idx = cur.getColumnIndex(SiteFields.ADDRESS.getName());
            String address = cur.getString(idx);

            idx = cur.getColumnIndex(SiteFields.PORT.getName());
            int port = cur.getInt(idx);

            idx = cur.getColumnIndex(SiteFields.USER.getName());
            String user = cur.getString(idx);

            idx = cur.getColumnIndex(SiteFields.PASSWORD.getName());
            String password = cur.getString(idx);

            idx = cur.getColumnIndex(SiteFields.VERSION.getName());//federico
            int version = cur.getInt(idx);//federico

            site = new Site(name, address, port, user, password, version);//federico
            site.setId(id);

        }

        cur.close();

        return site;
    }

    public static void deleteSite(int id) {
        String sql = "DELETE FROM " + Tables.SITES.getName();
        sql += " WHERE " + SiteFields.ID.getName() + "=" + id;
        getWritableDb().execSQL(sql);

    }

    public static int getSitesCount() {
        return getCount(Tables.SITES.getName());
    }

    //federico
    public static void updateSiteVersion(int id, int vers) {
        String sql = "UPDATE " + Tables.SITES.getName();
        sql += " SET " + SiteFields.VERSION.getName() + "=" + vers;
        sql += " WHERE " + SiteFields.ID.getName() + "=" + id;
        getWritableDb().execSQL(sql);

    }

    // ================ START PLANTS ===================

    private String getPlantsTableCreateString() {

        String s;
        s = "CREATE TABLE " + Tables.PLANTS.getName() + " (";

        PlantFields field;
        PlantFields[] fields = PlantFields.values();
        for (int i = 0; i < fields.length; i++) {
            field = fields[i];
            s += field.getName() + " " + field.getType();
            if (i < fields.length - 1) {
                s += ", ";
            }
        }
        s += ");";

        return s;
    }

    public static int savePlant(Plant plant) {
        ContentValues values = new ContentValues();
        values.put(PlantFields.IDSITE.getName(), plant.getIdSite());
        values.put(PlantFields.NUMBER.getName(), plant.getNumber());
        values.put(PlantFields.NAME.getName(), plant.getName());
        return save(Tables.PLANTS.getName(), PlantFields.ID.getName(), values, plant.getId());
    }

    public static Plant[] getPlants(int idSite) {
        ArrayList<Plant> plants = new ArrayList<Plant>();
        String sql;
        sql = "SELECT " + PlantFields.ID.getName() + " FROM " + Tables.PLANTS.getName();
        sql += " WHERE " + PlantFields.IDSITE.getName() + "=" + idSite;
        Cursor cur = getReadableDb().rawQuery(sql, null);
        while (cur.moveToNext()) {
            int idx = cur.getColumnIndex(PlantFields.ID.getName());
            int id = cur.getInt(idx);
            Plant plant = getPlant(id);
            plants.add(plant);
        }

        cur.close();

        Plant[] aplant = plants.toArray(new Plant[0]);
        return aplant;

    }

    public static Plant getPlant(int id) {
        String sql;
        sql = "SELECT * FROM " + Tables.PLANTS.getName();
        sql += " WHERE " + PlantFields.ID.getName() + "=" + id;
        Cursor cur = getReadableDb().rawQuery(sql, null);
        cur.moveToFirst();

        int idx;

        idx = cur.getColumnIndex(PlantFields.IDSITE.getName());
        int idsite = cur.getInt(idx);

        idx = cur.getColumnIndex(PlantFields.NUMBER.getName());
        int number = cur.getInt(idx);

        idx = cur.getColumnIndex(PlantFields.NAME.getName());
        String name = cur.getString(idx);

        cur.close();

        Plant plant = new Plant();

        plant.setId(id);
        plant.setIdSite(idsite);
        plant.setNumber(number);
        plant.setName(name);

        return plant;
    }

    public static void deletePlant(int id) {
        String sql = "DELETE FROM " + Tables.PLANTS.getName();
        sql += " WHERE " + PlantFields.ID.getName() + "=" + id;
        getWritableDb().execSQL(sql);

    }

    public static int getPlantsCount() {
        return getCount(Tables.PLANTS.getName());
    }

    public static int getPlantsCountBySite(int idSite) {
        String query = "SELECT COUNT(*) FROM " + Tables.PLANTS.getName() + " WHERE " + PlantFields.IDSITE.getName() + "=" + idSite;
        return (int) DatabaseUtils.longForQuery(getReadableDb(), query, null);
    }


    /**
     * Ritorna un Plant per id sito e numero di plant nel sito
     */
    public static Plant getPlantBySiteAndNumber(int idSite, int plantNumber) {
        Plant plant = null;
        int idPlant = 0;
        String sql;
        String[] columns = {PlantFields.ID.getName()};
        String selection = PlantFields.IDSITE.getName() + "=" + idSite + " AND " + PlantFields.NUMBER.getName() + "=" + plantNumber;
        Cursor cur = getReadableDb().query(Tables.PLANTS.getName(), columns, selection, null, null, null, null);
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            idPlant = cur.getInt(cur.getColumnIndex(PlantFields.ID.getName()));
        }

        cur.close();

        if (idPlant > 0) {
            plant = getPlant(idPlant);
        }
        return plant;
    }


    // ================ START AREAS ===================

    private String getAreasTableCreateString() {

        String s;
        s = "CREATE TABLE " + Tables.AREAS.getName() + " (";

        AreaFields field;
        AreaFields[] fields = AreaFields.values();
        for (int i = 0; i < fields.length; i++) {
            field = fields[i];
            s += field.getName() + " " + field.getType();
            if (i < fields.length - 1) {
                s += ", ";
            }
        }
        s += ");";

        return s;
    }

    public static int saveArea(Area area) {
        int id = 0;
        ContentValues values = new ContentValues();
        values.put(AreaFields.IDPLANT.getName(), area.getIdPlant());
        values.put(AreaFields.NUMBER.getName(), area.getNumber());
        values.put(AreaFields.NAME.getName(), area.getName());
        id = save(Tables.AREAS.getName(), AreaFields.ID.getName(), values, area.getId());
        area.setId(id);
        //delete all the area rows in the cross-table
        String where = AreaSensorFields.IDAREA + "=" + id;
        getWritableDb().delete(Tables.AREA_SENSOR.getName(), where, null);

        //create the new area rows in the cross-table

        for (int idSensor : area.getSensorsIds()) {
            values = new ContentValues();
            values.put(AreaSensorFields.IDAREA.getName(), id);
            values.put(AreaSensorFields.IDSENSOR.getName(), idSensor);
            getWritableDb().insert(Tables.AREA_SENSOR.getName(), null, values);
        }

        return id;
    }

    public static Area[] getAreasByPlant(int idPlant) {
        ArrayList<Area> areas = new ArrayList<Area>();
        String sql;
        sql = "SELECT " + AreaFields.ID.getName() + " FROM " + Tables.AREAS.getName();
        sql += " WHERE " + AreaFields.IDPLANT.getName() + "=" + idPlant;
        Cursor cur = getReadableDb().rawQuery(sql, null);
        while (cur.moveToNext()) {
            int idx = cur.getColumnIndex(AreaFields.ID.getName());
            int id = cur.getInt(idx);
            Area area = getArea(id);
            areas.add(area);
        }

        cur.close();

        Area[] aarea = areas.toArray(new Area[0]);
        return aarea;

    }

    public static Area getArea(int id) {
        Area area = null;
        if (id > 0) {
            String sql;
            sql = "SELECT * FROM " + Tables.AREAS.getName();
            sql += " WHERE " + AreaFields.ID.getName() + "=" + id;
            Cursor cur = getReadableDb().rawQuery(sql, null);
            cur.moveToFirst();

            int idx;

            idx = cur.getColumnIndex(AreaFields.IDPLANT.getName());
            int idplant = cur.getInt(idx);

            idx = cur.getColumnIndex(AreaFields.NUMBER.getName());
            int number = cur.getInt(idx);

            idx = cur.getColumnIndex(AreaFields.NAME.getName());
            String name = cur.getString(idx);

            cur.close();

            area = new Area();

            area.setId(id);
            area.setIdPlant(idplant);
            area.setNumber(number);
            area.setName(name);

            area.setSensorsIds(getSensorIdsByArea(area.getId()));
        }
        return area;

    }

    public static Area[] getAreasBySensor(int idSensor) {
        ArrayList<Area> areas = new ArrayList<Area>();
        int[] areaIds = getAreaIdsBySensor(idSensor);
        for (int id : areaIds) {
            Area area = getArea(id);
            areas.add(area);
        }
        Area[] aarea = areas.toArray(new Area[0]);
        return aarea;
    }

    public static int[] getAreaIdsBySensor(int idSensor) {
        String[] columns = {AreaSensorFields.IDAREA.getName()};
        String selection = AreaSensorFields.IDSENSOR.getName() + "=" + idSensor;
        Cursor cur = getReadableDb().query(Tables.AREA_SENSOR.getName(), columns, selection, null, null, null, null);
        int[] aarea = new int[cur.getCount()];
        int i = 0;
        int idx = cur.getColumnIndex(AreaSensorFields.IDAREA.getName());
        while (cur.moveToNext()) {
            int idArea = cur.getInt(idx);
            aarea[i] = idArea;
            i++;
        }

        cur.close();

        return aarea;
    }


    /**
     * Ritorna un Area per id plant e numero di area nel plant
     */
    public static Area getAreaByIdPlantAndAreaNumber(int idPlant, int areaNumber) {
        Area area = null;
        int idArea = 0;
        String[] columns = {AreaFields.ID.getName()};
        String selection = AreaFields.IDPLANT.getName() + "=" + idPlant + " AND " + AreaFields.NUMBER.getName() + "=" + areaNumber;
        Cursor cur = getReadableDb().query(Tables.AREAS.getName(), columns, selection, null, null, null, null);
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            idArea = cur.getInt(cur.getColumnIndex(AreaFields.ID.getName()));
        }

        cur.close();

        if (idArea > 0) {
            area = getArea(idArea);
        }
        return area;
    }

    public static int getAreasCount() {
        return getCount(Tables.AREAS.getName());
    }

    public static int getAreasCountByPlant(int idPlant) {
        String query = "SELECT COUNT(*) FROM " + Tables.AREAS.getName() + " WHERE " + AreaFields.IDPLANT.getName() + "=" + idPlant;
        return (int) DatabaseUtils.longForQuery(getReadableDb(), query, null);
    }


    // ================ START SENSORS ===================

    private String getSensorsTableCreateString() {

        String s;
        s = "CREATE TABLE " + Tables.SENSORS.getName() + " (";

        SensorFields field;
        SensorFields[] fields = SensorFields.values();
        for (int i = 0; i < fields.length; i++) {
            field = fields[i];
            s += field.getName() + " " + field.getType();
            if (i < fields.length - 1) {
                s += ", ";
            }
        }
        s += ");";

        return s;
    }

    public static int saveSensor(Sensor sensor) {

        int id = 0;
        ContentValues values = new ContentValues();
        values.put(SensorFields.NUMBER.getName(), sensor.getNumber());
        values.put(SensorFields.NAME.getName(), sensor.getName());
        values.put(SensorFields.TYPE.getName(), sensor.getTipo());
        id = save(Tables.SENSORS.getName(), SensorFields.ID.getName(), values, sensor.getId());
        sensor.setId(id);

        //delete all the sensor rows in the cross-table
        String where = AreaSensorFields.IDSENSOR + "=" + id;
        getWritableDb().delete(Tables.AREA_SENSOR.getName(), where, null);

        //create the new sensor rows in the cross-table
        for (int idArea : sensor.getAreaIds()) {
            values = new ContentValues();
            values.put(AreaSensorFields.IDAREA.getName(), idArea);
            values.put(AreaSensorFields.IDSENSOR.getName(), id);
            getWritableDb().insert(Tables.AREA_SENSOR.getName(), null, values);
        }

        return id;

    }

    public static Sensor[] getSensorsByArea(int idArea) {
        ArrayList<Sensor> sensors = new ArrayList<Sensor>();
        int[] sensorIds = getSensorIdsByArea(idArea);
        for (int id : sensorIds) {
            Sensor sensor = getSensor(id);
            sensors.add(sensor);
        }
        Sensor[] asensor = sensors.toArray(new Sensor[0]);
        return asensor;
    }

    public static int[] getSensorIdsByArea(int idArea) {
        String[] columns = {AreaSensorFields.IDSENSOR.getName()};
        String selection = AreaSensorFields.IDAREA.getName() + "=" + idArea;
        Cursor cur = getReadableDb().query(Tables.AREA_SENSOR.getName(), columns, selection, null, null, null, null);
        int[] asensor = new int[cur.getCount()];
        int i = 0;
        int idx = cur.getColumnIndex(AreaSensorFields.IDSENSOR.getName());
        while (cur.moveToNext()) {
            int idSensor = cur.getInt(idx);
            asensor[i] = idSensor;
            i++;
        }

        cur.close();

        return asensor;
    }


    public static Sensor getSensor(int id) {
        Sensor sensor = null;
        if (id > 0) {
            String sql;
            sql = "SELECT * FROM " + Tables.SENSORS.getName();
            sql += " WHERE " + SensorFields.ID.getName() + "=" + id;
            Cursor cur = getReadableDb().rawQuery(sql, null);
            cur.moveToFirst();

            int idx;

            idx = cur.getColumnIndex(SensorFields.NUMBER.getName());
            int number = cur.getInt(idx);

            idx = cur.getColumnIndex(SensorFields.TYPE.getName());
            int type = cur.getInt(idx);

            idx = cur.getColumnIndex(SensorFields.NAME.getName());
            String name = cur.getString(idx);

            cur.close();

            sensor = new Sensor();

            sensor.setId(id);
            sensor.setNumber(number);
            sensor.setTipo(type);
            sensor.setName(name);
            int[] areaIds = getAreaIdsBySensor(sensor.getId());
            sensor.setAreaIds(areaIds);
        }
        return sensor;
    }

    public static int getSensorsCount() {
        return getCount(Tables.SENSORS.getName());
    }

    public static int getSensorCountByArea(int idArea) {
        String query = "SELECT COUNT(*) FROM " + Tables.AREA_SENSOR.getName() + " WHERE " + AreaSensorFields.IDAREA.getName() + "=" + idArea;
        return (int) DatabaseUtils.longForQuery(getReadableDb(), query, null);
    }


// ================ START AREA-SENSOR ===================

    private String getAreaSensorTableCreateString() {

        String s;
        s = "CREATE TABLE " + Tables.AREA_SENSOR.getName() + " (";

        AreaSensorFields field;
        AreaSensorFields[] fields = AreaSensorFields.values();
        for (int i = 0; i < fields.length; i++) {
            field = fields[i];
            s += field.getName() + " " + field.getType();
            if (i < fields.length - 1) {
                s += ", ";
            }
        }
        s += ");";

        return s;
    }


    public static int saveAreaSensor(int idArea, int idSensor) throws Exception {
        ContentValues values = new ContentValues();
        values.put(AreaSensorFields.IDAREA.getName(), idArea);
        values.put(AreaSensorFields.IDSENSOR.getName(), idSensor);
        return save(Tables.AREA_SENSOR.getName(), null, values, 0);
    }


    // ================ START BOARDS ===================

    private String getBoardsTableCreateString() {

        String s;
        s = "CREATE TABLE " + Tables.BOARDS.getName() + " (";

        BoardFields field;
        BoardFields[] fields = BoardFields.values();
        for (int i = 0; i < fields.length; i++) {
            field = fields[i];
            s += field.getName() + " " + field.getType();
            if (i < fields.length - 1) {
                s += ", ";
            }
        }
        s += ");";

        return s;
    }

    public static int saveBoard(Board board) {

        int id = 0;
        ContentValues values = new ContentValues();
        values.put(BoardFields.IDSITE.getName(), board.getIdSite());
        values.put(BoardFields.NUMBER.getName(), board.getNumber());
        values.put(BoardFields.NAME.getName(), board.getName());
        id = save(Tables.BOARDS.getName(), BoardFields.ID.getName(), values, board.getId());
        board.setId(id);

        return id;

    }


    public static void deleteBoard(int id) {
        String sql = "DELETE FROM " + Tables.BOARDS.getName();
        sql += " WHERE " + BoardFields.ID.getName() + "=" + id;
        getWritableDb().execSQL(sql);
    }



    public static Board getBoard(int id) {
        Board board = null;
        if (id > 0) {
            String sql;
            sql = "SELECT * FROM " + Tables.BOARDS.getName();
            sql += " WHERE " + BoardFields.ID.getName() + "=" + id;
            Cursor cur = getReadableDb().rawQuery(sql, null);
            cur.moveToFirst();

            int idx;

            idx = cur.getColumnIndex(BoardFields.IDSITE.getName());
            int idSite = cur.getInt(idx);

            idx = cur.getColumnIndex(BoardFields.NUMBER.getName());
            int number = cur.getInt(idx);

            idx = cur.getColumnIndex(BoardFields.NAME.getName());
            String name = cur.getString(idx);

            cur.close();

            board = new Board();

            board.setId(id);
            board.setIdSite(idSite);
            board.setNumber(number);
            board.setName(name);
        }
        return board;
    }


    public static Board[] getBoards(int idSite) {
        ArrayList<Board> boards = new ArrayList<Board>();
        String sql;
        sql = "SELECT " + BoardFields.ID.getName() + " FROM " + Tables.BOARDS.getName();
        sql += " WHERE " + BoardFields.IDSITE.getName() + "=" + idSite;
        Cursor cur = getReadableDb().rawQuery(sql, null);
        while (cur.moveToNext()) {
            int idx = cur.getColumnIndex(BoardFields.ID.getName());
            int id = cur.getInt(idx);
            Board board = getBoard(id);
            boards.add(board);
        }

        cur.close();

        Board[] aboard = boards.toArray(new Board[0]);
        return aboard;

    }

    public static int getBoardsCount() {
        return getCount(Tables.BOARDS.getName());
    }

    public static int getBoardsCountBySite(int idSite) {
        String query = "SELECT COUNT(*) FROM " + Tables.BOARDS.getName() + " WHERE " + BoardFields.IDSITE.getName() + "=" + idSite;
        return (int) DatabaseUtils.longForQuery(getReadableDb(), query, null);
    }


    // ================ START MENUS ===================


    private String getMenusTableCreateString() {

        String s;
        s = "CREATE TABLE " + Tables.MENUS.getName() + " (";

        MenuFields field;
        MenuFields[] fields = MenuFields.values();
        for (int i = 0; i < fields.length; i++) {
            field = fields[i];
            s += field.getName() + " " + field.getType();
            if (i < fields.length - 1) {
                s += ", ";
            }
        }
        s += ");";

        return s;
    }

    public static int saveMenu(Menu menu) {

        int id = 0;
        ContentValues values = new ContentValues();
        values.put(MenuFields.IDSITE.getName(), menu.getIdSite());
        values.put(MenuFields.NUMBER.getName(), menu.getNumber());
        values.put(MenuFields.NAME.getName(), menu.getName());
        values.put(MenuFields.ACTION.getName(), menu.getAction());
        values.put(MenuFields.PAGE.getName(), menu.getPage());
        id = save(Tables.MENUS.getName(), MenuFields.ID.getName(), values, menu.getId());
        menu.setId(id);

        return id;

    }



    public static void deleteMenu(int id) {
        String sql = "DELETE FROM " + Tables.MENUS.getName();
        sql += " WHERE " + MenuFields.ID.getName() + "=" + id;
        getWritableDb().execSQL(sql);
    }


    public static Menu getMenu(int id) {
        Menu menu = null;
        if (id > 0) {
            String sql;
            sql = "SELECT * FROM " + Tables.MENUS.getName();
            sql += " WHERE " + MenuFields.ID.getName() + "=" + id;
            Cursor cur = getReadableDb().rawQuery(sql, null);
            cur.moveToFirst();

            int idx;

            idx = cur.getColumnIndex(MenuFields.IDSITE.getName());
            int idSite = cur.getInt(idx);

            idx = cur.getColumnIndex(MenuFields.NUMBER.getName());
            int number = cur.getInt(idx);

            idx = cur.getColumnIndex(MenuFields.NAME.getName());
            String name = cur.getString(idx);

            idx = cur.getColumnIndex(MenuFields.ACTION.getName());
            int action = cur.getInt(idx);

            idx = cur.getColumnIndex(MenuFields.PAGE.getName());
            int page = cur.getInt(idx);

            cur.close();

            menu = new Menu();

            menu.setId(id);
            menu.setIdSite(idSite);
            menu.setNumber(number);
            menu.setName(name);
            menu.setAction(action);
            menu.setPage(page);
        }
        return menu;
    }

    public static Menu[] getMenus(int idSite) {
        ArrayList<Menu> menus = new ArrayList<Menu>();
        String sql;
        sql = "SELECT " + MenuFields.ID.getName() + " FROM " + Tables.MENUS.getName();
        sql += " WHERE " + MenuFields.IDSITE.getName() + "=" + idSite;
        Cursor cur = getReadableDb().rawQuery(sql, null);
        while (cur.moveToNext()) {
            int idx = cur.getColumnIndex(MenuFields.ID.getName());
            int id = cur.getInt(idx);
            Menu menu = getMenu(id);
            menus.add(menu);
        }

        cur.close();

        Menu[] aMenus = menus.toArray(new Menu[0]);
        return aMenus;

    }


    public static Menu[] getMenusBySiteAndPage(int idSite, int idPage) {
        ArrayList<Menu> menus = new ArrayList<Menu>();
        String sql;
        sql = "SELECT " + MenuFields.ID.getName() + " FROM " + Tables.MENUS.getName();
        sql += " WHERE " + MenuFields.IDSITE.getName() + "=" + idSite;
        sql += " AND " + MenuFields.PAGE.getName() + "=" + idPage;
        Cursor cur = getReadableDb().rawQuery(sql, null);
        while (cur.moveToNext()) {
            int idx = cur.getColumnIndex(MenuFields.ID.getName());
            int id = cur.getInt(idx);
            Menu menu = getMenu(id);
            menus.add(menu);
        }

        cur.close();

        Menu[] aMenus = menus.toArray(new Menu[0]);
        return aMenus;

    }


    public static int getMenusCount() {
        return getCount(Tables.MENUS.getName());
    }

    public static int getMenusCountBySite(int idSite) {
        String query = "SELECT COUNT(*) FROM " + Tables.MENUS.getName() + " WHERE " + MenuFields.IDSITE.getName() + "=" + idSite;
        return (int) DatabaseUtils.longForQuery(getReadableDb(), query, null);
    }

    public static int getMenusCountBySiteAndPage(int idSite, int idPage) {
        String query = "SELECT COUNT(*) FROM " + Tables.MENUS.getName();
        query += " WHERE " + MenuFields.IDSITE.getName() + "=" + idSite;
        query += " AND " + MenuFields.PAGE.getName() + "=" + idPage;
        return (int) DatabaseUtils.longForQuery(getReadableDb(), query, null);
    }


}
