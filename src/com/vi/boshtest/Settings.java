
package com.vi.boshtest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class Settings extends Activity {
	SharedPreferences storedValues;
	SharedPreferences.Editor editor;
    EditText ethost, etdomain, etport, etuser, etpassword, etrecipient;
    View rbxmpp, rbbosh;
    Button btnOK;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
		storedValues = getSharedPreferences("StoredValues", 0);
		editor = storedValues.edit();

        ethost = (EditText)this.findViewById(R.id.et_host);
        etdomain = (EditText)this.findViewById(R.id.et_domain);
        etport = (EditText)this.findViewById(R.id.et_port);
        etuser = (EditText)this.findViewById(R.id.et_user);
        etpassword = (EditText)this.findViewById(R.id.et_password);
        etrecipient = (EditText)this.findViewById(R.id.et_recipient);
        rbxmpp = this.findViewById(R.id.rb_xmpp);
        rbbosh = this.findViewById(R.id.rb_bosh);
        btnOK = (Button)this.findViewById(R.id.ok);

		if (!storedValues.getBoolean("bosh", false)) {
			((RadioButton)rbxmpp).setChecked(true);
			onRadioButtonClicked(rbxmpp);
		} else {
			((RadioButton)rbbosh).setChecked(true);
			onRadioButtonClicked(rbbosh);
		}
    }
    
    public void onRadioButtonClicked(View view) {
        String str = "";
        switch(view.getId()) {
            case R.id.rb_xmpp:
                editor.putBoolean("bosh", false);
                editor.commit();
                	
        		str = storedValues.getString("xmpp_host", "talk.google.com");
    			ethost.setText(str);
    			str = storedValues.getString("xmpp_domain", "gmail.com");
    			etdomain.setText(str);
    			str = "" + storedValues.getInt("xmpp_port", 5222);
    			etport.setText(str);
    			str = storedValues.getString("xmpp_user", "");
    			etuser.setText(str);
    			str = storedValues.getString("xmpp_password", "");
    			etpassword.setText(str);
    			str = storedValues.getString("xmpp_recipient", "");
    			etrecipient.setText(str);
    			break;
    			
            case R.id.rb_bosh:
                editor.putBoolean("bosh", true);
                editor.commit();
                	
        		str = storedValues.getString("bosh_host", "xmpp.viprod.com");
        		ethost.setText(str);
        		str = storedValues.getString("bosh_domain", "viprod.com");
        		etdomain.setText(str);
        		str = "" + storedValues.getInt("bosh_port", 5280);
        		etport.setText(str);
        		str = storedValues.getString("bosh_user", "test1@viprod.com");
        		etuser.setText(str);
        		str = storedValues.getString("bosh_password", "test1");
        		etpassword.setText(str);
        		str = storedValues.getString("bosh_recipient", "test2@viprod.com");
        		etrecipient.setText(str);
                break;
        }
    }
    
    public void onButtonClicked(View view) {
    	if (view.getId() == btnOK.getId()) {
    		if (storedValues.getBoolean("bosh", false)) {
    			editor.putString("bosh_host", ethost.getText().toString());
    			editor.putString("bosh_domain", etdomain.getText().toString());
    			editor.putInt("bosh_port", Integer.parseInt(etport.getText().toString()));
    			editor.putString("bosh_user", etuser.getText().toString());
    			editor.putString("bosh_password", etpassword.getText().toString());
    			editor.putString("bosh_recipient", etrecipient.getText().toString());
    			editor.commit();
    		} else {
    			editor.putString("xmpp_host", ethost.getText().toString());
    			editor.putString("xmpp_domain", etdomain.getText().toString());
    			editor.putInt("xmpp_port", Integer.parseInt(etport.getText().toString()));
    			editor.putString("xmpp_user", etuser.getText().toString());
    			editor.putString("xmpp_password", etpassword.getText().toString());
    			editor.putString("xmpp_recipient", etrecipient.getText().toString());
    			editor.commit();
    		}

            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
    	}
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    }
   
    @Override
    protected void onStop() {
    	super.onStop();
    }
}