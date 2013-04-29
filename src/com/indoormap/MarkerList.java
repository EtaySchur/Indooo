package com.indoormap;

import java.util.ArrayList;




import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;



public class MarkerList {
	private ArrayList<Marker>		markerList	= null;
	private static MarkerList		instance	= null;
	private Context					context;
	
	
	private MarkerList( Context con  )
	{
	this.context = con;
	this.markerList = new ArrayList<Marker>();
	//db = new ToDoDataBaseHandler( context );
	//toDoArray = db.getAllItems();
	}
	
	
	public static MarkerList getInstance( Context con )
	{
	if ( instance == null )
		{
		instance = new MarkerList( con  );
		}
	return instance;
	}
	
	public int getSize()
	{
		return markerList.size();
	}
	
	public Marker getMarker(int position)
	{
		return markerList.get(position);
	}
	
	public void delMarker(int position)
	{
		markerList.remove(position);
	}


	public ArrayList<Marker> getMarkerList() {
		return markerList;
	}


	public void setMarkerList(ArrayList<Marker> markerList) {
		this.markerList = markerList;
	}


	public static MarkerList getInstance() {
		return instance;
	}


	public static void setInstance(MarkerList instance) {
		MarkerList.instance = instance;
	}


	public Context getContext() {
		return context;
	}


	public void setContext(Context context) {
		this.context = context;
	}


	


	
	
	
	public void addMarker(Marker newMarker)
	{
		Log.v("Notes","add marker list");
		markerList.add(newMarker);
	}
	
	public static void resetSingleton()
	{
		instance=null;
	}
	
	
	
	

}
