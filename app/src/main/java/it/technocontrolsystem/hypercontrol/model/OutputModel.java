package it.technocontrolsystem.hypercontrol.model;

import it.technocontrolsystem.hypercontrol.domain.Output;

/**
 *
 */
public class OutputModel implements ModelIF {
    private Output output;
    private int status;

    public OutputModel(Output output) {
        this.output = output;
        this.status=-1; // valore iniziale: unknown
    }

    public Output getOutput() {
        return output;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int getNumber() {
        return getOutput().getNumber();
    }

    @Override
    public void clearStatus() {
        setStatus(-1);
    }
}