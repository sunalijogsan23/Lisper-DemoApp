/* $Id: MainActivity.java 4870 2014-07-03 09:43:19Z bennylp $ */
/*
 * Copyright (C) 2013 Teluu Inc. (http://www.teluu.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.example.appllicationdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.lintel.lisper.Lisper;
import org.lintel.lisper.LisperCall;
import org.lintel.lisper.PhoneCallback;
import org.lintel.lisper.RegistrationCallback;
import org.pjsip.pjsua2.BuddyConfig;
import org.pjsip.pjsua2.OnRegStateParam;

import java.util.HashMap;

import static org.lintel.lisper.Lisper.app;

public class MainActivity extends Activity{

	ImageButton buttonCall;
	LisperCall lisperCall_s=null;


	public class MSG_TYPE {
		public final static int INCOMING_CALL = 1;
		public final static int CALL_STATE = 2;
		public final static int REG_STATE = 3;
		public final static int BUDDY_STATE = 4;
	}

	private HashMap<String, String> putData(String uri, String status) {
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("uri", uri);
		item.put("status", status);
		return item;
	}

	private void showCallActivity() {
        Intent intent = new Intent(this, CallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
	}

	public void broadcastIntent(View view){
		Intent intent = new Intent();
		intent.setAction("com.tutorialspoint.CUSTOM_INTENT");
		sendBroadcast(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		buttonCall = findViewById(R.id.buttonCall);

		Lisper.InitLisper(MainActivity.this,"5060");

		Lisper.addCallback(new RegistrationCallback() {
			@Override
			public void registrationOk() {
				super.registrationOk();
				Log.e("TAG", "registrationOk: ");
				Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();

				/*Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
				dialog();*/
			}

			@Override
			public void registrationFailed() {
				super.registrationFailed();
				Log.e("TAG", "registrationFailed: ");
				Toast.makeText(MainActivity.this, "登录失败！", Toast.LENGTH_SHORT).show();
			}
		}, new PhoneCallback() {
			@Override
			public void incomingCall(LisperCall lisperCall) {
				super.incomingCall(lisperCall);
				Log.e("TAG", "Incoming call: ");
				lisperCall_s = lisperCall;
				dialog(lisperCall_s);
			}

			@Override
			public void outgoingInit() {
				super.outgoingInit();
				Log.e("TAG", "Outgoing call: ");
			}

			@Override
			public void callConnected() {
				super.callConnected();
				Log.e("TAG", "Connect call: ");
				dialog_callconnected(lisperCall_s);
			}

			@Override
			public void callEnd() {
				super.callEnd();
				Log.e("TAG", "End call: ");
			}

			@Override
			public void error() {
				super.error();
			}
		});

		buttonCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Lisper.MakeCall("sip:1007@pbx.lintelindia.com:5060");
			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_acc_config:
			dlgAccountSetting();
			break;

		case R.id.action_quit:
			/*Message m = Message.obtain(handler, 0);
			m.sendToTarget();*/
			break;

		default:
			break;
		}

		return true;
	}

	public void accountConfig(View view) {
		dlgAccountSetting();
	}

	private void dlgAccountSetting() {

		LayoutInflater li = LayoutInflater.from(this);
		View view = li.inflate(R.layout.dlg_account_config, null);

		/*if (lastRegStatus.length()!=0) {
			TextView tvInfo = (TextView)view.findViewById(R.id.textViewInfo);
			tvInfo.setText("Last status: " + lastRegStatus);
		}*/

		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setView(view);
		adb.setTitle("Account Settings");

		final EditText etId    = (EditText)view.findViewById(R.id.editTextId);
		final EditText etReg   = (EditText)view.findViewById(R.id.editTextRegistrar);
		final EditText etProxy = (EditText)view.findViewById(R.id.editTextProxy);
		final EditText etUser  = (EditText)view.findViewById(R.id.editTextUsername);
		final EditText etPass  = (EditText)view.findViewById(R.id.editTextPassword);

		adb.setCancelable(false);
		adb.setPositiveButton("OK",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
			    	String acc_id 	 = etId.getText().toString();
			    	String registrar = etReg.getText().toString();
			    	//String proxy 	 = etProxy.getText().toString();
			    	String username  = etUser.getText().toString();
			    	String password  = etPass.getText().toString();

			    	Lisper lisper = new Lisper();
					Lisper.Account_Regi(username,password,registrar,MainActivity.this);

			    }
			  });
		adb.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  });

		AlertDialog ad = adb.create();
		ad.show();
	}

	private void dlgAddEditBuddy(BuddyConfig initial) {
		final BuddyConfig cfg = new BuddyConfig();
		final BuddyConfig old_cfg = initial;
		final boolean is_add = initial == null;

		LayoutInflater li = LayoutInflater.from(this);
		View view = li.inflate(R.layout.dlg_add_buddy, null);

		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setView(view);

		final EditText etUri    = (EditText)view.findViewById(R.id.editTextUri);
		final CheckBox cbSubs  = (CheckBox)view.findViewById(R.id.checkBoxSubscribe);

		if (is_add) {
			adb.setTitle("Add Buddy");
		} else {
			adb.setTitle("Edit Buddy");
			etUri. setText(initial.getUri());
			cbSubs.setChecked(initial.getSubscribe());
		}

		adb.setCancelable(false);
		adb.setPositiveButton("OK",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
			    	cfg.setUri(etUri.getText().toString());
			    	cfg.setSubscribe(cbSubs.isChecked());

			    	/*if (is_add) {
			    		account.addBuddy(cfg);
						buddyList.add(putData(cfg.getUri(), ""));
						buddyListAdapter.notifyDataSetChanged();
						buddyListSelectedIdx = -1;
			    	} else {
			    		if (!old_cfg.getUri().equals(cfg.getUri())) {
			    			account.delBuddy(buddyListSelectedIdx);
			    			account.addBuddy(cfg);
							buddyList.remove(buddyListSelectedIdx);
							buddyList.add(putData(cfg.getUri(), ""));
			    			buddyListAdapter.notifyDataSetChanged();
			    			buddyListSelectedIdx = -1;
			    		} else if (old_cfg.getSubscribe() != cfg.getSubscribe()) {
			    			MyBuddy bud = account.buddyList.get(buddyListSelectedIdx);
							try {
				    			bud.subscribePresence(cfg.getSubscribe());
							} catch (Exception e) {}
			    		}
			    	}*/
			    }
			  });
		adb.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  });

		AlertDialog ad = adb.create();
		ad.show();
	}

	public void addBuddy(View view) {
		dlgAddEditBuddy(null);
	}

	public void editBuddy(View view) {
		//Lisper.acceptCall();
		/*if (buddyListSelectedIdx == -1)
			return;

		BuddyConfig old_cfg = account.buddyList.get(buddyListSelectedIdx).cfg;
		dlgAddEditBuddy(old_cfg);*/
	}

	public void delBuddy(View view) {
		//Lisper.hangupCall();
		/*if (buddyListSelectedIdx == -1)
			return;

		final HashMap<String, String> item = (HashMap<String, String>) buddyListView.getItemAtPosition(buddyListSelectedIdx);
		String buddy_uri = item.get("uri");

		DialogInterface.OnClickListener ocl = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which) {
		        case DialogInterface.BUTTON_POSITIVE:
		        	account.delBuddy(buddyListSelectedIdx);
		    		buddyList.remove(item);
		    		buddyListAdapter.notifyDataSetChanged();
		    		buddyListSelectedIdx = -1;
		            break;
		        case DialogInterface.BUTTON_NEGATIVE:
		            break;
		        }
		    }
		};

		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(buddy_uri);
		adb.setMessage("\nDelete this buddy?\n");
		adb.setPositiveButton("Yes", ocl);
		adb.setNegativeButton("No", ocl);
		adb.show();*/
	}

	public void dialog(LisperCall lisperCall){
		Log.e("handlem","4");
		new AlertDialog.Builder(MainActivity.this)
				.setTitle("Your Alert")
				.setMessage("Your Message")
				.setCancelable(false)
				.setPositiveButton("ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Whatever...
						Lisper.acceptCall(lisperCall);
						lisperCall_s = lisperCall;
						dialog.dismiss();
					}
				}).setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Lisper.hangupCall(lisperCall);
					dialog.dismiss();
				}
				}).show();
	}

	public void dialog_callconnected(LisperCall lisperCall){
		Log.e("handlem","5");
		new AlertDialog.Builder(MainActivity.this)
				.setTitle("Call connected")
				.setMessage("Your Message")
				.setCancelable(false)
				.setPositiveButton("Hang UP", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Whatever...
						dialog.dismiss();
						Lisper.hangupCall(lisperCall);
					}
				}).show();
	}

	/*public void notifyIncomingCall(MyCall call) {
		Message m = Message.obtain(handler, MSG_TYPE.INCOMING_CALL, call);
		m.sendToTarget();
	}

	public void notifyRegState(pjsip_status_code code, String reason, int expiration) {
		Log.e("tag","code---->" + code);
		String msg_str = "";
		if (expiration == 0)
			msg_str += "Unregistration";
		else
			msg_str += "Registration";

		if (code.swigValue()/100 == 2)
			msg_str += " successful";
		else
			msg_str += " failed: " + reason;

		Message m = Message.obtain(handler, MSG_TYPE.REG_STATE, msg_str);
		m.sendToTarget();
	}

	public void notifyCallState(MyCall call) {
		if (currentCall == null || call.getId() != currentCall.getId())
			return;

		CallInfo ci;
		try {
			ci = call.getInfo();
		} catch (Exception e) {
			ci = null;
		}
		Message m = Message.obtain(handler, MSG_TYPE.CALL_STATE, ci);
		m.sendToTarget();

		if (ci != null &&
			ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED)
		{
			currentCall = null;
		}
	}

	public void notifyBuddyState(MyBuddy buddy) {
		Message m = Message.obtain(handler, MSG_TYPE.BUDDY_STATE, buddy);
		m.sendToTarget();
	}*/

}
