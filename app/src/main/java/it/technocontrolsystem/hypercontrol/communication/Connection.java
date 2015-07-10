package it.technocontrolsystem.hypercontrol.communication;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.domain.Site;

/**
 * Connection with the unit
 */
public class Connection {

    private Site site;

    private Socket socket;
    private DataOutputStream dataOutputStream = null;
    private DataInputStream dataInputStream = null;

    //coda delle richieste da tresmettere
    private ArrayList<Request> requestQueue;

    //serbatoio delle richieste già inviate e in attesa di risposta
    private HashMap<Integer, Request> sentQueue;

    //serbatoio delle risposte ricevute
    private HashMap<Integer, Response> responseQueue;

    //listener da notificare quando si riceve un messaggio Live
    private LiveListener liveListener;

    //flag che viene acceso quando riceviamo il primo carattere
    //di una risposta ed iniziamo a processarla, e viene spento
    // dopo che la risposta è stata processata
    private static boolean processingResponse = false;

    private static final String TAG="CONN";


    public Connection(Site site) throws Exception {
        Log.d(TAG,"creating new connection...");

        this.site = site;

        requestQueue = new ArrayList();
        responseQueue = new HashMap();
        sentQueue = new HashMap();

        if (Lib.isNetworkAvailable()){
            createSocket();
            createRequestThread();
            createReceiveThread();
            doLogin();
            Log.d(TAG,"connection created successfully");
        }else{
            Log.d(TAG,"create connection failed");
            throw new NetworkUnavailableException();
        }

    }

    /**
     * Mock object constructor
     */
    public Connection() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Effettua il login
     * Se fallisce lancia un Exception
     */
    private void doLogin() throws Exception {
        Request request = new LoginRequest(getSite().getUsername(), getSite().getPassword());
        Response resp = sendRequest(request);
        if (resp != null) {
            if (!resp.isSuccess()) {
                throw new Exception(resp.getText());
            }
        } else {//comunication failed
            throw new Exception("Richiesta di login fallita");
        }
    }


    /**
     * accoda la richiesta e attende la risposta corrispondente
     * quando riceve la risposta corrispondente la ritorna
     *
     * @param req richiesta da accodare
     * @return risposta corrispondente
     */
    public Response sendRequest(Request req) {
        Response resp = null;
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
                    stop = true;
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


        if(socketCreator.getSocket()!=null){    // riuscito
            Socket socket = socketCreator.getSocket();
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
            dataInputStream = new DataInputStream(socket.getInputStream());



        }else{  // fallito
            Exception e = socketCreator.getException();
            throw e;
        }


    }

    /**
     * crea il thread per inviare le richieste alla centrale
     */
    private void createRequestThread() {
        // close socket in separate thread (not in the UI thread)
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    if (requestQueue.size() > 0) {
                        Request request = requestQueue.get(0);
                        requestQueue.remove(0);
                        String text = request.getXML();
                        try {
                            dataOutputStream.writeUTF(text);
                            sentQueue.put(request.getRequestNumber(), request);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    /**
     * crea il thread per ricevere le risposte dalla centrale
     */
    private void createReceiveThread() {
        // close socket in separate thread (not in the UI thread)
        new Thread(new Runnable() {

            private final String EOF = "" + new Character((char) 0x1A);
            private final String OPEN_TAG = "<Numero>";
            private final String CLOSE_TAG = "</Numero>";


            @Override
            public void run() {

                int responseNumber = 0;
                int timeoutSec = 0;
                long startTime = 0;
                String s = "";
                String responseText = "";
                byte[] streamBuffer;


                while (true) {
                    try {
                        timeoutSec = 0;
                        //controllo del timeout
                        //se è stiamo processando una risposta ed è trascorso il timeout
                        //smette di ascoltare la risposta
                        if (timeoutSec > 0) {
                            if (processingResponse) {
                                long currTime = System.currentTimeMillis();
                                int elapsed = (int) (currTime - startTime) / 1000;
                                if (elapsed > timeoutSec) {
                                    processingResponse = false;
                                }
                            }
                        }

                        //lettura dell'inputStream
                        if (dataInputStream.available() > 0) {

                            int i = dataInputStream.read();

                            if (i != -1) {
                                // if (true) {
                                s = "" + (char) i;
                                //s = dataInputStream.readUTF();
                                //s="";
                                //while(dataInputStream.available()>0)
                                //s += "" + (char)dataInputStream.read();
                                if (processingResponse) {
                                    if (!s.equals(EOF)) {

                                        //accumula il carattere ricevuto
                                        responseText += s;

                                        //se il responseNumber=0 lo cerca nella
                                        //stringa ricevuta finora.
                                        //se lo trova,lo registra e smette di cercarlo
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
                                    } else {//ricevuto EOF
                                        processResponse(responseNumber, responseText);
                                        processingResponse = false;
                                    }


                                } else {    //non stiamo processando una risposta
                                    //se riceviamo un carattere di inizio risposta
                                    //iniziamo a processarla
                                    if (s.equals("@")) {
                                        responseText = "";
                                        responseNumber = 0;
                                        startTime = System.currentTimeMillis();
                                        processingResponse = true;
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }


            /**
             * Processa una response
             */
            private void processResponse(int responseNumber, String responseText) {
                Log.d("HC", "process response " + responseNumber);

                responseText = "<?xml version='1.0' encoding='utf-8'?>\n" + responseText;
                Request req = sentQueue.get(responseNumber);
                if (req != null) { // la response ha una corrispondente request

                    // build response of right type by reflection
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

                } else {  // response senza request
                    Response resp = new Response(responseText);
                    String command = resp.getComando();
                    if (command.equalsIgnoreCase("live")) {
                        final LiveMessage message = new LiveMessage(responseText);
                        if (liveListener != null) {
                            liveListener.liveReceived(message);
                        }
                    }

                }

            }

        }).start();

    }



}
