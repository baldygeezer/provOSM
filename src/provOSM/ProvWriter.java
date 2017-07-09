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
			

			switch (entities[1].getmType()) {

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
	
	
	private OSM_Primitive[] getVersions(OSM_Primitive[] pList) {
		ArrayList<OSM_Primitive> vList=new ArrayList<OSM_Primitive>();
		
		for (OSM_Primitive p : pList) {
		if (p.getmType().equals(OSMDataType.WAY))	{
			vList.add(p);
		}
		}
		
		
		vList.sort(Comparator.comparing(OSM_Primitive::getVersion));
		OSM_Primitive[] versions=vList.toArray(new OSM_Primitive[vList.size()]);
		return versions;
	}
	
	
	
	

}
