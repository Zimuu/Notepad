package kth.proj.notepad;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;

public class NoteList extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
		AssetManager asset = getAssets();  
		InputStream in = null;
		try {
			in = asset.open("database.xml");  
			Notes.readXML(in);
		} catch (Exception e) {} finally { try { in.close(); } catch (IOException e) {} }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notelist);
    }
}