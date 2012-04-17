package kth.proj.notepad;

import java.util.zip.Inflater;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("unused")
public class EditNote extends Activity {
	
	public static final int SAVE = 0;
	public static final int SEND = 1;
	public static final int EXIT = 2;
	public static final int FONT = 3;
	public static final int ALARM = 4;
	public static final int DELETE = 5;

	public static final int DEFAULT_COLOR = Color.BLACK;
	public static final int DEFAULT_STYLE = 0;
	public static final int DEFAULT_Size = 12;
	
	private EditText note;
	private EditText title;
	private AlertDialog info;
	private Builder infoBuilder;
	private AlertDialog confirm;
	private Builder confirmBuilder;
	
	private AlertDialog font;
	private TextView size;
	private int style;
	private int color;
	private int textSize;
	
	private boolean saved;
	private Note currentNote;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editnote);
        note = (EditText) findViewById(R.id.noteField);
        title = (EditText) findViewById(R.id.titleField);
        
        //currentNote = Notes.getNote(/*getIntent().getIntExtra("noteid", 0)*/2);
        //note.setText(currentNote.getContent());
        //title.setText(currentNote.getTitle());
        
        infoBuilder = new AlertDialog.Builder(this)
			.setTitle(R.string.infodialog)
			.setPositiveButton(R.string.confirm, new CloseOperation());
        confirmBuilder =  new AlertDialog.Builder(this)
			.setTitle(R.string.confirmdialog)
			.setNegativeButton(R.string.close, new CloseOperation());

        createFontDialog();
    }
    
    private void createFontDialog() {
        LayoutInflater inflator = getLayoutInflater();
        View layout = inflator.inflate(R.layout.fontdialog, (ViewGroup) findViewById(R.id.fontlayout));
        font = new AlertDialog.Builder(this)
        	.setView(layout)
        	.setTitle(R.string.font)
        	.setPositiveButton(R.string.confirm, new FontListener())
        	.setNegativeButton(R.string.close, new CloseOperation())
        	.create();
        
        size = (TextView) layout.findViewById(R.id.sizeField);
        
        Spinner fontSpinner = (Spinner) layout.findViewById(R.id.fontspinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
        		this, R.array.styles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontSpinner.setAdapter(adapter);
        fontSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				style = position + 1;
			}

			@Override
			public void onNothingSelected(AdapterView<?> nothing) {
				style = DEFAULT_STYLE;
			}
        });
        
        Spinner colorSpinner = (Spinner) layout.findViewById(R.id.colorspinner);
        adapter = ArrayAdapter.createFromResource(
        		this, R.array.colors, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(adapter);
        colorSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				color = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> nothing) {
				color = DEFAULT_STYLE;
			}
			
        });
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
		
		currentNote.setTitle(title.getText().toString());
		currentNote.setContent(noteString);
		currentNote.setDate(System.currentTimeMillis());
		saved = true;
		Notes.save(currentNote);
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
						//TODO
						finish();
					}
				
				});
		if (saved)
			confirm = confirmBuilder.setMessage(R.string.con1).create();
		else
			confirm = confirmBuilder.setMessage(R.string.con4).create();
		confirm.show();
	}
	
	private void font() {
		font.show();
	}
	
	//TODO
	private void alarm() {}
	
	//TODO
	private void delete() {
		Notes.delete(currentNote.getId());
		Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
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
	
	private class FontListener implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			int s = 0;
			try {
				s = Integer.parseInt(size.getText().toString());
			} catch (Exception e) {
				s = 18;
			}
			textSize = (s > 50)
					? 50 : s;
			note.setTextSize(textSize);
			int c = 0;
			switch (color) {
				case 0:
					c = Color.BLACK;
					break;
				case 1:
					c = Color.RED;
					break;
				case 2:
					c = Color.BLUE;
					break;
				case 3:
					c = Color.YELLOW;
					break;
				case 4:
					c = Color.GREEN;
			}
			note.setTextColor(c);
			note.setTypeface(note.getTypeface(), style);
		}
	}
	
	private class CloseOperation implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	}
}
