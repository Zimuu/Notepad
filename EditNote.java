package kth.proj.notepad;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Set;
import java.util.zip.Inflater;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

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
	public static final float DEFAULT_SIZE = 18f;
	
	private EditText note;
	private EditText title;
	private AlertDialog info;
	private Builder infoBuilder;
	private AlertDialog confirm;
	private Builder confirmBuilder;
	
	private AlertDialog alarm;
	private Calendar calendar = Calendar.getInstance();
	
	private AlertDialog font;
	private Spinner colorSpinner;
	private Spinner styleSpinner;
	private int style = DEFAULT_COLOR;
	private int color = DEFAULT_STYLE;
	private float size = DEFAULT_SIZE;
	
	private boolean alarmed;
	private boolean saved;
	private Note currentNote;
	private boolean running = true;
	private Thread draftThread;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editnote);
        note = (EditText) findViewById(R.id.noteField);
        title = (EditText) findViewById(R.id.titleField);
        
        Intent i = getIntent();
        int code = i.getIntExtra(NoteList.OPERATION, 0);
        System.out.println(code);
        switch (code) {
	        case -2:
	        	currentNote = Notes.getDraft();
	        	break;
	        case - 1:
	        	currentNote = new Note();
	        	break;
        	default:
        		currentNote = Notes.getNote(code);
        }
		if (currentNote.getAlarm() != -1) calendar.setTimeInMillis(currentNote.getAlarm());
		note.setText(currentNote.getContent());
		title.setText(currentNote.getTitle());

Notes.print();
        infoBuilder = new AlertDialog.Builder(this)
			.setTitle(R.string.infodialog)
			.setPositiveButton(R.string.confirm, new CloseOperation());
        confirmBuilder =  new AlertDialog.Builder(this)
			.setTitle(R.string.confirmdialog)
			.setNegativeButton(R.string.close, new CloseOperation());

        createAlarmDialog();
        createFontDialog();
        draftThread = new Thread(new Draft());
        draftThread.start();
    }
    
    private void createAlarmDialog() {
        LayoutInflater inflator = getLayoutInflater();
        View layout = inflator.inflate(R.layout.alarmdialog, (ViewGroup) findViewById(R.id.alarmlayout));

        DatePicker datepicker = (DatePicker) layout.findViewById(R.id.datepicker);
        TimePicker timepicker = (TimePicker) layout.findViewById(R.id.timepicker);
        
        datepicker.init(calendar.get(Calendar.YEAR)
        		, calendar.get(Calendar.MONTH)
        		, calendar.get(Calendar.DAY_OF_MONTH)
        		, new DatePicker.OnDateChangedListener() {
        	@Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear,
            		int dayOfMonth) {
            	calendar.set(year, monthOfYear, dayOfMonth);
            }
        });
        
        timepicker.setIs24HourView(true);
        timepicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            
        	@Override
        	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        		calendar.set(Calendar.MINUTE, minute);
        	}
        });
        
        ToggleButton tb = (ToggleButton) layout.findViewById(R.id.togglebutton);
        tb.setChecked((currentNote.getAlarm() == -1) ? false : true);
        tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
				alarmed = isChecked;
			}
        	
        });
        
        alarm = new AlertDialog.Builder(this)
        	.setView(layout)
        	.setTitle(R.string.alarm)
        	.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (alarmed) 
		            			if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
		            				calendar.setTime(new Date(System.currentTimeMillis()));
		            				Toast.makeText(EditNote.this, R.string.illegalalarm, Toast.LENGTH_SHORT).show();
		            			} else currentNote.setAlarm(calendar.getTimeInMillis());
					else
						currentNote.setAlarm(-1);
				}
			})
        	.setNegativeButton(R.string.close, new CloseOperation())
        	.create();
    }
    
    private void createFontDialog() {
        LayoutInflater inflator = getLayoutInflater();
        View layout = inflator.inflate(R.layout.fontdialog, (ViewGroup) findViewById(R.id.fontlayout));
        
        final EditText sizeField = (EditText) layout.findViewById(R.id.sizeField);
        
        styleSpinner = (Spinner) layout.findViewById(R.id.fontspinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
        		this, R.array.styles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        styleSpinner.setAdapter(adapter);
        styleSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

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
        
        colorSpinner = (Spinner) layout.findViewById(R.id.colorspinner);
        adapter = ArrayAdapter.createFromResource(
        		this, R.array.colors, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(adapter);
        colorSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
					case 0:
						color = Color.BLACK;
						break;
					case 1:
						color = Color.RED;
						break;
					case 2:
						color = Color.BLUE;
						break;
					case 3:
						color = Color.YELLOW;
						break;
					case 4:
						color = Color.GREEN;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> nothing) {
				color = DEFAULT_STYLE;
			}
			
        });
        Button button = (Button) layout.findViewById(R.id.reset);
        button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				size = DEFAULT_SIZE;
				style = DEFAULT_STYLE;
				color = DEFAULT_COLOR;
				confirmFont();
			}
        	
        });
        
        font = new AlertDialog.Builder(this)
    	.setView(layout)
    	.setTitle(R.string.font)
    	.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
    		
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    			float s = 0;
    			try {
    				s = Float.parseFloat(sizeField.getText().toString());
    			} catch (Exception e) {
    				s = DEFAULT_SIZE;
    			}
    			size = (s > 50) ? 50 : ((s < 10) ? DEFAULT_SIZE : s);
				confirmFont();
    		}
    	})
    	.setNegativeButton(R.string.close, new CloseOperation())
    	.create();
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
				break;
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
		
		if (currentNote == null) currentNote = new Note();
		currentNote.setTitle(title.getText().toString());
		currentNote.setContent(noteString);
		currentNote.setDate(System.currentTimeMillis());
		if (alarmed) currentNote.setAlarm(calendar.getTimeInMillis());
		else currentNote.setAlarm(-1);
		saved = true;
		Notes.saveDraft(null);
		Notes.save(currentNote);
		Toast.makeText(EditNote.this, R.string.saved, Toast.LENGTH_SHORT).show();
