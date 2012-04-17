package kth.proj.notepad;

import java.sql.Date;

public final class Note implements Comparable<Note>{

	private static int id = 0;
	private String title;
	private Date date;
	private String content;
	
	public Note(String title, Date date, String content) {
		id++;
		this.title = title;
		this.date = date;
		this.content = content;
	}

	@Override
	public int compareTo(Note o) {
		if (o.date == this.date &&
				o.title == this.title &&
				o.content == this.content)
			return 0;
		return o.date.compareTo(this.date);
	}
	
	@Override
	public int hashCode() {
		return String.valueOf(content).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		Note o = (Note) obj;
		return (o.title.equals(this.title) && 
				this.date.equals(this.date) && 
				this.content.equals(this.content));
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}

	public String getDate() {
		return date.toString();
	}

	public String getContent() {
		return content;
	}
	
	public int getId() {
		return id;
	}
	
	public void deleteNote() {
		id--;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
