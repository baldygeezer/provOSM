package provOSM;

import java.util.ArrayList;
import java.util.Map;

public class OSM_Way extends OSM_Primitive {
	private String[] mNodes;

	public OSM_Way(String id, String changeSet, String uid, String userName, String timeStamp, String version, ArrayList<String[]> tags, String[] nodes) {
		super(OSMDataType.WAY,id, changeSet, uid, userName, timeStamp, version, tags);
		this.mNodes=nodes;
	}

	public String[] getmNodes() {
		return mNodes;
	}

	@Override
	public String toString() {
		
		return super.toString();
	}

	
	
	
	

}
