package pastebin;

import java.net.URL;
import java.util.ArrayList;

public class InvolatilePaste extends Paste{

    //Paste properties
    private String pURL = "";
    private String pTitle = "";
    private String pType = "";
    private String rawURL = "";

    //<editor-fold defaultstate="collapsed" desc="Setters">
    @Override
    public void setURL(String u) {
        pURL = u;
    }

    @Override
    public void setType(String pt) {
        pType = pt;
    }

    @Override
    public void setTitle(String t) {
        pTitle = t;
    }

    public void setRawURL(String ru) {
        rawURL = ru;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Getters">
    @Override
    public URL getURL() {
        try {
            return new URL(pURL);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getType() {
        return pType;
    }

    @Override
    public String getTitle() {
        return pTitle;
    }

    public String getRawURL() {
        return rawURL;
    }

    //</editor-fold>
    
    @Override
    public int getHash(){
        return pURL.hashCode();
    }
    
}
