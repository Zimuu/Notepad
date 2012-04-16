package kth.proj.notepad;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class EditNote extends Activity {
	
	public static final int SAVE = 0;
	public static final int SEND = 1;
	public static final int EXIT = 2;
	public static final int FONT = 3;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editnote);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, SAVE, R.string.save);
		menu.add(0, 1, SEND, R.string.send);
		menu.add(0, 1, EXIT, R.string.exit);
		menu.add(0, 1, FONT, R.string.font);
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
	
	private void save() {}
	private void send() {}
	private void exit() {}
	private void font() {}

}
