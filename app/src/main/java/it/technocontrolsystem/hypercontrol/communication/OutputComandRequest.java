package it.technocontrolsystem.hypercontrol.communication;

/**
 * Created by Federico on 08/10/2015.
 */
public class OutputComandRequest extends CommandRequest {
    private boolean stato;
    private int function;

    public OutputComandRequest(int itemId, int function, boolean stato) {
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
