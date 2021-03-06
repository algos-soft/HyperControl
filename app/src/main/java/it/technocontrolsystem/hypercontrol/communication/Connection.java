package it.technocontrolsystem.hypercontrol.communication;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.domain.Site;

/**
 * TCP/IP Connection to a site
 * Created by alex on 25-07-2015.
 */
public class Connection {

    private Site site;

    private Socket socket;
    private Thread requestThread;
    private Thread receiveThread;
    private DataOutputStream dataOutputStream = null;
    private DataInputStream dataInputStream = null;
    private boolean open;
    private String hpUUID;  // id del sito al quale si è connessi

    //coda delle richieste da tresmettere
    private ArrayList<Request> requestQueue;

    //serbatoio delle richieste già inviate e in attesa di risposta
    private HashMap<Integer, Request> sentQueue;

    //serbatoio delle risposte ricevute
    private HashMap<Integer, Response> responseQueue;

    //listener da notificare quando si riceve un messaggio Live
    private LiveListener liveListener;

    //listeners interessati al cambio di stato della connessione
    private ArrayList<OnConnectionStatusChangedListener> connectionStatusChangedListeners;


    //flag che viene acceso quando riceviamo il primo carattere
    //di una risposta ed iniziamo a processarla, e viene spento
    // dopo che la risposta è stata processata
    private static boolean processingResponse = false;

    private static final String TAG = "CONN";


    public Connection(Site site) {
        Log.d(TAG, "creating new connection...");

        this.site = site;

        requestQueue = new ArrayList();
        responseQueue = new HashMap();
        sentQueue = new HashMap();

        connectionStatusChangedListeners = new ArrayList<>();

    }


    /**
     * Apre la connessione
     * Crea il socket e i background threads (Request e Receive)
     * effettua il login
     */
    public void open() throws Exception {
        if (Lib.isNetworkAvailable()) {
            boolean oldStatus = open;
            createSocket();
            createRequestThread();
            createReceiveThread();
            open = true;
            doLogin();
            fireStatusChanged(oldStatus, true);
            Log.d(TAG, "connection opened successfully");

        } else {
            Log.d(TAG, "open connection failed");
            throw new NetworkUnavailableException();
        }

    }

