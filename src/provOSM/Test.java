package provOSM;

import org.openprovenance.prov.model.*;

import java.util.ArrayList;

import org.openprovenance.prov.interop.*;


public class Test {

    private final ProvFactory mPFactory;
    private final Namespace mNs;


    public Test(ProvFactory pFactory) {
        this.mPFactory = pFactory;
        mNs = new Namespace();
        mNs.addKnownNamespaces();
        mNs.register("osm", "ploppy");


    }


    public static void main(String[] args) {
//		ArrayList<OSM_Way>ways; //=new ArrayList<OSM_Way>();
//		OSM_Extractor ploppy=new OSM_Extractor("ptwdMiniTest.osm");
//
//		ways = ploppy.extractWays();
//		for (OSM_Way w :ways) {
//			System.out.println(w.toString());
//		}
//

        GraphWriter graphWriter = new GraphWriter(new OSM_Extractor("testfixture.osm"));
        ArrayList<double[]> results = graphWriter.buildVectorList();
for (double[] d:results){
    String s ="";
    for (double a :d) {
       String rstr=", "+a;

        s+=rstr;
    }
    System.out.println(s);
}
        //ProvWriter provWriter=new ProvWriter(new OSM_Extractor("testfixture.osm"));
        //provWriter.printDocumentToScreen();

    }

}
