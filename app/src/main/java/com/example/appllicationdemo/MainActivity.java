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
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomsheet.BottomSheetDialog;

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
	RelativeLayout rl_incomingcall;
	TextView displayname,callStatusText;
	ImageView ringingControlDecline,ringingControlAccept,iv_close;
	public static BottomSheetDialog dialog;


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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Init();

		//dialog(lisperCall_s);
		Lisper.InitLisper(MainActivity.this,"5060");

		Lisper.addCallback(new RegistrationCallback() {
			@Override
			public void registrationOk() {
				super.registrationOk();
				Log.e("TAG", "registrationOk: ");
				Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
				dialog_register("Registration Successful");
				/*Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
				dialog();*/
			}

			@Override
			public void registrationFailed() {
				super.registrationFailed();
				Log.e("TAG", "registrationFailed: ");
				dialog_register("Registration Failed");
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
				dialog_outgoingcall(lisperCall_s);
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
				dialog_endcall();
			}

			@Override
			public void error() {
				super.error();
			}
		});

		buttonCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				View view = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);

				dialog = new BottomSheetDialog(MainActivity.this);
				dialog.setContentView(view);
				dialog.show();
				//Lisper.MakeCall("sip:1007@pbx.lintelindia.com:5060");
			}
		});
	}

	public void Init(){
		rl_incomingcall = findViewById(R.id.rl_incomingcall);
		buttonCall = findViewById(R.id.buttonCall);
		displayname = findViewById(R.id.displayname);
		callStatusText = findViewById(R.id.callStatusText);
		ringingControlAccept = findViewById(R.id.ringingControlAccept);
		ringingControlDecline = findViewById(R.id.ringingControlDecline);
		iv_close = findViewById(R.id.iv_close);
	}

	public void accountConfig(View view) {
		dlgAccountSetting();
	}

	private void dlgAccountSetting() {

		LayoutInflater li = LayoutInflater.from(this);
		View view = li.inflate(R.layout.dlg_account_config, null);

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
		rl_incomingcall.setVisibility(View.VISIBLE);
		iv_close.setVisibility(View.GONE);
		//displayname.setText(lisperCall.getUserData().toString());
		callStatusText.setText("Incoming call");
		if(!ringingControlAccept.isShown()){
			ringingControlAccept.setVisibility(View.VISIBLE);
		}
		if(!ringingControlDecline.isShown()){
			ringingControlDecline.setVisibility(View.VISIBLE);
		}

		ringingControlAccept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Lisper.acceptCall(lisperCall);
				lisperCall_s = lisperCall;
				ringingControlAccept.setVisibility(View.GONE);
			}
		});

		ringingControlDecline.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Lisper.hangupCall(lisperCall);
				rl_incomingcall.setVisibility(View.GONE);
			}
		});
		/*new AlertDialog.Builder(MainActivity.this)
				.setTitle("Incoming Call.....")
				//.setMessage("Your Message")
				.setCancelable(false)
				.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
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
				}).show();*/
	}

	public void dialog_callconnected(LisperCall lisperCall){
		Log.e("handlem","5");
		callStatusText.setText("Call connected");
		ringingControlAccept.setVisibility(View.GONE);
		iv_close.setVisibility(View.GONE);
		ringingControlDecline.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Lisper.hangupCall(lisperCall);
				rl_incomingcall.setVisibility(View.GONE);
			}
		});

		/*new AlertDialog.Builder(MainActivity.this)
				.setTitle("Call connected...")
				//.setMessage("Your Message")
				.setCancelable(false)
				.setPositiveButton("Hang UP", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Whatever...
						dialog.dismiss();
						Lisper.hangupCall(lisperCall);
					}
				}).show();*/
	}

	public void dialog_register(String status){
		new AlertDialog.Builder(MainActivity.this)
				.setTitle("Registration Status ")
				.setMessage(status)
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Whatever...
						dialog.dismiss();
					}
				}).show();
	}

	public void dialog_outgoingcall(LisperCall lisperCall){
		rl_incomingcall.setVisibility(View.VISIBLE);
		ringingControlAccept.setVisibility(View.GONE);
		iv_close.setVisibility(View.GONE);
		callStatusText.setText("Connecting...");
		displayname.setText(KeyboardView.mPasswordField.getText().toString());

		ringingControlDecline.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Lisper.hangupCall(lisperCall);
				rl_incomingcall.setVisibility(View.GONE);
			}
		});
	}

	public void dialog_endcall(){
		rl_incomingcall.setVisibility(View.VISIBLE);
		if(ringingControlAccept.isShown()){
			ringingControlAccept.setVisibility(View.GONE);
		}
		if(ringingControlDecline.isShown()){
			ringingControlDecline.setVisibility(View.GONE);
		}

		iv_close.setVisibility(View.VISIBLE);
		callStatusText.setText("Call disconnected...");
		displayname.setText(KeyboardView.mPasswordField.getText().toString());

		iv_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rl_incomingcall.setVisibility(View.GONE);
			}
		});
	}
}
