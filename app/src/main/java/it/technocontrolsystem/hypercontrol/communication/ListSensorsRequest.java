package it.technocontrolsystem.hypercontrol.communication;

/**
 *
 */
public class ListSensorsRequest extends Request {
    public String getMessage() {

        return "";

    }

    @Override
    public String getCommandId() {
        return "Inputs";
    }

    @Override
    public int getTimeout() {
        // 20 minuti, molto alto perché i sensori vengono inviati
        // tutti insieme con una unica richiesta, indicativamente
        // trasferisce 100 sensori al minuto
        return 1200;
    }

    @Override
    public Class getResponseClass() {
        return ListSensorsResponse.class;
    }

}
