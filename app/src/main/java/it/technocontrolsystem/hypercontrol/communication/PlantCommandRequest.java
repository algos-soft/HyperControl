package it.technocontrolsystem.hypercontrol.communication;

/**
 * Created by Federico on 29/04/2015.
 */
public class PlantCommandRequest extends CommandRequest {
    private boolean partial;

    public PlantCommandRequest(int numPlant, boolean checked,boolean partial) {
        super(numPlant,checked);
        this.partial=partial;
    }

    @Override
    public String getMessage() {
        String sStato="";
        String sArea="";

        if(!isEnable()){
            sStato="0";
            sArea="0";
        }else{
            if(partial){
                sStato="1";
                sArea="0";//sostituire con valore per parziale o numero aree o togliere tag area
            }else{
                sStato="2";
                sArea="0";
            }
        }

        String s="<Num>1</Num><Stato>"+sStato+"</Stato><Impianto><Numero>"+getItemId()+"</Numero><Area><Numero>"+sArea+"</Numero></Area></Impianto>";
        return s;
    }
}
