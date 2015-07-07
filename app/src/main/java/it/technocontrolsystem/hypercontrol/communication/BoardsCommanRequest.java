package it.technocontrolsystem.hypercontrol.communication;

/**
 * Created by Federico on 04/05/2015.
 */
public class BoardsCommanRequest extends CommandRequest {
    private boolean test;
    public BoardsCommanRequest(int itemId, boolean enable, boolean test) {
        super(itemId, enable);
        this.test = test;
    }

    @Override
    public String getMessage() {
        String sStato="";
        if(isEnable()){
            sStato="1";
        }else{
            sStato="0";
        }

        String s="<Num>3</Num><Stato>"+sStato+"</Stato><Scheda><Numero>"+getItemId()+"</Numero></Scheda>";
        return s;
    }
}
