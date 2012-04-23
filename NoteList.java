package kth.proj.notepad;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
    private static final int DELETENOTE = 4;
    private static final int EDITNOTE = 3;

	
    
	@Override
	public void onCreate(Bundle savedInstanceState) {    	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notelist);
		
		init();
		updateList();
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
				return true;
			case EDITNOTE:
				Intent i = new Intent(this, EditNote.class);
				i.putExtra(OPERATION, info.position);
				startActivityForResult(i, EDITNOTE);
				updateList();
				return true;
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
		 super.onRestart();
	 }
	
	 @Override
	 protected void onResume() {
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
		 super.onDestroy();
	 }
	    
 }
