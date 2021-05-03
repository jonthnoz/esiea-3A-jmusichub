package musichub.util;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.SAXException;

import org.w3c.dom.*;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;

/** 
 * Class creating a DOM Object from XML file and vice versa
 * @author Felicia Ionascu,  Jonathan Ozouf
 */
public class XMLHandler {
	TransformerFactory transformerFactory;
	Transformer transformer;
	DocumentBuilderFactory documentFactory;
	DocumentBuilder documentBuilder;

	public XMLHandler() {
		try {
			transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			documentFactory = DocumentBuilderFactory.newInstance();
			documentBuilder = documentFactory.newDocumentBuilder();
		} catch (TransformerException tfe) {
            tfe.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        }
	}
	
	public void createXMLFile(Document document, String filePath)
	{
		try {
		// create the xml file
        //transform the DOM Object to an XML File
		DOMSource domSource = new DOMSource(document);
		StreamResult streamResult = new StreamResult(new File(filePath));

		// If you use
		// StreamResult result = new StreamResult(System.out);
		// the output will be pushed to the standard output ...
		// You can use that for debugging 

		transformer.transform(domSource, streamResult);
		
		} catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
	}
	
	public Document createXMLDocument()
	{
		return documentBuilder.newDocument();
	}		
	
	public NodeList parseXMLFile (String filePath) {
		NodeList elementNodes = null;
		
		// The class loader that loaded the class
        ClassLoader classLoader = getClass().getClassLoader();
        // the stream holding the file content
        InputStream inputStream = classLoader.getResourceAsStream(filePath);

		try {
			Document document= documentBuilder.parse(inputStream);
			Element root = document.getDocumentElement();
			
			elementNodes = root.getChildNodes();	
		}
		catch (SAXException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) 
		{
			System.out.println("File not found: " + filePath);
		}
		
		return elementNodes;
	}
	
}