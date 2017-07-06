package provOSM;

import java.util.Map;

public abstract class OSM_Primitive {
	private OSMDataType mType;
	private String mChangeSet;
	private String mUid;
	private String mUserName;
	private String mTimeStamp;
	private String mVersion;
	private Map<String, String> mTags;

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

	public OSM_Primitive(OSMDataType type, String changeSet, String uid, String userName, String timeStamp, String version, Map<String, String> tags) {
		
		this.mType = type;
		this.mChangeSet = changeSet;
		this.mUid = uid;
		this.mUserName = userName;
		this.mTimeStamp = timeStamp;
		this.mVersion = version;
		this.mTags=tags;
	}

}