Notes.print();
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
						if (phoneNumber.trim().length() == 0) {
							Toast.makeText(EditNote.this, R.string.err2, Toast.LENGTH_SHORT).show();
							return ;
						}
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
		if (!saved)
			confirm = confirmBuilder.setMessage(R.string.con1).create();
		else
			confirm = confirmBuilder.setMessage(R.string.con4).create();
		confirm.show();
	}
	
	private void font() {
		font.show();
		colorSpinner.setSelection(color);
		styleSpinner.setSelection(style);
	}
	
	private void alarm() {
		alarm.show();
	}
	
	private void delete() {
		Notes.delete(currentNote.getId());
		Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
		finish();
	}
	
	private void saveDraft() {
		Notes.saveDraft(new Note(
				title.getText().toString(),
				System.currentTimeMillis(), 
				(alarmed) ? calendar.getTimeInMillis() : -1, 
				note.getText().toString()));		
	}
	
	private void confirmFont() {
		note.setTextSize(size);
		note.setTextColor(color);
		note.setTypeface(note.getTypeface(), style);
		Toast.makeText(EditNote.this, R.string.fontchanged, Toast.LENGTH_SHORT).show();	
	}
	
	@Override
	protected void onResume() {
        draftThread = new Thread(new Draft());
        draftThread.start();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		running = false;
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		try {
			saveDraft();
			FileOutputStream outStream = this.openFileOutput("database.xml", Context.MODE_PRIVATE);
			OutputStreamWriter writer = new OutputStreamWriter(outStream, "UTF-8");
			Notes.writeXML(writer);
			running = false;
			draftThread = null;
Notes.print();
		} catch (Exception e) { e.printStackTrace(); }
		super.onDestroy();
	}
	
	private class CloseOperation implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	}
	
	private class Draft implements Runnable {
		private int timer = 36;
		@Override
		public void run() {
			while (running) {
				if (timer <= 0 && changed()) {
					saveDraft();
					timer = 36;
Notes.print();
				}
				timer--;
				if (changed()) 
					saved = false;
				try {
					Thread.sleep(5000);
				} catch (Exception e) {}
			}
		}
		
		private boolean changed() {
			return !currentNote.getTitle().equals(title.getText().toString())
					|| !currentNote.getContent().equals(note.getText().toString());
		}
		
	}
	
}
