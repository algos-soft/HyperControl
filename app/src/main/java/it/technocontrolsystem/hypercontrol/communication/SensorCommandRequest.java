package it.technocontrolsystem.hypercontrol.communication;

/**
 * Richiesta di comando per un sensore: function=4 per abilitare/disabilitare un sensore, function=5 per attivare/disattivare il test
 */
public class SensorCommandRequest extends CommandRequest {
    private boolean stato;
    private int function;

    public SensorCommandRequest(int itemId, int function, boolean stato) {
        super(itemId, false);
        this.stato = stato;
        this.function=function;
    }

    @Override
    public String getMessage() {
        String sStato="";
        if(stato==true){
            sStato="1";
        }else{
            sStato="0";
        }

        String s="<Num>"+function+"</Num><Stato>"+sStato+"</Stato><Sensore><Numero>"+getItemId()+"</Numero></Sensore>";
        return s;
    }
}
