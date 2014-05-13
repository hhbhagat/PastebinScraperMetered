package pastebin;

import java.net.URL;

public abstract class Paste {
    
    protected String pURL = "";
    protected String pTitle = "";
    protected String pType = "";
    
    public abstract void setURL(String u);
    public abstract void setType(String pt);
    public abstract void setTitle(String t);
    public abstract URL getURL();
    public abstract String getType();
    public abstract String getTitle();
}
