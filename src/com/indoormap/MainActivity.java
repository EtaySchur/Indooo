package com.indoormap;


import java.util.List;





import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.indoormap.R.id;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import com.parse.SaveCallback;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	
	private ImageView map,tempMarker;
	private Context context;
	private static int markerCounter=0;
	private  MarkerList markerList;
	private EditText markerText;
	private TextView mainTextView;
	private Button doneButton,delButton,scanButton,createNewMarkerButton;
	private static float dpX;
	private static float dpY;
	private RelativeLayout relativeLayout;
	private int markerTag=-1;
	private LayoutParams params;
	private ParseManager pm;
	private IntentIntegrator integrator;
	private String qrCode;
	private boolean signIn;
	private String tempTitle;
	private ParseUser currentUser;
	private Runnable updater;
	private	UIUpdater uiUpdater;
	boolean activeLongPress = false;
	private String qrResult;
	
	   
	

	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//MarkerList.resetSingleton();
		setContentView(R.layout.activity_main);
		context=this;
		currentUser		= ParseUser.getCurrentUser();
		if(currentUser==null){
			startActivity( new Intent( context , LoginActivity.class ) );	
		}
		map 		    = (ImageView) findViewById(id.mapView1);
		tempMarker		= (ImageView) findViewById(id.markerGreen);
		markerList      = MarkerList.getInstance(context);
		markerText      = (EditText) findViewById(id.editText1);
		mainTextView    = (TextView) findViewById(id.main_text_view);
		//userText   	    = (TextView) findViewById(id.userText);
		doneButton      = (Button) findViewById(R.id.doneButton);
		delButton		= (Button) findViewById(R.id.del_button);
		scanButton    = (Button) findViewById(R.id.create_button1);
		createNewMarkerButton = (Button) findViewById(R.id.createNewMarkerButton);
		relativeLayout  = (RelativeLayout) findViewById(R.id.main_layout);
	    params = new LayoutParams (RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		delButton.setVisibility(View.INVISIBLE);
		doneButton.setVisibility(View.INVISIBLE);
		markerText.setVisibility(View.INVISIBLE);
		createNewMarkerButton.setVisibility(View.INVISIBLE);
		integrator = new IntentIntegrator(this);
		//userText.setText("User Name : " + ParseUser.getCurrentUser().getUsername());
		pm = new ParseManager(this,getIntent());
		markerText.setFocusable(true);
		if(markerList.getSize()==0){
			Log.d("Notes","Getting Markers from Parse");
		pm.getAllMarkers();
		}
		else
		{
			Log.d("Notes","Getting Markers from Singleton");
			drawExMarkers();
		}
		updater = new Runnable() {//added
			
			@Override
			public void run() {
				pm.getAllMarkers();
				Log.d("juv","runnung");
			}
		};
		uiUpdater = new UIUpdater(updater);
		uiUpdater.startUpdates();
			
		final GestureDetector gd = new GestureDetector(this,gdListener); 
		map.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) { gd.onTouchEvent(event);
			return true;
			} });
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null) {
		String result = scanResult.getContents();
		qrResult=result;
		if(markerTag>0)
		{
			RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(45,45);
			ImageView image = (ImageView) findViewById(markerTag);
			image.setLayoutParams(parms);
		}
		if(result !=null)
		{
			for(int i=0 ; i<markerList.getSize() ; i++)
			{
				if(result.equals(markerList.getMarker(i).getQrCode()))
				{
					
					drawTempMarker(markerList.getMarker(i).getxCord(),markerList.getMarker(i).getyCord());
					mainTextView.setText("Qr code found in data base , You are here!!!");
					markerText.setText(markerList.getMarker(i).getTitle());
					return;
				}
			}
		mainTextView.setText("QR code was not found in data base , please press on your map location");
		activeLongPress=true;
		scanButton.setVisibility(View.VISIBLE);
		markerText.setVisibility(View.INVISIBLE);
		return;
		}
		}
		finish();}
	
	

	
	public void drawExMarkers()
	{
		markerCounter=0;
		for(int i=0 ; i<markerList.getSize();i++)
		{
			System.out.println("Drawing Marker # " + (i+1));
			dpX=markerList.getMarker(i).getxCord();
			dpY=markerList.getMarker(i).getyCord();
			Log.d("Notes",String.valueOf(markerList.getMarker(i).getyCord()));
			markerCounter++;
			drawMarker(dpX,dpY);	
		}
	}
	
	
	public void delAllMarkerFromView()
	{
		while(markerList.getSize() > 0)
		{
			relativeLayout.removeView(findViewById(markerList.getMarker(0).getId()));
			markerList.delMarker(0); // delete object from the Singleton
	    }
		
		relativeLayout.refreshDrawableState();
		markerText.setText("");
		mainTextView.setText("");
		delButton.setVisibility(View.INVISIBLE);
		doneButton.setVisibility(View.INVISIBLE);
		markerText.setVisibility(View.INVISIBLE);
		
	}
	
	
	
	public void logOut(View v)
	{
		ParseUser.logOut();
		currentUser = ParseUser.getCurrentUser(); // this will now be null
		MarkerList.resetSingleton();
		startActivity( new Intent( context , LoginActivity.class ) );
	}
	
	// create new marker LongPress listener
	public void qrScan (View v)
	{	
		//tempTitle=markerText.getText().toString();
		integrator.initiateScan();
		
	}
	
	// Done edit marker Onclick listener 
	public void Done (View v)
	{
		for(int i=0 ; i<markerList.getSize() ; i++)
		{
			if( markerList.getMarker(i).getId() == markerTag)
			{				
				markerList.getMarker(i).setTitle(markerText.getText().toString());
				pm.updateMarker(markerList.getMarker(i).getObjectId(), markerText.getText().toString());
			}
		}
		mainTextView.setText("Marker has been edited");	
		delButton.setVisibility(View.INVISIBLE);
		doneButton.setVisibility(View.INVISIBLE);
		markerText.setVisibility(View.INVISIBLE);
		scanButton.setVisibility(View.VISIBLE);
		
	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		logOut(null);
	}

	public static float convertDpToPixel(float dp,Context context){
        float density = context.getResources().getDisplayMetrics().density;
    return dp * density;
}

	
	public static float convertPixelsToDp(float px,Context context){
        float density = context.getResources().getDisplayMetrics().density;
    return px / density;

}
	
	public void drawMarker(float xCord,float yCord)
	{
		relativeLayout.removeView(findViewById(0));
		Log.d("Notes","draw marker - counter = " + markerCounter);
		float newxCord=convertDpToPixel(xCord, context);
		float newYCord=convertDpToPixel(yCord, context);
		Log.d("probs",String.valueOf(newxCord));
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		ImageView markerImage = (ImageView) inflater.inflate(R.layout.marker, null);
		params.setMargins((int) newxCord , (int) newYCord, 0, 0);
		markerImage.setLayoutParams(params);
		markerImage.setX(newxCord-17);
		markerImage.setY(newYCord-45);
		markerImage.bringToFront();
		markerImage.setId(markerCounter);
		relativeLayout.addView(markerImage);
		relativeLayout.refreshDrawableState();
		markerImage.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				
				int index=0;
				int i;
				if(markerTag>0)
				{
					RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(40,40);
					ImageView image = (ImageView) findViewById(markerTag);
					image.setLayoutParams(parms);
				}
				RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(70,70);
				v.setLayoutParams(parms);
				markerText.setVisibility(View.VISIBLE);
				if(relativeLayout.findViewById(0) != null)
				{
					relativeLayout.removeView(findViewById(0));
				}
				// TODO Auto-generated method stub
				for(i=0 ; i<markerList.getSize() ; i++)
			    {
			    	if(  markerList.getMarker(i).getId() == v.getId())
			    	{	
			    		Log.d("Notes",String.valueOf(markerList.getMarker(i).getId()));
			    		Log.d("Notes","Write Access to this marker " + String.valueOf(markerList.getMarker(i).isWriteable()));
			    		markerText.setText(markerList.getMarker(i).getTitle());
			    		Log.d("probs",markerList.getMarker(i).getTitle());
			    		index=i;
			    	}
			    }
				
				scanButton.setVisibility(View.VISIBLE);
				markerTag=v.getId();
				Log.d("Notes","USER KEY " + ParseUser.getCurrentUser().getObjectId());
				
				if(markerList.getMarker(index).isWriteable())
				{
					markerText.setFocusableInTouchMode(true);
					mainTextView.setText("Edit Marker");
					delButton.setVisibility(View.VISIBLE);
					doneButton.setVisibility(View.VISIBLE);	
				}
				else
				{
					delButton.setVisibility(View.INVISIBLE);
					doneButton.setVisibility(View.INVISIBLE);
					mainTextView.setText("Marker description");
					markerText.setFocusable(false);
				}
				
				return false;
			}
		});
	}
	
	
	// Delete marker Onclick listener 
	public void delMarker(View v)
	{
		for(int i=0 ; i<markerList.getSize() ; i++)
		{
			if( markerList.getMarker(i).getId()== (markerTag))
			{
				pm.delMarker(markerList.getMarker(i).getObjectId()); // delete object from the cloud 
				markerList.delMarker(i); // delete object from the Singleton		
			}
	    }
		relativeLayout.removeView(findViewById(markerTag));
		relativeLayout.refreshDrawableState();
		markerText.setText("");
		mainTextView.setText("Delete Marker!");
		delButton.setVisibility(View.INVISIBLE);
		doneButton.setVisibility(View.INVISIBLE);
		markerText.setVisibility(View.INVISIBLE);
		scanButton.setVisibility(View.VISIBLE);
		markerTag=-1;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void createNewMarker(View v)
	{		
		markerCounter++;
		tempTitle=markerText.getText().toString();
		//float xCord = convertDpToPixel(dpX, context);
		//float yCord = convertDpToPixel(dpY, context);
		Log.d("probs","new marker created - x cord " + dpX);
		Marker newMarker = new Marker( markerCounter , tempTitle,  dpX, dpY , "new_item" , qrResult); 
		markerList.addMarker(newMarker);
		drawMarker(dpX,dpY);
		pm.uploadMarker(newMarker);
		markerText.setText("");
		mainTextView.setText("New marker added!");	
		activeLongPress=false;
		createNewMarkerButton.setVisibility(View.INVISIBLE);
		scanButton.setVisibility(View.VISIBLE);
		return;
	}
	
	GestureDetector.OnGestureListener gdListener = new GestureDetector.SimpleOnGestureListener(){
		@Override
		public boolean onSingleTapUp(MotionEvent e) { 
			scanButton.setVisibility(View.VISIBLE);   
			return false;
		}
		
		@Override
		public void onLongPress(MotionEvent e) {
		    if(!activeLongPress) 
		    {
		    	return;
		    }
		    if(markerTag>0)
			{
				RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(45,45);
				ImageView image = (ImageView) findViewById(markerTag);
				image.setLayoutParams(parms);
			}
			markerText.setVisibility(View.VISIBLE);
		    markerText.setFocusableInTouchMode(true);
		    scanButton.setVisibility(View.INVISIBLE);
		    createNewMarkerButton.setVisibility(View.VISIBLE);	
			delButton.setVisibility(View.INVISIBLE);
			doneButton.setVisibility(View.INVISIBLE);
			dpX = convertPixelsToDp(( map.getLeft() +e.getX()),context) ;
			dpY = convertPixelsToDp(( map.getTop() +e.getY()),context) ;      
			Log.d("probs",dpX + " " + dpY);
			markerText.setText("");
			mainTextView.setText("Enter marker's title");	
			if(relativeLayout.findViewById(0) != null)
			{
				relativeLayout.removeView(findViewById(0));
			}
			drawTempMarker(dpX,dpY);
		}
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
		float velocityY) { return false;
		}
		//There are more gestures. RTFM.
		};
		
		public void drawTempMarker(float xCordDP,float yCordDP)
		{
			
			Log.d("Notes","draw  temp marker");
			RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(55,55);
			float tempXCordPixel=convertDpToPixel(xCordDP, context);
			Log.d("probs","x Cord in DP of temp Marker " + String.valueOf(xCordDP));
			float tempYCordPixel=convertDpToPixel(yCordDP, context);
			Log.d("probs","DPX + DPY " + dpX + " " + dpY);
			LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
			tempMarker = (ImageView) inflater.inflate(R.layout.marker_green, null);
			params.setMargins((int) tempXCordPixel , (int) tempYCordPixel , 0, 0);
			tempMarker.setLayoutParams(parms);
			//tempMarker.setLayoutParams(params);
			tempMarker.setX(tempXCordPixel-27);
			tempMarker.setY(tempYCordPixel-55);
			tempMarker.bringToFront();
			tempMarker.setId(0);
			markerText.setFocusable(true);
			relativeLayout.addView(tempMarker);
			relativeLayout.refreshDrawableState();
		}
		
		
		// Inner Class to Manage all Parse functions
		public  class  ParseManager {
			Context context;
			public ParseManager(Context context , Intent intent) {
				//Parse.initialize(context, "jUcJa3W8SL4QvxBpA4mI4cVb1JvNnYoMvLGjmauq", "eZrEsi8Bak3tSgvV0ceuiyO9PRbdtuTqfX7Qk4fr");
				//ParseAnalytics.trackAppOpened(intent);
				//this.context=context;
			}
			
			
			//edit Marker to Parse
			public void updateMarker(String objectId,final String newTitle)
			{
				ParseQuery query = new ParseQuery("Marker");
				query.getInBackground(objectId, new GetCallback() {
				  public void done(ParseObject object, ParseException e) {
				    if (e == null) {
				     object.put("title", newTitle);
				     object.saveInBackground();
				     
				    } else {
				      Log.v("Notes","Update to sevrver failed");
				    }
				  }
				});
			}
			
			
			public void uploadMarker(final Marker marker)
			{
				
				final ParseObject parseMarker = new ParseObject("Marker");
				parseMarker.put("title", marker.getTitle());
				parseMarker.put("xCord", marker.getxCord());
				parseMarker.put("yCord", marker.getyCord());
				parseMarker.put("qrCode", marker.getQrCode());
				//parseMarker.setACL(new ParseACL(ParseUser.getCurrentUser()));
				parseMarker.saveInBackground(new SaveCallback(){

					@Override
					public void done(ParseException e) {
						if (e == null)
						{
							marker.setObjectId(parseMarker.getObjectId());
							Log.v("Notes",marker.getObjectId());
						}
						else
						{
							Log.v("Notes", "error save in backround");
						}
						
					}
					
				});
			}
			
			
			public void delMarker(String markerId)
			{
				ParseQuery query = new ParseQuery("Marker");
				query.getInBackground(markerId, new GetCallback() {
				  public void done(ParseObject object, ParseException e) {
				    if (e == null) {
				      object.deleteInBackground();
				      
				    } else {
				    		Log.v("Notes","Delete from Parse failed");
				    }
				  }
				});
			}
			public void getAllMarkers()
			{
			
				Log.d("Notes","getting markers from Parse");
				ParseQuery query = new ParseQuery("Marker");
				//markerList = MarkerList.getInstance(context);
				query.findInBackground(new FindCallback() {
				    public void done(List<ParseObject> markerParseList, ParseException e) {
				        if (e == null) {
				        	delAllMarkerFromView();
				            Log.d("Yeni", "Retrieved " + markerParseList.size() + " markers");
				        	String userId = ParseUser.getCurrentUser().getObjectId();
				            for(int i=0 ; i<markerParseList.size();i++)
				            {
				            	Log.d("Notes","getting Marker number " + (i+1));
				            	Marker newMarker = new Marker (i+1,markerParseList.get(i).getString("title"),markerParseList.get(i).getInt("xCord"),markerParseList.get(i).getInt("yCord"),markerParseList.get(i).getObjectId(),markerParseList.get(i).getString("qrCode"));
				            	newMarker.setWriteable(markerParseList.get(i).getACL().getWriteAccess(userId));
				            	Log.d("Notes",String.valueOf( markerParseList.get(i).getACL().getWriteAccess(userId)));
				            	markerList.addMarker(newMarker);
				            	
				            	
				            	
				            }
				        	System.out.println("new size after getting markers is " + markerList.getSize());
				            drawExMarkers();				       			      
				        } else {
				            Log.d("Notes", "Error: " + e.getMessage());
				          
				        }
				    }				
				});
			}
		}
}