    /**
     * Chiude la connessione.
     * Chiude il socket e ferma tutti i background threads.
     */
    public void close() {
        boolean oldStatus = open;
        Log.d(TAG, "connection close requested");

        if (requestThread != null) {
            requestThread.interrupt(); // nel Thread il metodo Thread.sleep() genera una InterruptedException
        }

        if (receiveThread != null) {
            receiveThread.interrupt(); // nel Thread il metodo Thread.sleep() genera una InterruptedException
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        open = false;

        fireStatusChanged(oldStatus, false);
        Log.d(TAG, "connection has been closed");

    }


    /**
     * Effettua il login
     * Se fallisce lancia un Exception
     */
    private void doLogin() throws Exception {
        hpUUID = "";  // resetta il siteid
        Request request = new LoginRequest(getSite().getUsername(), getSite().getPassword());
        Response resp = sendRequest(request);
        if (resp != null) {
            if (resp.isSuccess()) {
                // memorizza l'id del sito nella connessione per uso futuro
                LoginResponse lresp = (LoginResponse) resp;
                hpUUID = lresp.getHpUUID();
            } else {
                throw new Exception(resp.getText());
            }
        } else {//comunication failed
            throw new Exception("Richiesta di login fallita");
        }
    }


    /**
     * Accoda una richiesta e attende la risposta corrispondente.
     * Quando riceve la risposta corrispondente la ritorna.
     * Il flusso richiesta-risposta è strettamente sincrono.
     * Prima di inviare una nuova richiesta si attende la riposta precedente.
     * La richiesta viene accodata solo se la connessione è aperta.
     *
     * @param req richiesta da accodare
     * @return risposta corrispondente, null se in timeout o se
     * la connessione è chiusa
     */
    public Response sendRequest(Request req) {

        Response resp = null;

        if (isOpen()) {

            requestQueue.add(req);

            // attende che nella coda delle risposte appaia la risposta corrispondente
            boolean stop = false;
            int num = req.getRequestNumber();
            long start = System.currentTimeMillis();
            while (!stop) {

                resp = responseQueue.get(num);
                if (resp != null) {
                    responseQueue.remove(num);
                    stop = true;
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // check timeout if present
                if (req.getTimeout() > 0) {
                    long secs = (System.currentTimeMillis() - start) / 1000;
                    if (secs > req.getTimeout()) {
                        Log.e(TAG, "Request timeout " + req.getDebugString());
                        stop = true;
                    }
                }

            }
        }

        return resp;
    }


    /**
     * Cerca una risposta con un dato id nella coda delle risposte
     *
     * @param id della risposta
     * @return la risposta trovata o null se non trovata
     */
    public Response findResponse(int id) {
        return null;
    }


    private Site getSite() {
        return site;
    }


    public String getHpUUID() {
        return hpUUID;
    }

    public void setLiveListener(LiveListener listener) {
        liveListener = listener;
    }


    public interface LiveListener {
        public void liveReceived(LiveMessage message);
    }

    public static boolean isProcessingResponse() {
        return processingResponse;
    }


    /**
     * @return true se la connessione è aperta
     * (il socket è aperto e i thread di input e output sono attivi)
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * crea socket in un thread separato e lo registra
     */
    public void createSocket() throws Exception {


        // crea il socket
        SocketCreator socketCreator = new SocketCreator(getSite().getAddress(), getSite().getPort());
        new Thread(socketCreator).start();

        // attendi il termine della creazione del socket
        while (!socketCreator.isDone()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        if (socketCreator.getSocket() != null) {    // riuscito
            socket = socketCreator.getSocket();
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
        } else {  // fallito
            Exception e = socketCreator.getException();
            throw e;
        }


    }

    /**
     * crea il thread per inviare le richieste alla centrale
     */
    private void createRequestThread() {

        // close socket in separate thread (not in the UI thread)
        requestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    if (requestQueue.size() > 0) {
                        Request request = requestQueue.get(0);
                        requestQueue.remove(0);
                        String text = request.getXML();
                        try {
                            Log.d(TAG, "sending request # " + request.getRequestNumber());
                            dataOutputStream.writeUTF(text);
                            sentQueue.put(request.getRequestNumber(), request);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        try {
                            dataOutputStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        break;
                    }
                }
            }
        });

        requestThread.setName("requests processing thread");
        requestThread.start();

    }


    /**
     * crea il thread per ricevere le risposte dalla centrale
     */
    private void createReceiveThread() {

        // close socket in separate thread (not in the UI thread)
        receiveThread = new Thread(new Runnable() {

            private final String EOF = "" + new Character((char) 0x1A);
            private final String OPEN_TAG = "<Numero>";
            private final String CLOSE_TAG = "</Numero>";


            @Override
            public void run() {

                int responseNumber = 0;
                int timeoutSec = 0;
                long startTime = 0;
                String s = "";
                StringBuilder responseText = new StringBuilder();

                // ascolta l'inputStream e processa le risposte
                while (true) {

                    try {

                        // Thread.sleep ci vuole se no quando chiamo
                        // interrupt() non viene generata l'eccezione
                        // e il ciclo non termina
                        Thread.sleep(5);

                        try {

                            //controllo del timeout
                            //se stiamo processando una risposta ed è trascorso il timeout
                            //smette di ascoltare la risposta
                            if (timeoutSec > 0) {
                                if (processingResponse) {
                                    long currTime = System.currentTimeMillis();
                                    int elapsed = (int) (currTime - startTime) / 1000;
                                    if (elapsed > timeoutSec) {
                                        processingResponse = false;

                                        // simo in timeout.
                                        // la centrale potrebbe essere caduta?
                                        // per sicurezza chiudiamo la connessione.
                                        close();

                                    }
                                }
                            }

                            if (dataInputStream.available() > 0) {

                                byte[] bytes = new byte[4096];
                                int read = dataInputStream.read(bytes);

                                if (read > -1) {
                                    for (int i = 0; i < read; i++) {
                                        byte b = bytes[i];
                                        s = "" + (char) b;
                                        if (processingResponse) {

                                            if (!s.equals(EOF)) {

                                                //accumula il carattere ricevuto
                                                responseText.append(s);

                                                // Se il responseNumber è = 0 lo cerca nella
                                                // stringa ricevuta finora.
                                                // Appena lo trova,lo registra e smette di cercarlo
                                                // Appena trova il response number lo associa alla request in coda
                                                // e recupera il timeout
                                                if (responseNumber == 0) {
                                                    int startCloseTag = responseText.indexOf(CLOSE_TAG);
                                                    if (startCloseTag != -1) {
                                                        int startOpenTag = responseText.indexOf(OPEN_TAG);
                                                        int endOpenTag = startOpenTag + OPEN_TAG.length();
                                                        String sNum = responseText.substring(endOpenTag, startCloseTag);
                                                        responseNumber = Integer.parseInt(sNum);
                                                        Request req = sentQueue.get(responseNumber);
                                                        if (req != null) {
                                                            timeoutSec = req.getTimeout();
                                                        }
                                                    }
                                                }
                                            } else {//ricevuto EOF, risposta completa
                                                Log.d(TAG, "response received # " + responseNumber);
                                                String s1 = responseText.toString();

                                                byte[] aBytes=s1.getBytes();
                                                //String sUTF = new String(aBytes,"UTF_8");
                                                String sUTF = new String(aBytes, Charset.forName("UTF-8"));


                                                // i caratteri ampersand mandano in crash l'XMLPullParser!
                                                sUTF = sUTF.replaceAll("&", " and ");
                                                processResponse(responseNumber, sUTF);
                                                processingResponse = false;
                                            }


                                        } else {    //non stiamo processando una risposta
                                            //se riceviamo un carattere di inizio risposta
                                            //iniziamo a processarla
                                            if (s.equals("@")) {
                                                responseText = new StringBuilder();
                                                responseNumber = 0;
                                                startTime = System.currentTimeMillis();
                                                processingResponse = true;
                                            }

                                        }

                                    }
                                }

                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                        try {
                            dataInputStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        break;
                    }

                }

            }


            /**
             * Processa una response
             */

            private void processResponse(int responseNumber, String responseText) {

                responseText = "<?xml version='1.0' encoding='utf-8'?>\n" + responseText;
                Request req = sentQueue.get(responseNumber);
                if (req != null) { // la response ha una corrispondente request

                    // build response of the right class by reflection
                    Class responseClass = req.getResponseClass();
                    Response resp = null;
                    Constructor<Response> constructor = null;
                    try {
                        constructor = responseClass.getConstructor(String.class);
                        resp = constructor.newInstance(responseText);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    responseQueue.put(responseNumber, resp);
                    sentQueue.remove(responseNumber);

                } else {  // response senza corrispondente request

                    Response resp = new Response(responseText);
                    String command = resp.getComando();
                    if (command.equalsIgnoreCase("live")) {
                        final LiveMessage message = new LiveMessage(responseText);
                        Log.d(TAG, "live message received, cmd: " + command);
                        if (liveListener != null) {
                            liveListener.liveReceived(message);
                        }
                    }

                }

            }


        });

        receiveThread.setName("responses processing thread");
        receiveThread.start();
    }


    public void addOnConnectionStatusChangedListener(OnConnectionStatusChangedListener l) {
        connectionStatusChangedListeners.add(l);
    }

    private void fireStatusChanged(boolean oldStatus, boolean newStatus) {
        for (OnConnectionStatusChangedListener l : connectionStatusChangedListeners) {
            if ((oldStatus == false) && (newStatus == true)) {
                l.connectionOpened(this);
            }
            if ((oldStatus == true) && (newStatus == false)) {
                l.connectionClosed(this);
            }
        }
    }

    public interface OnConnectionStatusChangedListener {
        public void connectionOpened(Connection conn);

        public void connectionClosed(Connection conn);
    }


}
