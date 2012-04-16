package kth.proj.notepad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class EditNote extends Activity {
	
	public static final int SAVE = 0;
	public static final int SEND = 1;
	public static final int EXIT = 2;
	public static final int FONT = 3;
	
	private EditText note;
	private EditText title;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editnote);
        note = (EditText) findViewById(R.id.noteField);
        title = (EditText) findViewById(R.id.titleField);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, SAVE, 0, R.string.save);
		menu.add(0, SEND, 0, R.string.send);
		menu.add(0, EXIT, 0, R.string.exit);
		menu.add(0, FONT, 0, R.string.font);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case SAVE:
				save();
				break;		
			case SEND:
				send();
				break;		
			case EXIT:
				exit();
				break;	
			case FONT:
				font();
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void save() {
		String titleString = title.getText().toString();
		if (titleString.trim().length() == 0) {
			Intent i = new Intent();
			i.putExtra("info", R.string.err1);
			i.setClass(this, InfoDialog.class);
			this.startActivity(i);
			return;
		}
		String noteString = checkEmptyNote();
		if (noteString == null) return;
		
	}
	
	private void send() {
		String string = checkEmptyNote();
		if (string == null) return;
		
		Intent i = new Intent();
		i.setClass(EditNote.this, MessageDialog.class);
		i.putExtra("note", string);
		this.startActivity(i);
	}
	
	private String checkEmptyNote() {
		String string = note.getText().toString();
		if (string.trim().length() != 0)
			return string;
		
		Intent i = new Intent();
		i.setClass(this, InfoDialog.class);
		i.putExtra("info", R.string.err3);
		this.startActivity(i);
		return null;
	}
	
	private void exit() {}
	
	private void font() {}

}
