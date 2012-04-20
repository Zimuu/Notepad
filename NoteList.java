package kth.proj.notepad;
 
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
 
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
 

@SuppressWarnings("unused")
public class NoteList extends ListActivity {
	private ListView listView;
	//private NotesAdapter notesAdapter;
	//private ArrayAdapter arrayAdapter;
	private static final int ADDNOTE = -1;
	private static final int DELETEALLNOTES = 1;
	private static final int DELETENOTE = 2;   
	private static final int EDITNOTE = 3;
	//private static final int NEW_NOTE = -1;
	private static final String KEY_TITLE = "title";
	private static final String KEY_BODY = "body";
	private static final String KEY_ROWID = "_id";
	private AlertDialog confirmDelete;
	private AdapterView<?> adapter;
	
	private Set<Note> notes;
   
	@Override
	public void onCreate(Bundle savedInstanceState) {           
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notelist);
		final ListView lView = getListView();
		
		registerForContextMenu(lView);
		readTesting();//switch to readFromDataBase() when testing database
		
		setListAdapter(new ArrayAdapter<Note>(this, R.layout.notelist, (Note[]) notes.toArray()));
		updateList();
		lView.setOnItemClickListener(new OnItemClickListener() {         
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
				Note listItem = (Note) lView.getItemAtPosition(position);
			}
		}); 
	}
    
	/**
	 * read from database
	 */
	private void readFromDatabase() {
		try {
			InputStream in = this.openFileInput("database.xml");
			Notes.readXML(in);
		} catch (Exception e) { e.printStackTrace(); }
		notes = Notes.getNotes();
	}
	
	/**
	 * recommended for testing
	 */
	private void readTesting() {
		notes = new HashSet<Note>();
		notes.add(new Note("hey1", 1, 1, "content1"));
		notes.add(new Note("hey2", 2, 1, "content2"));
		notes.add(new Note("hey3", 3, 1, "content3"));
		notes.add(new Note("hey4", 4, 1, "content4"));
		notes.add(new Note("hey5", 5, 1, "content5"));
		notes.add(new Note("hey5", 6, 1, "content6"));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);                                        
		Intent i = new Intent(this, EditNote.class);
		startActivityForResult(i, position);//Will this work? use getListView().getItemAtPosition(position)
	}   

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, ADDNOTE, 0, R.string.addnote);
		menu.add(0, DELETENOTE, 0, R.string.delete);
		menu.add(0, DELETEALLNOTES, 0, R.string.deleteall);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.string.addnote:
				createNote();
				return true;
			case R.string.deleteall:
				confirmDelete =  new AlertDialog.Builder(this)
						.setTitle(R.string.confirmdeleteall)
						.setPositiveButton(R.string.close, new CloseOperation())
						.create();
				clearNotes();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}                       

	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case DELETENOTE:
				AdapterContextMenuInfo deleteInfo = (AdapterContextMenuInfo) item.getMenuInfo();
				Notes.delete(deleteInfo.position);
				return true;
			case EDITNOTE:
				AdapterContextMenuInfo editInfo = (AdapterContextMenuInfo) item.getMenuInfo();
				Intent i = new Intent(this, EditNote.class);
				i.putExtra(KEY_ROWID, editInfo.position);
				startActivityForResult(i,EDITNOTE);
				return true;
		}
		updateList();
		return super.onContextItemSelected(item);
	}

	private void createNote() {
		Intent i = new Intent(this, EditNote.class);
		startActivityForResult(i, ADDNOTE);
	}   

	/**
	 * you can change this commentary back to code in your project
	 * i just don't want see error signs in my project :)
	 */
	private void updateList() {   //TODO
		//String[] from = new String[] { NotesDbAdapter.KEY_TITLE };
		//int[] to = new int[] { R.id.text1 };
		//SimpleCursorAdapter notes =  new SimpleCursorAdapter(this, R.layout.notes_row, c, from, to);
		//setListAdapter(notes);
	}

	public void clearNotes() {
		Notes.clear();
	}

	private class CloseOperation implements DialogInterface.OnClickListener {
	
	@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	}
	
	@Override
	protected void onStart() {}
	@Override
	protected void onRestart() {}
	@Override
	protected void onResume() {}
	@Override
	protected void onPause() {}
	@Override
	protected void onStop() {}
	@Override
	protected void onDestroy() {}
}