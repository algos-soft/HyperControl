package it.technocontrolsystem.hypercontrol.communication;

/**
 * A request sent to the Unit
 */
public abstract class Request {

    public static int requestCount=0;
    private int requestNumber;

    protected Request() {
        requestCount++;
        this.requestNumber=requestCount;
    }

    /**
     * ritorna la classe delle risposte relative a questo tipo di request
     */
    public abstract Class getResponseClass();

    /**
     * ritorna il timeout in secondi per questo tipo di request
     * @return  timeout
     */
    public abstract int getTimeout();

    public abstract String getMessage();

    public abstract String getCommandId();


    public int getRequestNumber() {
        return requestNumber;
    }

    public String getXML(){
        String xml="@<Request><Numero>"+getRequestNumber()+"</Numero>";
        xml+="<Comando><Id>"+getCommandId()+"</Id>";
        xml+=getMessage();
        xml+="</Comando></Request>";
        xml+=""+new Character((char)0x1A);
        return xml;
    }
}
