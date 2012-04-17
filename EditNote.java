package kth.proj.notepad;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class EditNote extends Activity {
	
	public static final int SAVE = 0;
	public static final int SEND = 1;
	public static final int EXIT = 2;
	public static final int FONT = 3;
	public static final int ALARM = 4;
	public static final int DELETE = 5;
	
	private EditText note;
	private EditText title;
	private AlertDialog info;
	private Builder infoBuilder;
	private AlertDialog confirm;
	private Builder confirmBuilder;
	
	private boolean saved;
	private Note currentNote;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editnote);
        note = (EditText) findViewById(R.id.noteField);
        title = (EditText) findViewById(R.id.titleField);
        
        //currentNote = Notes.getNote(getIntent().getIntExtra("noteid", 0));
        //note.setText(currentNote.getContent());
        //title.setText(currentNote.getTitle());
        
        infoBuilder = new AlertDialog.Builder(this)
			.setTitle(R.string.infodialog)
			.setPositiveButton(R.string.confirm, new CloseOperation());
        confirmBuilder =  new AlertDialog.Builder(this)
			.setTitle(R.string.confirmdialog)
			.setNegativeButton(R.string.close, new CloseOperation());
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, SAVE, 0, R.string.save);
		menu.add(0, SEND, 0, R.string.send);
		menu.add(0, EXIT, 0, R.string.exit);
		menu.add(0, FONT, 0, R.string.font);
		menu.add(0, ALARM, 0, R.string.alarm);
		menu.add(0, DELETE, 0, R.string.delete);
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
				break;
			case ALARM:
				alarm();
				break;
			case DELETE:
				delete();
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void save() {
		String titleString = title.getText().toString();
		if (titleString.trim().length() == 0) {
			info = infoBuilder.setMessage(R.string.err1).create();
			info.show();
			return;
		}
		String noteString = checkNote();
		if (noteString == null) return;
		
		saved = true;
		Toast.makeText(EditNote.this, R.string.saved, Toast.LENGTH_SHORT).show();
	}
	
	private void send() {
		final String string = checkNote();
		if (string == null) return;
		
		final EditText phone = new EditText(this);
		
		new AlertDialog.Builder(this)
				.setTitle(R.string.sms)
				.setMessage(R.string.phone)
				.setView(phone)
				.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String phoneNumber = phone.getText().toString();
						Uri uri = Uri.parse("smsto:" + phoneNumber);
						Intent i = new Intent(Intent.ACTION_SENDTO, uri);
						i.putExtra("sms_body", string);
						EditNote.this.startActivity(i);
					}
				})
				.setNegativeButton(R.string.close, new CloseOperation())
				.create().show();	
	}
	
	private String checkNote() {
		String string = note.getText().toString();
		if (string.trim().length() != 0)
			return string;  
		
		info = infoBuilder.setMessage(R.string.err3).create();
		info.show();
		return null;
	}
	
	private void exit() {
		confirmBuilder
				.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				
				});
		if (saved)
			confirm = confirmBuilder.setMessage(R.string.con1).create();
		else
			confirm = confirmBuilder.setMessage(R.string.con4).create();
		confirm.show();
	}
	
	private void font() {}
	
	private void alarm() {}
	
	private void delete() {
		Notes.getNotes().remove(currentNote);
		finish();
	}

	/**
	 * Change status of note to unsaved when user presses any key
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		saved = false;
		return super.onKeyDown(keyCode, event);
	}
	
	private class CloseOperation implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	}
}
