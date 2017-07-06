package provOSM;

import org.openprovenance.prov.model.*;
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
		
		FileCleaner ploppy=new FileCleaner("ptwdMiniTest.osm", "wibble");
		
		ploppy.cleanFile();
		// TODO Auto-generated method stub

	}

}
