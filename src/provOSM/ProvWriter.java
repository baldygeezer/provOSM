package provOSM;

import org.openprovenance.prov.model.*;

import java.util.ArrayList;
import java.util.Comparator;

import org.openprovenance.prov.interop.*;

public class ProvWriter {

	// probably shouldn't use this...
	private static final String OSM_NAMESPACE = "https://www.openstreetmap.org/";

	private final ProvFactory provFactory;

	public ProvWriter(OSM_Primitive[] entities) {

		provFactory = InteropFramework.newXMLProvFactory();

		for (int i = 0; i <= entities.length; i++) {

		}

		switch (entities[1].getType()) {

		case WAY:
			// Entity way=provFactory.

			break;

		case NODE:
			System.out.println("We are not handling nodes yet");
			break;

		case RELATION:
			System.out.println("We are not handling relations yet yet");
			break;

		case CHANGESET:
			System.out.println("Stopit already! I can't cope, give me ways!");
			break;
		}
	}

	/***
	 * returns an array of arrays: each array in the list is a sorted list of each
	 * version of a specific way this is horrible...
	 * 
	 * @param pList
	 * @return OSMPrimitive[][]
	 */
	protected OSM_Primitive[][] getVersions(OSM_Primitive[] pList) {
		ArrayList<OSM_Primitive[]> versionsList = new ArrayList<OSM_Primitive[]>();// the array of arrays!
		ArrayList<OSM_Primitive> vList = new ArrayList<OSM_Primitive>();// a list to store the versions of each way
		String pId = pList[0].getId();// stores the id of an item, starting with the first...
		OSMDataType type = pList[0].getType();// ...Store the type to as OSM primitives can have duplicate IDs for
												// different types

		for (OSM_Primitive p : pList) { // for every item...
		//	int pVersion = 1;

			if (p.getId().equals(pId) && p.getType().equals(type)) {// ...if the item id and type are the same values as the ones we have stored...
				
				vList.add(p); // ...add the item to a list
			} else { // if not...
				// we store the old list
				vList.sort(Comparator.comparing(OSM_Primitive::getVersion));// prepare the list by sorting by version
				OSM_Primitive[] v = vList.toArray(new OSM_Primitive[vList.size()]); // convert to primitive array
				versionsList.add(v);// add it to the final arraylist
				// then make a new one
				pId = p.getId(); // store the new id
				vList = new ArrayList<OSM_Primitive>();// make a new list
				vList.add(p);// store the item that has a different id or type in the new list
				// update the stored type and id values with the new ones ready to go round the loop again
				pId = p.getId();
				type = p.getType();

			}

		}

		OSM_Primitive[][] versions = versionsList.toArray(new OSM_Primitive[versionsList.size()][]);
		return versions;
	}

}
