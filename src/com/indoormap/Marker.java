package com.indoormap;

import android.annotation.SuppressLint;

@SuppressLint("NewApi")
public class Marker  {
	private int id ;
	private String title;
	private float xCord,yCord;
	//private LayoutInflater	l_Inflater;
	private String objectId;
	private String qrCode;
	private boolean isWriteable;
	
	public int getId() {
		return id;
	}
	public Marker(int id , String title ,  float xCord , float yCord  , String objectID,String qrCode)
	{
	
		this.id=id;
		this.title=title;
		this.xCord=xCord;
		this.yCord=yCord;
		this.objectId=objectID;
		this.qrCode=qrCode;
		this.isWriteable=true;
		
       // this.markerID.setText(id);
     //   this.setImageDrawable(imageMarker);
       /* this.markerImage.setX(xCord);
        this.markerImage.setY(yCord);*/
    

		
	}
	public boolean isWriteable() {
		return isWriteable;
	}
	public void setWriteable(boolean isWriteable) {
		this.isWriteable = isWriteable;
	}
	public String getQrCode() {
		return qrCode;
	}
	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public float getxCord() {
		return xCord;
	}
	public void setxCord(float xCord) {
		this.xCord = xCord;
	}
	public float getyCord() {
		return yCord;
	}
	public void setyCord(float yCord) {
		this.yCord = yCord;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
