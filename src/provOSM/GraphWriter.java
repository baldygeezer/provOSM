package provOSM;

import com.hendrix.erdos.algorithms.*;
import com.hendrix.erdos.graphs.*;
import com.hendrix.erdos.types.Edge;
import com.hendrix.erdos.types.IVertex;
import com.hendrix.erdos.types.Vertex;
import com.hendrix.erdos.utils.SVertexUtils;
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
import java.util.Collections;

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


    // public runAnalysis(){
    //   forea
    //}


    /***
     *
     * @param versions an array of OSM_Primitive ordered by version
     * @param prefix unused - if we need to visualise we will use it to make PROV labels
     * @return an int[] containing graph metrics
     */
    protected double[] create_feature(OSM_Primitive[] versions, String prefix) {
        SimpleDirectedGraph graph = new SimpleDirectedGraph();//the subgraph

        ArrayList<String> agents = new ArrayList<>();
        ArrayList<String> edits = new ArrayList<>();
        ArrayList<String> swAgents = new ArrayList<>();
        Vertex lastVertex = null;// the last vertex as the destination for the wasDerivedFrom edge
        for (OSM_Primitive p : versions) {


            //loops to make the derivations
            if (p.getVersion() == 1) {//make node for the original if we are on version 1
                Vertex originalVertex = new Vertex(p.getId() + "entity_original");
                graph.addVertex(originalVertex);
                //addUserAgent(graph, originalVertex, agents, p);
                //addSoftwareAgent(graph, originalVertex, agents, p);
                addEditSession(graph, originalVertex, addUserAgent(graph, originalVertex, agents, p), edits, swAgents, p);

                lastVertex = originalVertex;//store the current vertex as the last one ready for next time round

            }
            if (p.getVersion() == 2) { //if the version is 2 then it isn't the original and needs a wasDerivedFrom edge pointing at the original
                String thisVertexTag = "entity_" + p.getId() + "_v" + p.getVersion();
                Vertex thisVertex = new Vertex(thisVertexTag);
                graph.addVertex(thisVertex);
                graph.addEdge(thisVertex, lastVertex);//create the edge
                graph.getEdge(thisVertex, lastVertex).setTag("wasDerivedFrom");//tag it with the prov relation
                // addUserAgent(graph, thisVertex, agents, p);
                // addSoftwareAgent(graph, thisVertex, agents, p);
                addEditSession(graph, thisVertex, addUserAgent(graph, thisVertex, agents, p), edits, swAgents, p);

                lastVertex = thisVertex;//store the current vertex as the last one ready for next time round NOTE to self: DO THIS LAST!!

            }

            if (p.getVersion() > 2) {//if the version >2 then we just create derivations form the last verion
                String lastVertexTag = "entity_" + p.getId() + "_v" + (p.getVersion() - 1);
                String thisVertexTag = "entity_" + p.getId() + "_v" + p.getVersion();
                Vertex thisVertex = new Vertex(thisVertexTag);
                graph.addVertex(thisVertex);
                graph.addEdge(thisVertex, lastVertex);
                graph.getEdge(thisVertex, lastVertex).setTag("wasDerivedFrom");
                // addUserAgent(graph, thisVertex, agents, p);//create an assign a user agent
                //addSoftwareAgent(graph, thisVertex, agents, p);
                addEditSession(graph, thisVertex, addUserAgent(graph, thisVertex, agents, p), edits, swAgents, p);
                lastVertex = thisVertex;

            }


        }
        graph.print();
        maxFiniteDistance(graph);
        return analise(graph);


    }


    private double[] analise(SimpleDirectedGraph graph) {
        double[] features = {0, 0, 0};

        AllPairsShortPathResult res1 = new Johnson(graph).applyAlgorithm();
        Vertex original = null;
        for (IVertex v : graph.vertices()) {
            if (v.getTag().contains("orig")) {
                original = (Vertex) v;
            }
        }

        ShortestPathsTree res = new BellmanFordShortestPath(graph).setStartVertex(original).applyAlgorithm();

        //  res.print();


        features[1] = res.edges().size() / graph.edges().size();

        //features[2]=res1.
        features[2] = 2.2;
        System.out.println(features[0]);
        System.out.println(features[1]);
        maxFiniteDistance(graph);
        return features;
    }


    private double averageAgentInDegree(SimpleDirectedGraph graph) {


        double totAgentInDegree = 0;
        int ctr = 0;

        for (IVertex v : graph.vertices()) {
            if (v.getTag().contains("agent")) {

                totAgentInDegree += graph.inDegreeOfVertex(v);
                ctr++;
            }
        }
        ctr = ctr == 0 ? 1 : ctr;
        return totAgentInDegree / ctr;

    }

    /***
     * Convert a Directed graph into an undirected graph. Returns a new graph object cretad with the data of the parameter
     * @param dg SimpleDirectedGraph
     * @return SimpleGraph
     */
    private SimpleGraph graphConvert(SimpleDirectedGraph dg) {

        SimpleGraph sg = new SimpleGraph();

        for (IVertex v : dg.vertices()) {//for every vertex in the old graph

            Vertex nv = new Vertex(v.getTag());//make a new one with the same tag
            sg.addVertex(nv);//add it to the new graph

        }

        for (Edge e : dg.edges()) {//for each edge in the old graph
            Vertex SV = getVertexByTag(sg, e.getV1().getTag());//get the corresponding edges in the new one
            Vertex EV = getVertexByTag(sg, e.getV2().getTag());

            Edge ne = new Edge(SV, EV, Edge.EDGE_DIRECTION.UNDIRECTED);
            sg.addEdge(ne);
        }


        return sg;

    }

    /***
     * gets a vertex from a graph using its tag
     * @param graph
     * @param tag
     * @return
     */
    private Vertex getVertexByTag(IGraph graph, String tag) {
        Vertex nv = null;

        for (IVertex v : graph.vertices()) {

            if (v.getTag().contains(tag)) {
                nv = (Vertex) v;
            }
        }
        return nv;
    }


    private void maxFiniteDistance(SimpleDirectedGraph graph) {


        ArrayList<Float> finres = new ArrayList<>();
        ArrayList<Float> res = new ArrayList<>();
        String[] nTypes = {"agent", "swAgent", "activity", "entity"};
        double[] mfd = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int ctr = 0;
        SimpleGraph g = graphConvert(graph);


        for (String s : nTypes) {//for every string in the list of node types, use it as a source node s

            for (String d : nTypes) {//for every string(2) in the list use it as a destinatin node d


                if (s != d) {//if they are not the same node type tag


                    for (IVertex vs : g.vertices()) {//look at each vertex
                        if (vs.getTag().contains(s)) {//is it is a source node
                            ShortestPathsTree result = new DijkstraShortestPath(g).setStartVertex(vs).applyAlgorithm();//use it as the satrt not for the shortest path tree

                            for (IVertex vd : graph.vertices()) {//then look a tag containing our non-matching string
                                if (vd.getTag().contains(d)) { //we found one if it is our non matching destination
                                    res.add(result.distanceOf(vd));//add its distance from the source to the list

                                }
                            }


                        }
                    }

                    float min = 0;

                    for (float f : res) {
                        min = f < min ? f : min;
                    }
                    finres.add(min);
                    res = new ArrayList<>();
                }


            }


        }


        g.print();

        System.out.println("poopy");
        for (float i : finres) {

            System.out.println(i);
        }


    }

    //  private void diameter(ShortestPathsTree)


    /**
     * cretaes and assigns a useragent
     *
     * @param graph
     * @param thisVertex
     * @param agents
     * @param p
     * @return
     */
    private Vertex addUserAgent(SimpleDirectedGraph graph, Vertex thisVertex, ArrayList<String> agents, OSM_Primitive p) {
        Vertex agentVertex = null;

        if (!agents.contains(p.getUid())) { //if we haven't already created an agent(no string stored on the list)
            agentVertex = new Vertex("agent_" + p.getUid());//create and agent vertex
            graph.addVertex(agentVertex);
            agents.add(p.getUid());//store the agent id so we know we already made one

            Edge edge = new Edge(thisVertex, agentVertex, Edge.EDGE_DIRECTION.DIRECTED);//make an edge
            edge.setTag("WasAttributedTo");//tag it
            graph.addEdge(edge);//add it to the graph
        } else {//else we made one
            for (IVertex v : graph.vertices()) {
                if (v.getTag().equals("agent_" + p.getUid())) {
                    agentVertex = (Vertex) v;
                    Edge edge = new Edge(thisVertex, agentVertex, Edge.EDGE_DIRECTION.DIRECTED);//make an edge
                    edge.setTag("WasAttributedTo");//tag it
                    graph.addEdge(edge);//add it to the graph
                }
            }
        }
        return agentVertex;
    }


    private void addEditSession(SimpleDirectedGraph graph, Vertex thisVertex, Vertex agentVertex, ArrayList<String> edits, ArrayList<String> swAgents, OSM_Primitive p) {
        if (!edits.contains(p.getChangeSet())) {//if there is no matching changesetId on the list we havent already made one
            Vertex editVertex = new Vertex("activity_" + p.getChangeSet());
            graph.addVertex(editVertex);
            edits.add(p.getChangeSet());
            Edge editEdge = new Edge(thisVertex, editVertex, Edge.EDGE_DIRECTION.DIRECTED);//the wasGeneratedby edge version to changeset
            Edge userEdge = new Edge(editVertex, agentVertex, Edge.EDGE_DIRECTION.DIRECTED);     //the wasassociateWith edge (to userAgent)
            graph.addEdge(editEdge);
            graph.addEdge(userEdge);
            addSoftwareAgent(graph, editVertex, swAgents, p);//

        } else {//if we already have this changeset in the graph and just need to getit and addeg an edge to it
            for (IVertex v : graph.vertices()) {//look though the list of vertices in the graph
                if (v.getTag().equals("activity_" + p.getChangeSet())) {
                    Edge editEdge = new Edge(thisVertex, v, Edge.EDGE_DIRECTION.DIRECTED);//the wasGeneratedby edge version to changeset
                    Edge userEdge = new Edge(v, agentVertex, Edge.EDGE_DIRECTION.DIRECTED);     //the wasassociateWith edge (to userAgent)
                    graph.addEdge(editEdge);
                    graph.addEdge(userEdge);
                    addSoftwareAgent(graph, (Vertex) v, swAgents, p);//now we have our activity we can associate it with a software agent
                }
            }
        }
    }


    /**
     * Creates and assigns a software agent
     *
     * @param graph
     * @param activityVertex
     * @param swAgents
     * @param p
     */
    private void addSoftwareAgent(SimpleDirectedGraph graph, Vertex activityVertex, ArrayList<String> swAgents, OSM_Primitive p) {
        for (String[] tag : p.getTags()) {//for each tag in the primitives set of tags
            if (tag[0].equals("created_by")) {//if the key is a 'created_by'...
                if (!swAgents.contains(tag[1])) {///check if we already made an agent by looking for it on a list of ones we made. if not...
                    Vertex swAgentVertex = new Vertex("swAgent_" + tag[1]);//then make one
                    graph.addVertex(swAgentVertex);// add it to the graph


                    graph.addEdge(activityVertex, swAgentVertex);//ad an edge representing WasAssociatedWith from the Activity (edit session to the software agent
                    swAgents.add(tag[1]);//add it to the list of ones we've made
                } else {//if we did make one already
                    for (IVertex v : graph.vertices()) {//look for it in the graph
                        if (v.getTag().equals("swAgent_" + tag[1])) {///if we find it
                            Vertex swAgentVertex = (Vertex) v;  //copy the reference for it
                            graph.addEdge(activityVertex, swAgentVertex);//add a wasAssociated edge to it from the edit activity
                        }
                    }
                }

            }
        }
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











