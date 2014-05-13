package pastebin;

import java.net.URL;

public class VolatilePaste extends Paste{

    //Paste properties
    //
    //Strings
    String pURL = "";
    String pTitle = "";
    String pType = "";
    String rawURL = "";
    //Numbers
    int expTime = 0;
    long expTimeEpoch = 0;

    public VolatilePaste() {
    }

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

    public void setExpiry(int e) {
        expTime = e;
    }

    public void setExpiry_Epoch(long ee) {
        expTimeEpoch = ee;
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
    public String getType(){
        return pType;
    }
    
    @Override
    public String getTitle(){
        return pTitle;
    }
    
    public int getExpiry(){
        return expTime;
    }
    
    public long getExpiry_Epoch(){
        return expTimeEpoch;
    }
    //</editor-fold>
    
}
