/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 *
 * @author guberti
 */
public class XMLParser {
    String path;
    ScriptEngine engine;
    
    public XMLParser(String path) {
        this.path = path;
        ScriptEngineManager mgr = new ScriptEngineManager();
        this.engine = mgr.getEngineByName("JavaScript");
    }
    
    public List<GridComponent> parseObjects() throws FileNotFoundException {
        List<GridComponent> parts = new ArrayList<>();

        try {

            File file = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("shape");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) nNode;
                        GridComponent e = parseElement(element);
                        parts.add(e);
                    }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File path is invalid!");
            throw e;
        } catch (Exception e) { 
            e.printStackTrace();
        }
        return parts;
    }
    
    GridComponent parseElement(Element element) {
        // All elements have x, y, z, phi, theta, radius, and type
        String type = element.getElementsByTagName("type").item(0).getTextContent();
        Vector v = new Vector();
        v.x = Double.parseDouble(element.getElementsByTagName("x").item(0).getTextContent());
        v.y = Double.parseDouble(element.getElementsByTagName("y").item(0).getTextContent());
        v.z = Double.parseDouble(element.getElementsByTagName("z").item(0).getTextContent());
        
        v.phi = parsePiDouble(element, "phi");
        v.theta = parsePiDouble(element, "theta");
                
        double radius = parsePiDouble(element, "radius");
        int charge = parseCharge(element);

        switch (type) {
            case "Cylinder":            
                double height = parsePiDouble(element, "height");
                return new Cylinder(v, radius, height, charge);
            case "TorusSegment":
                double radius2 = parsePiDouble(element, "radius2");
                double phi2 = parsePiDouble(element, "phi2");
                double phi3 = parsePiDouble(element, "phi3");
                return new TorusSegment(v, radius, phi2, phi3, radius2, charge);
            default:
                System.out.println("Unknown type " + type);
                return null;
        }
    }
    public double parsePiDouble(Element element, String tag) {
        String tC = element.getElementsByTagName(tag).item(0).getTextContent();
        /*tC = tC.toLowerCase(); // Squash all UC letters
        tC = tC.replaceAll("pi", "3.141592653589793");
        tC = tC.replaceAll("tau", "6.283185307179586");
        Object parsed;
        try {
            parsed = engine.eval(tC);
        } catch (ScriptException ex) {
            System.out.println("Could not parse expression \"" + tC + "\" from " + 
                    tag + " property of " + element.getElementsByTagName("type").item(0).getTextContent());
        }
        return(Double.parseDouble(engine.eval(tC).toString()));*/
        
        // Replace pi, Pi, PI, and π with 3.141592653589793
        
        if (tC.contains("pi")) {
            String[] containsPoint = tC.split("pi");
            return Double.parseDouble(containsPoint[1])*Math.PI;
        } else {
            return Double.parseDouble(tC);
        }
    }
    public int parseCharge(Element element) {
        return (int) parsePiDouble(element, "charge");
    }
}
