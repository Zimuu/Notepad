package kth.proj.notepad;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleAdapter;


public class NoteList extends ListActivity {
	
	public static final String OPERATION = "operation";
	public static final int ADDNOTE = -1; 
	public static final int OPENDRAFT = -2;
	public static final String DATE = "date";
	public static final String TITLE = "title";
	public static final String ID = "ID";
	
    private static final int DELETEALL = 2;
    private static final int DELETENOTE = 1;
    private static final int EDITNOTE = 0;

	
    
	@Override
	public void onCreate(Bundle savedInstanceState) {    	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notelist);
		
		init();
		updateList();
		registerForContextMenu(getListView());
	}
	
	private void init() {
		InputStream in = null;
		try {
			in = this.openFileInput("database.xml");
			Notes.readXML(in);
		} catch (Exception e) { e.printStackTrace(); }
		
		if (Notes.hasDraft()) {
			new AlertDialog.Builder(this)
				.setTitle(R.string.confirmdialog)
				.setMessage(R.string.con6)
				.setPositiveButton(R.string.open, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
			        	Intent i = new Intent(NoteList.this, EditNote.class);
			        	i.putExtra(OPERATION, OPENDRAFT);
			        	NoteList.this.startActivity(i);
					}
					
				})
				.setNegativeButton(R.string.close, new CloseOperation())
				.create().show();
		}
	}
    
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id); 
		Intent i = new Intent(this, EditNote.class);
		i.putExtra(OPERATION, position);
		startActivityForResult(i, EDITNOTE);
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, ADDNOTE + 2, 0, R.string.addnote);
		menu.add(0, DELETEALL, 0, R.string.deleteall);
		return super.onCreateOptionsMenu(menu);
	}
        
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(0, EDITNOTE, 0, R.string.edit_note);
		menu.add(0, DELETENOTE, 0, R.string.deletenote);
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(R.string.options);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case ADDNOTE + 2:
				createNote();
				break;
			case DELETEALL:
				new AlertDialog.Builder(this)
				.setTitle(R.string.confirmdialog)
				.setMessage(R.string.con2)
				.setPositiveButton(R.string.clear, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Notes.clear();
						updateList();
					}
					
				})
				.setNegativeButton(R.string.close, new CloseOperation())
				.create().show();
				break;
		}
	    return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()) {
			case DELETENOTE:
				Notes.delete(info.position);
				break;
			case EDITNOTE:
				Intent i = new Intent(this, EditNote.class);
				i.putExtra(OPERATION, info.position);
				startActivityForResult(i, EDITNOTE);
				updateList();
				break;
		}
		updateList();
		return super.onContextItemSelected(item);
	}
        
	private void createNote() { 
		Intent i = new Intent(this, EditNote.class);
		i.putExtra(OPERATION, ADDNOTE);
		startActivity(i);
	}   
		
	private void updateList() {
		List<HashMap<String, String>> notes = new ArrayList<HashMap<String, String>>();
		for (Note note : Notes.getNotes()) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(DATE, note.dateFormat());
			map.put(TITLE, note.getTitle());
			notes.add(map);
		}
		SimpleAdapter listAdapter = new SimpleAdapter(
				this, notes, R.layout.note, 
				new String[] { DATE, TITLE},
				new int[] { R.id.date, R.id.title });
		setListAdapter(listAdapter);
	}
	    
	private class CloseOperation implements DialogInterface.OnClickListener {
	
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onRestart() {
		updateList();
		super.onRestart();
	}
	
	@Override
	protected void onResume() {
		updateList();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	 
	@Override
	protected void onDestroy()  {
		 write();
		 super.onDestroy();
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
	    
 }
