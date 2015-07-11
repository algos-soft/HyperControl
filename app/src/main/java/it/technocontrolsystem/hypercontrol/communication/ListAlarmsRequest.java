package it.technocontrolsystem.hypercontrol.communication;

/**
 * Request ListAlarm
 */
public class ListAlarmsRequest extends Request {


    @Override
    public Class getResponseClass() {
        return ListAlarmsResponse.class;
    }

    @Override
    public int getTimeout() {
        return 600; // possono essere tanti
    }

    @Override
    public String getMessage() {
        return "";
    }

    @Override
    public String getCommandId() {
        return "Alarms";
    }
}
