
package com.vi.boshtest;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.AndroidConnectionConfiguration;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.BOSHConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.BOSHConnection;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

public class ChatActivity extends Activity {
	boolean bosh = false;
    BOSHConnection boshconnection;
    XMPPConnection xmppconnection;
    ArrayList<String> messages = new ArrayList();
    Handler handler = new Handler();
    EditText etmessage;
    ListView list;
    String user, password, recipient, host, domain;
    int port;
    Button send;
	SharedPreferences savedStrings;

    @Override
    public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatactivity);
		savedStrings = getSharedPreferences("StoredValues", 0);
		bosh = savedStrings.getBoolean("bosh", false);
		if (bosh) {
			user = savedStrings.getString("bosh_user", "");
			password = savedStrings.getString("bosh_password", "");
			recipient = savedStrings.getString("bosh_recipient", "");
			host = savedStrings.getString("bosh_host", "");
			domain = savedStrings.getString("bosh_domain", "");
			port = savedStrings.getInt("bosh_port", 0);
	        user += "@"; //ensure user.indexOf("@") works if no @ was in the string
		} else {
			user = savedStrings.getString("xmpp_user", "");
			password = savedStrings.getString("xmpp_password", "");
			recipient = savedStrings.getString("xmpp_recipient", "");
			host = savedStrings.getString("xmpp_host", "");
			domain = savedStrings.getString("xmpp_domain", "");
			port = savedStrings.getInt("xmpp_port", 0);			
		}
        
        list = (ListView)this.findViewById(R.id.listMessages);
        setListAdapter();

        etmessage = (EditText)this.findViewById(R.id.sendText);

        send = (Button) this.findViewById(R.id.send);
        
        new Thread() {
            @Override 
            public void run() {
            	setConnection();
            }
        }.start();
    }
    
    private void setConnection()
    {
        try {
        	SmackAndroid.init(this);
        	if (bosh) {
        		BOSHConfiguration config = new BOSHConfiguration(false, host, port, "/http-bind/", domain);
        		config.setSASLAuthenticationEnabled(true);
        		SASLAuthentication.supportSASLMechanism("PLAIN", 0);
        		boshconnection = new BOSHConnection(config);
        		boshconnection.DEBUG_ENABLED = true;
        	} else {
        		ConnectionConfiguration config = new ConnectionConfiguration(host, port, domain);
        		config.setSASLAuthenticationEnabled(true);
        		SASLAuthentication.supportSASLMechanism("PLAIN", 0);
        		xmppconnection = new XMPPConnection(config);
        		xmppconnection.DEBUG_ENABLED = true;
        	}
			try {
				Log.d("BoshTest", "ATTEMPT CONNECT");
				if (bosh) boshconnection.connect();
				else xmppconnection.connect();
				Log.d("BoshTest", "CONNECTION SUCCESS");

				try {
					Log.d("BoshTest", "ATTEMPT LOGIN");
					if (bosh) boshconnection.login(user.substring(0, user.indexOf("@")), password, "ASmackAndroidTest");
					else xmppconnection.login(user, password, "ASmackAndroidTest");
					Log.d("BoshTest", "LOGIN SUCCESS");
				} catch(Exception e) {
					Log.d("BoshTest", "LOGIN FAILED");
					e.printStackTrace();
	            	finish();
				}
				
			} catch (Exception e) {
				Log.d("BoshTest", "CONNECTION FAILED");
				e.printStackTrace();
            	finish();
			}

			/* method 1
			Log.d("SRNDPT", "ATTEMPT PACKETFILTER STUFF");
			PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            connection.addPacketListener(new PacketListener() {
                public void processPacket(Packet packet) {
                    Message message = (Message) packet;
                    if (message.getBody() != null) {
                        String fromName = StringUtils.parseBareAddress(message.getFrom());
                        messages.add(fromName + ":");
                        messages.add(message.getBody());
                        // Add the incoming message to the list view
                        handler.post(new Runnable() {
                            public void run() {
                                setListAdapter();
                            }
                        });
                    } else {
                    	Log.d("BoshTest", "Message body null!");
                    }
                }
            }, filter);
			
			send.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	                String to = recipient;
	                String text = etmessage.getText().toString();
	                etmessage.setText("");

	                Message msg = new Message(to, Message.Type.chat);
	                msg.setBody(text);
	                connection.sendPacket(msg);
	                messages.add(connection.getUser() + ":");
	                messages.add(text);
	                setListAdapter();
	                
	            }
	        });*/
			
			ChatManager chatmanager;
			if (bosh) chatmanager = boshconnection.getChatManager();
			else chatmanager = xmppconnection.getChatManager();
			final Chat newChat = chatmanager.createChat(recipient,
			 new MessageListener() {
			    public void processMessage(Chat chat, Message message) {
			    	String fromName = StringUtils.parseBareAddress(message.getFrom());
                    messages.add(fromName + ":");
                    messages.add(message.getBody() + "");
			    	handler.post(new Runnable() {
                        public void run() {
                            setListAdapter();
                        }
                    });
			    }
			});
			
			send.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	                String to = recipient;
	                String text = etmessage.getText().toString();
	                etmessage.setText("");
                	messages.add("You (" + user + "):");
                	messages.add(text);
                	setListAdapter();
	                try {
	                	Message msg = new Message(to, Message.Type.chat);
	                	msg.setBody(text);
	                	newChat.sendMessage(msg);
	                } catch(Exception e) {e.printStackTrace();}
	                
	            }
	        });
			
        } catch(Exception e) {e.printStackTrace();}
    }

    private void setListAdapter()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.multi_line_list_item, messages);
        list.setAdapter(adapter);
    }
   
    @Override
    protected void onDestroy() {
    	super.onStop();
    	if (bosh) boshconnection.disconnect();
        else xmppconnection.disconnect();
    }

}