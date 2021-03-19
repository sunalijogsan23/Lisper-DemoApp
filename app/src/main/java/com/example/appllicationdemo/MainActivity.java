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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import org.pjsip.pjsua2.BuddyConfig;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.lisper.Lisper;
import org.pjsip.pjsua2.lisper.MyCall;
import org.pjsip.pjsua2.pjsip_status_code;

import java.util.HashMap;

public class MainActivity extends Activity implements Lisper.StatusObs{

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


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
					Boolean reg_status = Lisper.Account_Regi(acc_id,registrar,username,password,MainActivity.this);
					if(reg_status){
						Toast.makeText(getApplicationContext(),"Register",Toast.LENGTH_LONG).show();
					}else {
						Toast.makeText(getApplicationContext(),"Fail",Toast.LENGTH_LONG).show();
					}

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

	@Override
	public void notifyRegState(pjsip_status_code code, String reason, int expiration) {
		Log.e("tag","code---->" + code);
		String msg_str = "";
		if (expiration == 0){
			msg_str += "Unregistration";
		}
		else
		{
			msg_str += "Registration";
		}

		if (code.swigValue()/100 == 2){
			msg_str += " successful";
		}
		else{
			msg_str += " failed: " + reason;
		}

		Log.e("msg_reg_activity",msg_str);
	}

	@Override
	public void notifyIncomingCall(MyCall call) {

	}

	@Override
	public void notifyCallState(MyCall call) {

	}

	public void makeCall(View view) {
		Lisper.getAccInfo();
		Lisper.MakeCall("sip:1007@pbx.lintelindia.com:5060");
		//showCallActivity();
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

	public void dialog(){
		Log.e("handlem","4");
		new AlertDialog.Builder(MainActivity.this)
				.setTitle("Your Alert")
				.setMessage("Your Message")
				.setCancelable(false)
				.setPositiveButton("ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Whatever...
						//Lisper.acceptCall();
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
