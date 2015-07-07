package it.technocontrolsystem.hypercontrol.domain;

/**
 *Enum dei tipi di sensore esistenti
 */
public enum SensorType {
 DIGITAL("Digitale",1),
 BALANCED("Bilanciato",2),
 ANALOGIC("Analogico",3),
 QUADRISTATE("QuattroStati",4);
    String description;
    int code;

    SensorType(String description, int code) {
        this.description = description;
        this.code = code;
    }

    private int getCode() {
        return code;
    }

    private String getDescription() {
        return description;
    }

    public static int getCode(String type) {
        int code=0;
        for(SensorType element : SensorType.values()){
            String desc=element.getDescription();
            if(desc.equalsIgnoreCase(type)){
                code=element.getCode();
                break;
            }
        }
        return code;
    }

    public static SensorType get(int tipo) {
        SensorType type=null;
        for(SensorType element : SensorType.values()){
            int code=element.getCode();
            if(tipo==code){
                type=element;
                break;
            }
        }
        return type;
    }
}
