package it.technocontrolsystem.hypercontrol.database;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.domain.Plant;
import it.technocontrolsystem.hypercontrol.domain.Site;

/**
 * Created by Federico on 30/03/2015.
 */
public class LibDev {

    /**
     * Esporta il DB su un file pubblico
     */
    public static int copyDb() {
        int result=0;
        try {
            Context ctx = HyperControlApp.getContext();
            File sd = ctx.getExternalFilesDir(null);

            if (sd.canWrite()) {

                File dbpath = ctx.getDatabasePath(DB.DBNAME);
                String backupDBPath = DB.DBNAME+".db3";
                File backupDB = new File(sd, backupDBPath);

                if (dbpath.exists()) {
                    FileChannel src = new FileInputStream(dbpath).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();

                    // Refresh the data so it can seen when the device is plugged in a
                    // computer. You may have to unplug and replug the device to see the
                    // latest changes. This is not necessary if the user should not modify
                    // the files.
                    MediaScannerConnection.scanFile(ctx,
                            new String[]{backupDB.toString()},
                            null,
                            null);

                    result=1;
                }
            }
        } catch (Exception e) {

            System.err.println("Errore di copia database");
        }

        return result;

    }

    // Cancella tutti gli impianti e tutti i record di un site collegati a cascata
    public static void deleteAllRecords(Site site) {
        Plant[] plants = site.getPlants();
        for (Plant plant : plants) {
            DB.deletePlant(plant.getId());
        }
    }

    /* Checks if external storage is available for read and write */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


}


