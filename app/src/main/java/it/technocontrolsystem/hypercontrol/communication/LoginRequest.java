package it.technocontrolsystem.hypercontrol.communication;

import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.service.DemoActivity;

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
        String msg="<User>"+siteuser+"</User>";
        msg+="<Pwd>"+sitepsw+"</Pwd>";
        msg+="<MAC>"+ Lib.getDeviceID()+"</MAC>";
        msg+="<RegId>"+ DemoActivity.regid.replace("-","\\-")+"</RegId>";
       return msg;
    }

    @Override
    public String getCommandId() {
        return "Login";
    }

    @Override
    public int getTimeout() {
        return 0;
    }//era 10(reimpostare alla fine)

    @Override
    public Class getResponseClass() {
        return Response.class;
    }

}
