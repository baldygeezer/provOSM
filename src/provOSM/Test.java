package provOSM;

import org.openprovenance.prov.model.*;

import java.util.ArrayList;

import org.openprovenance.prov.interop.*;




public class Test {
	
private final ProvFactory mPFactory;
private final Namespace mNs;

	
	
	
	public Test(ProvFactory pFactory) {
        this.mPFactory = pFactory;
        mNs= new Namespace();
        mNs.addKnownNamespaces();
        mNs.register("osm", "ploppy");
        
        
	
	}
	
	
	
	

	public static void main(String[] args) {
		ArrayList<OSM_Way>ways; //=new ArrayList<OSM_Way>();
		FileCleaner ploppy=new FileCleaner("ptwdMiniTest.osm", "wibble");
		
		ways = ploppy.extractWays();
		for (OSM_Way w :ways) {
			System.out.println(w.toString());
		}

	}

}
