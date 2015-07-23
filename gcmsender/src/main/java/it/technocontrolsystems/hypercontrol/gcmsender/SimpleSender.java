package it.technocontrolsystems.hypercontrol.gcmsender;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by alex on 23-07-2015.
 */
public class SimpleSender {

    public static final String API_KEY = "AIzaSyCTc1hvMyJUd_F8jBOJzmrbxz9mV9KveWU";
    public static final String DEVICE_ID = "fkfbaUAxrWE:APA91bFxP74Bu2l8eyeeZAFofjDe_goJsyojrG9q6-v410OEF023hSxECpKl0tIm72lzkIrT_ECs0I7EL4RlwCXOjYGS24lmXUDusTq9vGN6tC8MPY0wW3DYMq283FI0agCDTV2Digcc";


    public static void main(String[] args) {
        try {

            // prepara i dati
            JSONObject jData = new JSONObject();
            jData.put("messagetype", "alarm");
            jData.put("timestamp", "0000000000");
            jData.put("sitenum", "2");
            jData.put("plantnum", "4");
            jData.put("areanum", "3");
            jData.put("sensornum", "99");
            jData.put("details", "qui i dettagli dell'evento");

            // Prepare JSON containing the GCM message content. What to send and where to send.
            JSONObject jGcmData = new JSONObject();
            jGcmData.put("to", DEVICE_ID);
            jGcmData.put("data", jData);

            // Create connection to send GCM Message request.
            URL url = new URL("https://android.googleapis.com/gcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Send GCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jGcmData.toString().getBytes());

            // Read GCM response.
            InputStream inputStream = conn.getInputStream();
            String resp = IOUtils.toString(inputStream);
            System.out.println(resp);

        } catch (IOException e) {
            System.out.println("Unable to send GCM message.");
            e.printStackTrace();
        }
    }


}
