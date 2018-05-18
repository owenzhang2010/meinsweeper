import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class StatsHelper {
    public static void xmlSetup() {
        if (!(new File("stats.xml").exists())) {
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document doc = docBuilder.newDocument();

                Element rootElement = doc.createElement("stats");
                doc.appendChild(rootElement);

                initializeStatsHelper(doc, rootElement, "beginner");
                initializeStatsHelper(doc, rootElement, "intermediate");
                initializeStatsHelper(doc, rootElement, "expert");
                initializeStatsHelper(doc, rootElement, "lottery");

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File("stats.xml"));
                transformer.transform(source, result);
            } catch (ParserConfigurationException pce) {
                pce.printStackTrace();
            } catch (TransformerException tfe) {
                tfe.printStackTrace();
            }
        }
    }

    private static void initializeStatsHelper(Document d, Element root, String difficulty) {
        Element e = d.createElement(difficulty);
        e.setAttribute("average", "NaN");
        e.setAttribute("best", "NaN");
        e.setAttribute("wins", "0");
        e.setAttribute("played", "0");
        e.setAttribute("percentage", "NaN");
        root.appendChild(e);
    }

    public static void winGame() { // update all stats for gamemode

    }

    public static void loseGame() { // update played/% for gamemode

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
