package xml.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class DatabaseTesterActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        read();
        System.out.println(Notes.getNotes());
        System.out.println("------------");
        add();
        System.out.println(Notes.getNotes());
        System.out.println("------------");
        write();
        System.out.println(Notes.getNotes());
        delete();
        System.out.println(Notes.getNotes());
        System.out.println("------------");
        write();
        System.out.println(Notes.getNotes());
        System.out.println("------------");
        read();
        System.out.println(Notes.getNotes());
        System.out.println("------------");
        write();
        System.out.println(Notes.getNotes());
    }

    private Note n1 = new Note("1", 1, 1, "1");
    private Note n2 = new Note("2", 2, 2, "2");
    private Note n3 = new Note("3", 3, 3, "3");
    //private Note n4 = new Note("4", 4, 4, "4");
    //private Note n5 = new Note("5", 5, 5, "5");
    //private Note n6 = new Note("6", 6, 6, "6");
    
    private void add() {
    	Notes.save(n1);
    	Notes.save(n2);
    	Notes.save(n3);
    }
    
    private void write() {
    	FileOutputStream outStream;
		try {
			outStream = this.openFileOutput("database.xml", Context.MODE_WORLD_WRITEABLE);
	    	OutputStreamWriter writer = new OutputStreamWriter(outStream, "UTF-8");
	    	Notes.writeXML(writer);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
    
    private void read() {
        InputStream in = null;
		try {
			in = this.openFileInput("database.xml");
			Notes.readXML(in);
		} catch (Exception e) { e.printStackTrace(); }
	}
    
    private void delete() {
    	Notes.delete(2);
    	Notes.delete(3);
    }
}