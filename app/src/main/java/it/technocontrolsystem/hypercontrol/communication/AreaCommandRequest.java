package it.technocontrolsystem.hypercontrol.communication;

/**
 * Created by Federico on 29/04/2015.
 */
public class AreaCommandRequest extends CommandRequest {
    private boolean partial;
    private int numPlant;

    public AreaCommandRequest(int numPlant, int numArea, boolean checked,boolean partial) {
        super(numArea,checked);
        this.partial=partial;
        this.numPlant=numPlant;
    }

    @Override
    public String getMessage() {
        String sStato="";

        if(!isEnable()){
            sStato="0";
        }else{
            if(partial){
                sStato="1";
            }else{
                sStato="2";
            }
        }

        String s="<Num>1</Num><Stato>"+sStato+"</Stato><Impianto><Numero>"+numPlant+"</Numero><Area><Numero>"+getItemId()+"</Numero></Area></Impianto>";
        return s;
    }
}
