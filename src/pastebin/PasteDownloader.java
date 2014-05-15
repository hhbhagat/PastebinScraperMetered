package pastebin;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

public class PasteDownloader {

    public static void savePastes(ArrayList<Paste> arr) {
        File file = null;
        for (Paste p : arr) {

            //String heuristics = detectType(Jsoup.connect(p.getURL().toString()).toString());
            //maybe later ^^^

            URL v = p.getURL();
            File folder = new File("L:" + File.separator + "pastes" + File.separator);
            try {
                if (!(p.getType().contentEquals("None"))) {
                    file = new File("L:" + File.separator + "pastes" + File.separator + p.getType() + File.separator + "Pastebin" + " - " + p.getTitle() + " - " + p.getURL().toString().replace("http://pastebin.com/raw.php?i=", "") + ".txt");
                } else {
                    file = new File("L:" + File.separator + "pastes" + File.separator + "Pastebin" + " - " + p.getTitle() + " - " + p.getURL().toString().replace("http://pastebin.com/raw.php?i=", "") + ".txt");
                }

                if (!(file.exists())) { //check for overlapping files. This is a bit 
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                    FileUtils.copyURLToFile(v, file);
                    System.out.println("Created file for: " + p.getTitle());
                }
                else{
                    System.out.println("File " + file.getName() +" exists");
                }
            } catch (Exception e) {
                System.out.println("Sorry, the site had blocked you");
            }
            try {
                Thread.sleep(10*new Random().nextInt(180));
            } catch (InterruptedException ex) {
            }
        }
    }
    
}
