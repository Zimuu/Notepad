package kth.proj.notepad;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;


public class Notes {

	private static Set<Note> notes;
	
	public static Note getNote(int id) {
		for (Note note : notes) {
			if (note.getId() == id)
				return note;
		}
		return null;
	}
	
	public static void readXML(InputStream in) throws Exception{
		Note note = null;
		XmlPullParser parser = Xml.newPullParser();  
		
		parser.setInput(in, "UTF-8");  
		int eventCode = parser.getEventType();  
		while (XmlPullParser.END_DOCUMENT != eventCode ) {  
			switch (eventCode) {  
				case XmlPullParser.START_DOCUMENT:
					notes = new TreeSet<Note>();  
					break;  
				case XmlPullParser.START_TAG:
					String name = parser.getName();
					if ("note".equals(name))  
						note = new Note();  
					if (note != null) {  
						if ("title".equals(name)) 
							note.setTitle(parser.nextText());  
						else if ("date".equals(name)) 
							note.setDate(new Long(parser.nextText())); 
						else if ("content".equals(name))
							note.setContent(parser.nextText());
					}  
					break;  
				case XmlPullParser.END_TAG:
					if("note".equals(parser.getName()) && notes != null){  
						notes.add(note);  
						note = null;  
					}
					break;  
			} 
			eventCode = parser.next();  
		}
		System.out.println(notes);
	}  
	
	public static void writeXML() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			
			Element rootElement = doc.createElement("Notes");
			doc.appendChild(rootElement);
			
			for (Note note : notes) {
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
			}
			//TransformerFactory tFactory = TransformerFactory.newInstance();
			//Transformer transformer = tFactory.newTransformer();
			//DOMSource source = new DOMSource(doc);
			//StreamResult stream = new StreamResult(FILE);
			
			//transformer.transform(source, stream);
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public static Set<Note> getNotes() {
		return notes;
	}
	
	public static void save(Note note) {
		Iterator<Note> it = notes.iterator();
		while (it.hasNext()) {
			Note n = it.next();
			if (n.getId() == note.getId()) {
				n = note;
				return;
			}
		}
		notes.add(note);
	}

	public static void delete(int id) {
		Iterator<Note> it = notes.iterator();
		while (it.hasNext()) {
			Note note = it.next();
			if (note.getId() == id) 
				it.remove();
		}
	}
}
