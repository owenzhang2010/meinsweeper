import java.io.File;
import java.util.Map;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class StatsHelper {
    private static Document document;

    public static void xmlSetup() {
        File f = new File("stats.xml");
        if (!f.exists()) {
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                document = docBuilder.newDocument();

                Element rootElement = document.createElement("stats");
                document.appendChild(rootElement);

                initializeStatsHelper(rootElement, "beginner");
                initializeStatsHelper(rootElement, "intermediate");
                initializeStatsHelper(rootElement, "expert");
                initializeStatsHelper(rootElement, "lottery");

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(document);
                StreamResult result = new StreamResult(new File("stats.xml"));
                transformer.transform(source, result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void initializeStatsHelper(Element root, String difficulty) {
        Element e = document.createElement(difficulty);
        e.setAttribute("average", "NaN");
        e.setAttribute("best", "NaN");
        e.setAttribute("wins", "0");
        e.setAttribute("played", "0");
        e.setAttribute("percentage", "NaN");
        root.appendChild(e);
    }

    public static void showStats(String difficulty) {
        Map<String, String> stats = new HashMap<>();
        File f = new File("stats.xml");
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
            Element e = (Element) document.getElementsByTagName(difficulty).item(0);
            stats.put("average", e.getAttribute("average"));
            stats.put("best", e.getAttribute("best"));
            stats.put("wins", e.getAttribute("wins"));
            stats.put("played", e.getAttribute("played"));
            stats.put("percentage: ", e.getAttribute("percentage"));
            StatsBox.display(difficulty, stats);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void winGame() { // update all stats for game mode

    }

    public static void loseGame() { // update played/% for game mode

    }

    public static void resetMode(String mode) {

    }

    public static void resetAll() {
        resetMode("beginner");
        resetMode("intermediate");
        resetMode("expert");
        resetMode("lottery");
    }
}
