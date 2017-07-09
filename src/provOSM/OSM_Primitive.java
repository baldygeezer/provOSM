package provOSM;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public abstract class OSM_Primitive {
	private OSMDataType mType;
	private String mChangeSet;
	private String mUid;
	private String mUserName;
	private String mTimeStamp;
	private String mVersion;
	private ArrayList<String[]> mTags;

	public OSMDataType getmType() {
		return mType;
	}

	public String getmChangeSet() {
		return mChangeSet;
	}

	public String getmUid() {
		return mUid;
	}

	public String getmUserName() {
		return mUserName;
	}

	public String getmTimeStamp() {
		return mTimeStamp;
	}

	public String getVersion() {
		return mVersion;
	}

	public OSM_Primitive(OSMDataType type, String changeSet, String uid, String userName, String timeStamp, String version, ArrayList<String[]> tags) {
		
		this.mType = type;
		this.mChangeSet = changeSet;
		this.mUid = uid;
		this.mUserName = userName;
		this.mTimeStamp = timeStamp;
		this.mVersion = version;
		this.mTags=tags;
	}

	@Override
	public String toString() {
		String wayString = "Username: "+ mUserName +", User ID: "+ mUid + ", Changeset: " + mChangeSet 
				+ ", Timestamp: " + mTimeStamp + ", Version: " + mVersion + tagsToString();
		
		
		
		return wayString;
	}
	
	
	private String tagsToString(){
		String tagString = " \n Tags \n ************************ \n";
		
		for (String[] s : mTags){
			
			tagString += "<tag "+ s[0] +  " " + s[1] + " /> ";
			
	//		s += " ";
	//		tagString += s;
			
			
		}
		
		tagString+="\n";
		return tagString;
	}
	
}
