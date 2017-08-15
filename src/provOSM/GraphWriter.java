package provOSM;

import com.hendrix.erdos.graphs.SimpleDirectedGraph;
import com.hendrix.erdos.types.Edge;
import com.hendrix.erdos.types.Vertex;
import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.*;
import org.openprovenance.prov.model.Activity;
import org.openprovenance.prov.model.Agent;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.ObjectFactory;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.WasAssociatedWith;
import org.openprovenance.prov.model.WasAttributedTo;
import org.openprovenance.prov.model.WasDerivedFrom;
import org.openprovenance.prov.xml.*;
import com.hendrix.erdos.*;
import org.openrdf.query.algebra.Str;

import java.util.ArrayList;

/**
 * Created by baldy on 14/08/17.
 */
public class GraphWriter {

    // probably shouldn't use this...
    private static final String OSM_NAMESPACE = "https://www.openstreetmap.org/";

    private final ProvFactory provFactory;
    private final Namespace mNamespace;
    private static final String OSMPREFIX = "OSM";
    private static final String WAYPREFIX = "WAY";
    private static final String USRPREFIX = "USR";
    private static final String CHANGESET = "CHANGESET";
    private static final String OSMns = "OSM";
    private Document mDocument;
    //private ArrayList<Agent> mAgents;
    //private ArrayList<SoftwareAgent> mSWAgents;
    private ArrayList<Activity> mActivities;
    private boolean documentCreated;
    private final org.openprovenance.prov.xml.ObjectFactory oprov;
    private OSM_Extractor mOSM_Extractor;

    public GraphWriter(OSM_Extractor osm_extractor) {
        //we still need the PROV Docs namespaces
        mNamespace = new Namespace();
        mNamespace.addKnownNamespaces();
        mNamespace.register(OSMPREFIX, "htttp://openstreetmap.org/");
        mNamespace.register(WAYPREFIX, "http://wiki.openstreetmap.org/wiki/Way");
        mNamespace.register(USRPREFIX, "http://wiki.openstreetmap.org/wiki/user");
        mNamespace.register(CHANGESET, "http://wiki.openstreetmap.org/wiki/changeset");
        provFactory = InteropFramework.newXMLProvFactory();
        mDocument = provFactory.newDocument();
        documentCreated = false;
        //mAgents = new ArrayList<>();

        //this factory is from the xml framework; can't use the interop factory for generating some prov; Why?
        oprov = new org.openprovenance.prov.xml.ObjectFactory();

       /// mSWAgents = new ArrayList<>();
        mActivities = new ArrayList<>();
        mOSM_Extractor = osm_extractor;
    }
    private QualifiedName getQname(String name, String prefix) {
        return mNamespace.qualifiedName(prefix, name, provFactory);
    }

    /***
     *
     * @param versions an array of OSM_Primitive ordered by version
     * @param prefix unused - if we need to visualise we will use it to make PROV labels
     * @return an int[] containing graph metrics
     */
    protected int[] create_feature(OSM_Primitive[] versions, String prefix) {
        SimpleDirectedGraph graph = new SimpleDirectedGraph();//the subgraph
        int[] feature = new int[3];//the list to return
        ArrayList<String> agents = new ArrayList<>();

        Vertex lastVertex;//the vertextes for verision drivation
        Vertex thisVertex;

for(OSM_Primitive p:versions){
    String agentTag=p.getUid();

    //loop to make the derivations
    if (p.getVersion()==1){//make node for the original if we are on version 1
        graph.addVertex(new Vertex(p.getId()+"_original"));
    }
    if (p.getVersion()>1){//if the version is >1 then it isn't the orginal and needs a derivation
        String lastVertexTag=p.getId()+"_v"+(p.getVersion()-1);
        String thisVertexTag=p.getId()+"_v"+p.getVersion();
        lastVertex=new Vertex(lastVertexTag);
        thisVertex=new Vertex(thisVertexTag);
        graph.addVertex(thisVertex);
        graph.addEdge(thisVertex,lastVertex);
        graph.getEdge(thisVertex,lastVertex).setTag("wasDerivedFrom");


    }

    if(!agents.contains(agentTag)){
     agents.add(agentTag);
    }




    Vertex v = new Vertex();



}

        return feature;
    }



    /**
     * @param p OSM_Primitive
     */
    private void getActivities(OSM_Primitive p) {
        Activity activity;
        boolean activityExists = false;

        for (Activity a : mActivities) {
            if (a.getId().getLocalPart().equals(p.getChangeSet())) {
                activityExists = true;
            }
        }

        if (!activityExists) {
            activity = provFactory.newActivity(getQname(p.getChangeSet(), CHANGESET), "Map Edit");
            mActivities.add(activity);
        }


    }


    private String cleanForProvN_QName(String s) {
        return getTagValue(s).replace(" ", "");
    }

    private String getTagValue(String tag) {


        return tag.substring(tag.indexOf("=") + 2);


    }


}











