package kth.proj.notepad;

import java.io.InputStream;
import java.io.Writer;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;


public class Notes {

	private static Set<Note> notes = new TreeSet<Note>() ;
	
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
		//System.out.println(notes);
	}  
	
	public static void writeXML(Writer writer) {
		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag(null, "notes");
			for (Note note : notes) {
				//System.out.println(note);
				serializer.startTag(null, "note");
				
				serializer.startTag(null, "title");
				serializer.text(note.getTitle());
				serializer.endTag(null, "title");
				
				serializer.startTag(null, "date");
				serializer.text(String.valueOf(note.getDate()));
				serializer.endTag(null, "date");
				
				serializer.startTag(null, "content");
				serializer.text(note.getContent());
				serializer.endTag(null, "content");
				serializer.endTag(null, "note");
			}
			serializer.endTag(null, "notes");
			serializer.endDocument();
			writer.flush();
			//System.out.println("output finished");
		} catch (Exception e) { e.printStackTrace(); } finally { try { writer.close(); } catch (Exception e) {} }
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
