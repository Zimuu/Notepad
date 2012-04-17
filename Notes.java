package kth.proj.notepad;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Notes {

	private static final File FILE = new File("src\\kth\\proj\\notepad\\Database.xml");
	private static final Set<Note> NOTES = new HashSet<Note>();
	
	public static Note getNote(int id) {
		for (Note note : NOTES) {
			if (note.getId() == id)
				return note;
		}
		return null;
	}
	
	public static void readXML() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(FILE);
			doc.getDocumentElement().normalize();
			
			NodeList list = doc.getElementsByTagName("Notes");
			
			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					NOTES.add(new Note(
							getTagValue("title", element)
							, Long.parseLong(getTagValue("date", element))
							, getTagValue("content", element)));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void writeXML() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			
			Element rootElement = doc.createElement("Notes");
			doc.appendChild(rootElement);
			
			int i = 0;
			for (Note note : NOTES) {
				Element resultTag = doc.createElement("Note");
				rootElement.appendChild(resultTag);

				Element titleTag = doc.createElement("title");
				titleTag.appendChild(doc.createTextNode(note.getTitle()));
				resultTag.appendChild(titleTag);
				
				Element dateTag = doc.createElement("date");
				dateTag.appendChild(doc.createTextNode(String.valueOf(note.getDate())));
				resultTag.appendChild(dateTag);
				
				Element contentTag = doc.createElement("content");
				contentTag.appendChild(doc.createTextNode(
						note.getContent()));
				resultTag.appendChild(contentTag);
				
				i++;
				if (i == 10) break;
			}
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult stream = new StreamResult(FILE);
			
			transformer.transform(source, stream);
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	private static String getTagValue(String tag, Element element) {
		NodeList list = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node value = (Node)list.item(0);
		return value.getNodeValue();
	}
	
	public static Set<Note> getNotes() {
		return NOTES;
	}
	
	public static void save(Note note) {
		Iterator<Note> it = NOTES.iterator();
		while (it.hasNext()) {
			Note n = it.next();
			if (n.getId() == note.getId()) {
				n = note;
				return;
			}
		}
		NOTES.add(note);
	}
	
	public static void delete(int id) {
		for (Note note : NOTES) {
			if (note.getId() == id)
				NOTES.remove(note);
		}
	}
}
