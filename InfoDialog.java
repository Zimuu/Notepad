package kth.proj.notepad;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class InfoDialog extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infodialog);
        int i = getIntent().getIntExtra("info", 0);
        TextView tv = (TextView) findViewById(R.id.info);
        tv.setText(i);
        Button close = (Button) findViewById(R.id.close);
        close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
        });
    }
}
