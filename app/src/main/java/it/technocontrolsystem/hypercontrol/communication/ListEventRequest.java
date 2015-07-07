package it.technocontrolsystem.hypercontrol.communication;

/**
 *
 */
public class ListEventRequest extends Request {
    int numero;


    public ListEventRequest(int numero) {
        this.numero=numero;
    }

    @Override
    public String getMessage() {
        return "<Max>"+numero+"</Max><UltimoId>-1</UltimoId><Direzione>0</Direzione>";
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
        return 10;
    }
}
