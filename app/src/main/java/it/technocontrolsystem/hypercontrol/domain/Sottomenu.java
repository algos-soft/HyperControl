package it.technocontrolsystem.hypercontrol.domain;

/**
 * Domain Class for a single Sottomenu
 */
public class Sottomenu {
    private int numero;
    private String nome;
    private int azione;

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getAzione() {
        return azione;
    }

    public void setAzione(int azione) {
        this.azione = azione;
    }
}
