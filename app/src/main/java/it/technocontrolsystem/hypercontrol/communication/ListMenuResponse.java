package it.technocontrolsystem.hypercontrol.communication;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import it.technocontrolsystem.hypercontrol.domain.Menu;

/**
 * Risposta ad una ListMenuRequest
 */
public class ListMenuResponse extends Response{

  //  private ArrayList<Sottomenu> sottomenus;
    private ArrayList<Menu> menus;
    public ListMenuResponse(String string) {
        super(string);
    }

    @Override
    protected void readComando() throws XmlPullParserException, IOException {
        super.readComando();

        // se non esiste lo crea ora
        // nel costruttore è troppo tardi perché la superclasse
        // invoca questo metodo dal costruttore
//        if (sottomenus == null) {
//            sottomenus = new ArrayList<Sottomenu>();
//        }
        if (menus == null) {
            menus = new ArrayList<>();
        }

        if (gotoFirstTag("Menu")) {
            readMenu();
        }
    }

    private void readMenu() throws XmlPullParserException, IOException{
        boolean stop = false;
        Menu m = null;

        while (!stop) {

            boolean found = true;
            if (getParser().getName().equals("Sottomenu") && getParser().getEventType()==XmlPullParser.START_TAG) {
                //sono già posizionato all'inizio di un sottomenu
            }
            else
            {
             found = gotoNextStart("Sottomenu");
            }

            if (found) {
                // Sottomenu sottomenu = new Sottomenu();
                //sottomenus.add(sottomenu);
                //Menu menu = new Menu();
                m=new Menu();

                menus.add(m);
                //readSottomenu(sottomenu);
                readSottomenu(m);
            } else {
                stop = true;
            }

        }
    }

   // private void readSottomenu(Sottomenu sottomenu,Menu menu) throws XmlPullParserException, IOException {
        private void readSottomenu(Menu menu) throws XmlPullParserException, IOException {
        XmlPullParser parser = getParser();
        boolean stop = false;
        while (!stop) {
            parser.next();
            String name = parser.getName();
            if (name!=null){
                if (name.equals("Numero")) {
                    menu.setNumber(Integer.parseInt(parser.nextText()));
                } else if (name.equals("Nome")) {
                    menu.setName(parser.nextText());
                }else if (name.equals("Azione")) {
                    menu.setAction(Integer.parseInt(parser.nextText()));
                }else if (name.equals("Pagina")) {
                     menu.setPage(Integer.parseInt(parser.nextText()));
                }else if (name.equals("Sottomenu")) {
                    if(parser.getEventType()==XmlPullParser.END_TAG){
                        parser.next();
                    }
                    stop = true;
                }else{
                    stop = true;
                }
            }else{
                stop = true;
            }

        }
    }
//    public Sottomenu[] getSottomenu() {
//        return sottomenus.toArray(new Sottomenu [0]);
//    }

    public Menu[] getMenus() {
        return  menus.toArray(new Menu [0]);
    }
}
