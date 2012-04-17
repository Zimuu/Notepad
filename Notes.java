package kth.proj.notepad;

import java.util.HashSet;
import java.util.Set;

public class Notes {

	public static final Set<Note> NOTES = new HashSet<Note>();
	
	public static Note getNote(int id) {
		for (Note note : NOTES) {
			if (note.getId() == id)
				return note;
		}
		return null;
	}
	
	public static void readXML() {}
	
	public static void writeXML() {}
}
