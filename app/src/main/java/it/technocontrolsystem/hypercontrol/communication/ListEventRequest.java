package it.technocontrolsystem.hypercontrol.communication;

/**
 *
 */
public class ListEventRequest extends Request {

    int lastId;
    int numero;


    /**
     * @param lastId id dell'ultimo evento ricevuto
     * @param numero numero di eventi richiesti
     */
    public ListEventRequest(int lastId, int numero) {
        this.lastId=lastId;
        this.numero=numero;
    }

    @Override
    public String getMessage() {
        String str="<Max>"+numero+"</Max>";
        str+="<UltimoId>"+lastId+"</UltimoId>";
        str+="<Direzione>0</Direzione>";
        return str;
    }

    @Override
    public String getCommandId() {
        return "Events";
    }


    @Override
    public Class getResponseClass() {
        return ListEventResponse.class;
    }

    @Override
    public int getTimeout() {
        return 20;
    }
}
