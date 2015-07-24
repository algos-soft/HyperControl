/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.technocontrolsystem.hypercontrol.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.Prefs;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.activity.StartSiteActivity;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.domain.Plant;
import it.technocontrolsystem.hypercontrol.domain.Site;

public class HCGcmListenerService extends GcmListenerService {

    private static final String TAG = "HCGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {

        if(Prefs.isRiceviNotifiche()){

            String messagetype = data.getString("messagetype");
            String timestamp = data.getString("timestamp");
            String sitenum = data.getString("sitenum");
            String plantnum = data.getString("plantnum");
            String areanum = data.getString("areanum");
            String sensornum = data.getString("sensornum");
            String details = data.getString("details");
            Log.d(TAG, "Message received from: "+from+", type: "+messagetype);


            /**
             * Production applications would usually process the message here.
             * Eg: - Syncing with server.
             *     - Store message in local database.
             *     - Update UI.
             */

            /**
             * In some cases it may be useful to show a notification indicating to the user
             * that a message was received.
             */
            //String message=messagetype+" site:"+sitenum;
            sendNotification(data);


        }
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     */
    private void sendNotification(Bundle data) {
        String messagetype = data.getString("messagetype");

        switch (messagetype){

            // crea una notifica che quando cliccata apre il site in allarme
            case "alarm":
                int siteid=1;   // provvisorio!!!
                Intent intent = new Intent();
                intent.setClass(this, StartSiteActivity.class);
                intent.putExtra("siteid", siteid);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                String title="Allarme";
                Site site = DB.getSite(siteid);
                if(site!=null){
                    title+=" "+site.getName();
                }
                String msg="";
                try{
                    int plantnum=Integer.parseInt(data.getString("plantnum"));
                    Plant plant=DB.getPlantBySiteAndNumber(siteid, plantnum);
                    msg+=plant.getName();

                    int areanum=Integer.parseInt(data.getString("areanum"));
                    Area area=DB.getAreaByIdPlantAndAreaNumber(plant.getId(), areanum);
                    msg+=" "+area.getName();

                }catch (Exception e){}

                Uri defaultSoundUri=Lib.resourceToUri(R.raw.siren);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

                Notification mNotification = notificationBuilder.build();
                mNotification.flags |= Notification.FLAG_INSISTENT;

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(0, mNotification);

                // sveglia il device
                PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK |  PowerManager.ACQUIRE_CAUSES_WAKEUP), getClass().getSimpleName());
                wakeLock.acquire();


                break;
        }


    }
}
