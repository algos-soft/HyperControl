package it.technocontrolsystem.hypercontrol.model;

import it.technocontrolsystem.hypercontrol.domain.Board;

/**
 */
public class BoardModel implements ModelIF {
    private Board board;
    private int status;

    public BoardModel(Board board) {
        this.board = board;
        this.status=-1; // valore iniziale: unknown
    }

    public Board getBoard() {
        return board;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int getNumber() {
        return getBoard().getNumber();
    }

    @Override
    public void clearStatus() {
        setStatus(-1);
    }
}
