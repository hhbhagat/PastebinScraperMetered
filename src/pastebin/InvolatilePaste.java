package pastebin;

import java.net.URL;

public class InvolatilePaste {

    //Paste properties
    String pURL = "";
    String pTitle = "";
    String pType = "";
    String rawURL = "";
    
    //<editor-fold defaultstate="collapsed" desc="Setters">
    public void setURL(String u) {
        pURL = u;
    }

    public void setType(String pt) {
        pType = pt;
    }

    public void setTitle(String t) {
        pTitle = t;
    }
    
    public void setRawURL(String ru){
        rawURL = ru;
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Getters">
    public URL getURL() {
        try {
            return new URL(pURL);
        } catch (Exception e) {
            return null;
        }
    }
    
    public String getType(){
        return pType;
    }
    
    public String getTitle(){
        return pTitle;
    }
    
    public String getRawURL(){
        return rawURL;
    }
    
    //</editor-fold>
    
    
    
}
