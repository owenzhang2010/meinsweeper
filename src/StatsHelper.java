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
import org.w3c.dom.Node;

public class StatsHelper {
    public static void xmlSetup() {
        if (!(new File("stats.xml").exists())) {
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document doc = docBuilder.newDocument();

                Element rootElement = doc.createElement("stats");
                doc.appendChild(rootElement);

                Element browser = doc.createElement("BROWSER");
                browser.appendChild(doc.createTextNode("chrome"));
                rootElement.appendChild(browser);
                Element base = doc.createElement("BASE");
                base.appendChild(doc.createTextNode("http:fut"));
                rootElement.appendChild(base);
                Element employee = doc.createElement("EMPLOYEE");
                rootElement.appendChild(employee);
                Element empName = doc.createElement("EMP_NAME");
                empName.appendChild(doc.createTextNode("Anhorn, Irene"));
                employee.appendChild(empName);
                Element actDate = doc.createElement("ACT_DATE");
                actDate.appendChild(doc.createTextNode("20131201"));
                employee.appendChild(actDate);

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

    private void initializeStatsHelper(Document d, String difficulty) {
        Element e = d.createElement(difficulty);
        Element average = d.createElement("average");
    }

    public static String getDifficulty() {
        return null;
    }

    public static void setDifficulty() {

    }

    public static void winGame() { // update all stats for gamemode

    }

    public static void loseGame() { // update played for gamemode

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
