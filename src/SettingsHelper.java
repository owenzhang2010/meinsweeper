import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

import java.util.Map;
import java.util.HashMap;

public class SettingsHelper {
    public static void setDifficulty(String difficulty) {
        File f = new File("settings.xml");
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
            Element e = (Element) document.getElementsByTagName("difficulty").item(0);
            e.setNodeValue(difficulty);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Integer> getSettings() {
        File f = new File("settings.xml");
        Map<String, Integer> settings = new HashMap<>();
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
            String difficulty = document.getElementsByTagName("difficulty").item(0).getTextContent();
            Element e = (Element) document.getElementsByTagName(difficulty).item(0);
            settings.put("mines", Integer.valueOf(e.getAttribute("mines")));
            settings.put("width", Integer.valueOf(e.getAttribute("width")));
            settings.put("height", Integer.valueOf(e.getAttribute("height")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return settings;
    }
}
