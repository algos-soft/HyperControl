package it.technocontrolsystem.hypercontrol.communication;

import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.Prefs;

/**
 *
 */
public class LoginRequest extends Request {
    private String siteuser;//federico
    private String sitepsw;//federico

    public LoginRequest(String siteuser,String sitepsw) {//federico
        this.siteuser = siteuser;
        this.sitepsw = sitepsw;
    }

    public String getMessage() {

        String token = Prefs.getPrefs().getString(Prefs.GCM_REGISTRATION_TOKEN, null);
        if (token!=null) {
            token = token.replace("-", "\\-");  // perché questo?
        }else{
            token="";
        }

        String msg="<User>"+siteuser+"</User>";
        msg+="<Pwd>"+sitepsw+"</Pwd>";
        msg+="<MAC>"+ Lib.getDeviceID()+"</MAC>";
        msg+="<RegId>"+ token +"</RegId>";


       return msg;
    }

    @Override
    public String getCommandId() {
        return "Login";
    }

    @Override
    public int getTimeout() {
        return 10;
    }

    @Override
    public Class getResponseClass() {
        return Response.class;
    }

}
