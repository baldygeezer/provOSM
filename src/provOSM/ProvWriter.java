package provOSM;

import org.openprovenance.prov.model.*;

import java.util.ArrayList;

import org.openprovenance.prov.interop.*;

public class ProvWriter {
	
	//probably shouldn't use this...
	private static final String OSM_NAMESPACE = "https://www.openstreetmap.org/";
	
	
	private final ProvFactory provFactory;
	
	
	
	
	
	
	
	
	
	
	
	

	public ProvWriter(OSMDataType type, OSM_Primitive[] entities) {
		
		provFactory=InteropFramework.newXMLProvFactory();
		
		
		switch (type){
		 
		 case WAY:
			 //@TODO code for ways
			//provFactory.
			 
			 
			 
			 break;
		 
		 case NODE:
			 System.out.println("We are not handling nodes yet");
			 
		
		 
		 
		 
		 
		 case RELATION:
			 System.out.println("We are not handling relations yet yet");
			 
		 case CHANGESET:
			 System.out.println("Stopit already! I can't cope, give me ways!");
			 
	}}

}
