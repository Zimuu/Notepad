package kth.proj.notepad;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MessageDialog extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messagedialog);
        final EditText phoneNumber = (EditText) findViewById(R.id.phoneField);
        final String note = getIntent().getStringExtra("note");
        Button send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone = phoneNumber.getText().toString().trim();
				if (phone.length() != 0) {
					Uri uri = Uri.parse("smsto:" + phone);
					Intent i = new Intent(Intent.ACTION_SENDTO, uri);
					i.putExtra("sms_body", note);
					MessageDialog.this.startActivity(i);
				}
					
			}
        	
        });
        Button close = (Button) findViewById(R.id.close);
        close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
        	
        });
    }
}
