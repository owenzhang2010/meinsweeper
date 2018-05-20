import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class StatsHelper {
    public static void xmlSetup() {
        File f = new File("stats.xml");
        if (!f.exists()) {
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document document = docBuilder.newDocument();

                Element rootElement = document.createElement("stats");
                document.appendChild(rootElement);

                initializeStatsHelper(document, rootElement, "beginner");
                initializeStatsHelper(document, rootElement, "intermediate");
                initializeStatsHelper(document, rootElement, "expert");
                initializeStatsHelper(document, rootElement, "lottery");

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

    private static void initializeStatsHelper(Document d, Element root, String difficulty) {
        Element e = d.createElement(difficulty);
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
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
            Element e = (Element) document.getElementsByTagName(difficulty).item(0);
            stats.put("average", e.getAttribute("average"));
            stats.put("best", e.getAttribute("best"));
            stats.put("wins", e.getAttribute("wins"));
            stats.put("played", e.getAttribute("played"));
            stats.put("percentage", e.getAttribute("percentage"));
            StatsBox.display(difficulty, stats);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void winGame(String difficulty, double time) { // update all stats for game mode
        File f = new File("stats.xml");
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
            Element e = (Element) document.getElementsByTagName(difficulty).item(0);
            double average = Double.valueOf(e.getAttribute("average"));
            double best = Double.valueOf(e.getAttribute("best"));
            int wins = Integer.valueOf(e.getAttribute("wins"));
            int played = Integer.valueOf(e.getAttribute("played"));

            average = (Double.isNaN(average)) ? time : ((average * wins) + time) / (wins + 1);
            best = (Double.isNaN(best)) ? time : (time < best) ? time : best;
            wins += 1; played += 1;
            double percentage = ((double) wins) / ((double) played);
            e.setAttribute("average", Double.toString(round(average, 2)));
            e.setAttribute("best", Double.toString(round(best, 2)));
            e.setAttribute("wins", Integer.toString(wins));
            e.setAttribute("played", Integer.toString(played));
            e.setAttribute("percentage", Double.toString(round(percentage * 100, 2)));

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(f);
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loseGame(String difficulty) { // update played/% for game mode
        File f = new File("stats.xml");
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
            Element e = (Element) document.getElementsByTagName(difficulty).item(0);
            int wins = Integer.valueOf(e.getAttribute("wins"));
            int played = Integer.valueOf(e.getAttribute("played"));
            played += 1;
            double percentage = ((double) wins) / ((double) played);
            e.setAttribute("played", Integer.toString(played));
            e.setAttribute("percentage", Double.toString(round(percentage * 100, 2)));

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(f);
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void resetMode(String mode) {
        File f = new File("stats.xml");
        if (f.exists()) {
            try {
                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
                Element e = (Element) document.getElementsByTagName(mode).item(0);
                e.setAttribute("average", "NaN");
                e.setAttribute("best", "NaN");
                e.setAttribute("wins", "0");
                e.setAttribute("played", "0");
                e.setAttribute("percentage", "NaN");

                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                DOMSource source = new DOMSource(document);
                StreamResult result = new StreamResult(f);
                transformer.transform(source, result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void resetAll() {
        resetMode("beginner");
        resetMode("intermediate");
        resetMode("expert");
        resetMode("lottery");
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
