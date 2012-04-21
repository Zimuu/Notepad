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
	private static Note draft;
	
	public static Note getNote(int id) {
		for (Note note : notes) {
			if (note.getId() == id)
				return note;
		}
		return null;
	}
	
	public static void readXML(InputStream in){
		Note note = null;
		try {
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
							else if ("alarm".equals(name))
								note.setAlarm(new Long(parser.nextText()));
							else if ("content".equals(name))
								note.setContent(parser.nextText());
							continue;
						}  
						if ("draft".equals(name))
							draft = new Note();
						if (draft != null) {
							if ("title".equals(name)) 
								draft.setTitle(parser.nextText());  
							else if ("date".equals(name)) 
								draft.setDate(new Long(parser.nextText())); 
							else if ("alarm".equals(name))
								draft.setAlarm(new Long(parser.nextText()));
							else if ("content".equals(name))
								draft.setContent(parser.nextText());
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
		} catch (Exception e) { e.printStackTrace(); } finally { try { in.close(); } catch(Exception e) {} }
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
				
				if (note.getAlarm() != -1){
					serializer.startTag(null, "alarm");
					serializer.text(String.valueOf(note.getAlarm()));
					serializer.endTag(null, "alarm");
				}
				
				serializer.startTag(null, "content");
				serializer.text(note.getContent());
				serializer.endTag(null, "content");
				serializer.endTag(null, "note");
			}
			if (draft != null) {
				serializer.startTag(null, "draft");
				serializer.startTag(null, "title");
				serializer.text(draft.getTitle());
				serializer.endTag(null, "title");
				
				serializer.startTag(null, "date");
				serializer.text(String.valueOf(draft.getDate()));
				serializer.endTag(null, "date");
				
				if (draft.getAlarm() != -1){
					serializer.startTag(null, "alarm");
					serializer.text(String.valueOf(draft.getAlarm()));
					serializer.endTag(null, "alarm");
				}
				
				serializer.startTag(null, "content");
				serializer.text(draft.getContent());
				serializer.endTag(null, "content");
				serializer.endTag(null, "draft");
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
	
	public static void saveDraft(Note note) {
		draft = note;
	}
	
	public static Note getDraft() {
		return draft;
	}
	
	public static boolean checkDraft() {
		return draft == null;
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
	
	public static void clear() {
		notes.clear();
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
