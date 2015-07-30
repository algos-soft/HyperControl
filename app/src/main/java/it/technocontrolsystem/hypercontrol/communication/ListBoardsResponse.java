package it.technocontrolsystem.hypercontrol.communication;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import it.technocontrolsystem.hypercontrol.domain.Board;

/**
 * Risposta ad una ListBoardsRequest
 */
public class ListBoardsResponse extends Response {
    private ArrayList<Board> boards;
    public ListBoardsResponse(String string) {
        super(string);
    }

    @Override
    protected void readComando() throws XmlPullParserException, IOException {
        super.readComando();
        // se non esiste lo crea ora
        // nel costruttore è troppo tardi perché la superclasse
        // invoca questo metodo dal costruttore
        if (boards == null) {
            boards = new ArrayList<Board>();
        }
        if (gotoFirstTag("Schede")){
            readSchede();
        }
    }

    private void readSchede()throws XmlPullParserException, IOException {
        boolean stop = false;
        while (!stop) {
            boolean found = gotoNextStart("Scheda");
            if (found) {
                Board board = new Board();
                boards.add(board);
                readBoard(board);
            } else {
                stop = true;
            }
        }
    }

    private void readBoard(Board board)throws XmlPullParserException, IOException {
        XmlPullParser parser = getParser();
        boolean stop = false;
        while (!stop) {
            parser.next();
            String name = parser.getName();
            if (name!=null){
                if (name.equals("Numero")) {
                    board.setNumber(Integer.parseInt(parser.nextText()));
                    // setNumber(Integer.parseInt(parser.nextText()));
                } else if (name.equals("Nome")) {
                    board.setName(parser.nextText());
                    // setDate(parser.nextText());
                }else{
                    stop = true;
                }
            }else{
                stop = true;
            }

        }
    }

    public Board[] getBoards() {
        return boards.toArray(new Board [0]);
    }

}



