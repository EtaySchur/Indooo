package com.indoormap;





import java.util.List;

import com.indoormap.R.id;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.net.ParseException;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private Button loginButton,signupButton,createNewAccountButton;
	private EditText usernameText,passwordText,confirmPasswordText,eMailText;
	private Context context;
	private String username;
	private ProgressDialog progressDialog;
	private int durationLong,durationShort;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		loginButton = (Button) findViewById(id.loginButton);
		signupButton = (Button) findViewById(id.signupButton);
		createNewAccountButton = (Button) findViewById(id.createNewAccount);
		confirmPasswordText = (EditText) findViewById(id.confirmPassword);
		eMailText = (EditText) findViewById(id.eMailText);
		usernameText = (EditText) findViewById(id.usernameText);
		passwordText = (EditText) findViewById(id.passwordText);
		context=this;
		parseInit(this,getIntent());
		usernameText.setText("");
		passwordText.setText("");
		confirmPasswordText.setVisibility(View.INVISIBLE);
		eMailText.setVisibility(View.INVISIBLE);	
		signupButton.setVisibility(View.INVISIBLE);
		durationLong = Toast.LENGTH_LONG;
		durationShort = Toast.LENGTH_SHORT;
	}

	@Override
	protected void onResume() {
		usernameText.setText("");
		passwordText.setText("");
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}
	
	
	public void createNewAccount(View v)
	{
		
		createNewAccountButton.setVisibility(View.INVISIBLE);
		signupButton.setVisibility(View.VISIBLE);
		eMailText.setVisibility(View.VISIBLE);
		confirmPasswordText.setVisibility(View.VISIBLE);
	}
	
	
	public void Signup (View v)
	{
		if(confirmPasswordText.getText().toString().equals(passwordText.getText().toString()))
	    	{
				progressDialog = ProgressDialog.show(LoginActivity.this, "", "Signing up ...");
				ParseUser user = new ParseUser();
				username=usernameText.getText().toString();
				user.setUsername(username);
				user.setPassword(passwordText.getText().toString());
				user.setEmail(eMailText.getText().toString());
				user.signUpInBackground(new SignUpCallback() { 
						@Override
							public void done(com.parse.ParseException e) {
								  
									if (e==null) {
									setSecurity();
									Toast toast = Toast.makeText(context,username + " has signed in" , durationLong);
									toast.show();
									progressDialog.dismiss();
									finish();
									startActivity( new Intent( context , MainActivity.class ) );
									}
									else{
			    	
										progressDialog.dismiss();
										Toast toast = Toast.makeText(context,"Signup failed", durationLong);
										toast.show();
									    }
									}	
								});
	    }
		else
		{
			Toast toast = Toast.makeText(context,"Please confirm your password",durationLong);
			toast.show();
			
		}
		
	}
	
	public void setSecurity()
	{
		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
	
		// Optionally enable public read access while disabling public write access.
	    defaultACL.setPublicReadAccess(true);
	    defaultACL.setPublicWriteAccess(false);
	    ParseACL.setDefaultACL(defaultACL, true);
	}
	
	public void Login(View v)
	{
		progressDialog = ProgressDialog.show(LoginActivity.this, "", "Logging in ...");
		ParseUser.logInInBackground(usernameText.getText().toString(),passwordText.getText().toString(), new LogInCallback() {
			@Override
			public void done(ParseUser currentUser, com.parse.ParseException e) {
				if(currentUser != null){
					
					Toast toast = Toast.makeText(context,currentUser.getUsername() + " has logged in" , durationLong);
					toast.show();
					progressDialog.dismiss();
					finish();
			    	startActivity( new Intent( context , MainActivity.class ) );	
				}
				else
				{
					Toast toast = Toast.makeText(context,"Login failed" , durationLong);
					progressDialog.dismiss();
					toast.show();
				}
			}
			});
	}
	
	public void parseInit(Context context , Intent intent)
	{
		Parse.initialize(context, "jUcJa3W8SL4QvxBpA4mI4cVb1JvNnYoMvLGjmauq", "eZrEsi8Bak3tSgvV0ceuiyO9PRbdtuTqfX7Qk4fr");
		ParseAnalytics.trackAppOpened(intent);
		this.context=context;
	}

	
	
	
	


}

