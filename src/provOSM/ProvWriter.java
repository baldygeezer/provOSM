package provOSM;

import org.openprovenance.prov.interop.InteropFramework.ProvFormat;
import org.openprovenance.prov.model.*;

import java.util.ArrayList;

import org.openprovenance.prov.interop.*;
import org.openprovenance.prov.model.Activity;
import org.openprovenance.prov.model.Agent;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.WasAssociatedWith;
import org.openprovenance.prov.model.WasAttributedTo;
import org.openprovenance.prov.model.WasDerivedFrom;
import org.openprovenance.prov.xml.*;
import org.openprovenance.prov.xml.ObjectFactory;


public class ProvWriter {

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
    private ArrayList<Agent> mAgents;
    private ArrayList<SoftwareAgent> mSWAgents;
    private ArrayList<Activity> mActivities;
    private boolean documentCreated;
    private final ObjectFactory oprov;
    private OSM_Extractor mOSM_Extractor;

    public ProvWriter(OSM_Extractor osm_extractor) {
        mNamespace = new Namespace();
        mNamespace.addKnownNamespaces();
        mNamespace.register(OSMPREFIX, "htttp://openstreetmap.org/");
        mNamespace.register(WAYPREFIX, "http://wiki.openstreetmap.org/wiki/Way");
        mNamespace.register(USRPREFIX, "http://wiki.openstreetmap.org/wiki/user");
        mNamespace.register(CHANGESET,"http://wiki.openstreetmap.org/wiki/changeset");



        provFactory = InteropFramework.newXMLProvFactory();
        mDocument = provFactory.newDocument();
        documentCreated = false;
        mAgents = new ArrayList<>();

        //this factory is from the xml framework; can't use the interop factory for generating some prov; Why?
        oprov = new ObjectFactory();

        mSWAgents = new ArrayList<>();
        mActivities = new ArrayList<>();
        mOSM_Extractor = osm_extractor;

        //   for (OSM_Primitive OSM_Entity[] : OSM_Entities) {

//'cos we thought we might be looking at other primitive types...
        //we will eventually so leave it for now
/*            switch (OSM_Entity[0].getType()) {

                case WAY:

                    //       Entity original = provFactory.newEntity(getQname(OSM_Entity.getId()));


                    break;

                case NODE:
                    System.out.println("We are not handling nodes yet");
                    break;

                case RELATION:
                    System.out.println("We are not handling relations yet");
                    break;

                case CHANGESET:
                    System.out.println("Stopit already! I can't cope, give me ways!");
                    break;
            }*/
        //  }

    }


    public void getDocument() {
        for (OSM_Primitive[] v : mOSM_Extractor.getVersionedElements("k = building")) {
            mDocument.getStatementOrBundle().addAll(create_bundle(v, OSMPREFIX));
        }

        mDocument.getStatementOrBundle().addAll(getAgentBundle());
        mDocument.getStatementOrBundle().addAll(getActivityBundle());

        mDocument.setNamespace(mNamespace);
        documentCreated = true;

    }


    public void printDocumentToScreen() {

        if (documentCreated) {
            // System.out.println(mDocument.toString());
            convertToProvN(mDocument, "data/out/");

        } else {


            getDocument();
            //System.out.println(mDocument.toString());
            convertToProvN(mDocument, "data/out/");
        }
    }


    public void convertToProvN(Document document, String file) {
        InteropFramework interOp = new InteropFramework();
        //interOp.writeDocument(file,ProvFormat.PNG, document);
        interOp.writeDocument(System.out, ProvFormat.PROVN, document);

    }


    private QualifiedName getQname(String name, String prefix) {
        return mNamespace.qualifiedName(prefix, name, provFactory);
    }

    private void createEntities(OSM_Primitive[] entities) {


    }

    protected ArrayList<StatementOrBundle> create_bundle(OSM_Primitive[] versions, String prefix) {
        Entity original = new org.openprovenance.prov.xml.Entity();//the original osm primitive
        Entity[] derivatives = new Entity[versions.length - 1];//list of subsequent versions
        ArrayList<StatementOrBundle> statements = new ArrayList<>();
        int i;


        //Start main for loop: make an entity for each version *****************

        // for every item in the versioned list (i had a reason for wanting a counter but can't remember...
        for (i = 0; i <= versions.length - 1; i++) {


            //having made an agent we need to attribute the primitive to it
            //WasAttributedTo madeBy = provFactory.newWasAttributedTo(null, versions[i].agent.getId(), )

            //if this is the original...
            if (versions[i].getVersion() == 1) {//store it as original
                original = provFactory.newEntity(getQname(versions[i].getId()+"V"+versions[i].getVersion(), WAYPREFIX), "way");//need to handle labels better
                statements.add(original);
                //add software agents here
            } else {//if it is a later version
                Entity entity = provFactory.newEntity(getQname(versions[i].getId()+"V"+versions[i].getVersion(), WAYPREFIX), "way");//store it as local var
                derivatives[versions[i].getVersion() - 2] = entity;//put it into an index in the array that correspond to its version (v2 goes in index 0)

                statements.add(entity);
            }


            //create an agent for the primitive
            getAgents(versions[i], USRPREFIX);
            // create an edit session for the primitive
            getActivities(versions[i]);


            //create a software agent for the primitive. associate it with an edit (activity
            //if getSoftwareAgent returns true then there is a software agent for the primitive, either because it
            // created one or because it found a pre-existing one, in which case we need to assocate them

            if (getSoftwareAgent(versions[i])) {

                for (Activity a : mActivities) {
                    //if the activity has the changsetId that matches the chnageset attribute of the primitive
                    if (a.getId().getLocalPart() == versions[i].getChangeSet()) {
                        //associate them; we don't pull from the list of agents as this may slow things down, quicker to get the qualified name from the primitive
                        WasAssociatedWith softwareEditAssoc = provFactory.newWasAssociatedWith(null, a.getId(), getQname(versions[i].getUid(), USRPREFIX));
                        statements.add(softwareEditAssoc);
                    }
                }
            }

            //associate the user agent with the edit activity, using info we already have rather than more loping over arraylists...
            WasAssociatedWith agentEditAssoc = provFactory.newWasAssociatedWith(null, getQname(versions[i].getChangeSet(), CHANGESET), getQname(versions[i].getUid(), USRPREFIX));
            statements.add(agentEditAssoc);

        } //************** end main for loop


        // create the derivedFrom relations
        int i2 = 0;
        for (Entity e : derivatives) {//for each later version

            if (i2 == 0) {//if we are on the second go round the loop the 1st item (v2) is derived from original
                WasDerivedFrom der = provFactory.newWasDerivedFrom(derivatives[0].getId(), original.getId());
                statements.add(der);
                i2++;
            } else {//if not then it is derived from the item before
                WasDerivedFrom der = provFactory.newWasDerivedFrom(derivatives[i2].getId(), derivatives[i2 - 1].getId());
                statements.add(der);
            }
        }


        //  create attributions to Agents
        //for every primitive find its agent in the list and create the attribution

        for (OSM_Primitive p : versions) {
            Agent creator = null;
            for (Agent a : mAgents) {
                if (a.getId().getLocalPart() == p.getUid()) {
                    creator = a;
                    WasAttributedTo madeBy = provFactory.newWasAttributedTo(null, getQname(p.getId(), WAYPREFIX), creator.getId());
                    statements.add(madeBy);
                }
            }

        }

        return statements;
    }


    private ArrayList<StatementOrBundle> getAgentBundle() {
        ArrayList<StatementOrBundle> statements = new ArrayList<>();
        //add agents
        for (Agent a : mAgents) {
            statements.add(a);
        }
        //add softwareagents

        for (SoftwareAgent sw : mSWAgents) {
            statements.add(sw);
        }

        return statements;
    }


    private ArrayList<StatementOrBundle> getActivityBundle() {
        ArrayList<StatementOrBundle> statements = new ArrayList<>();
        for (Activity a : mActivities) {
            statements.add(a);
        }
        return statements;

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


    /**
     * if no agent has already been created for the software used to create the primitive this method will
     * create one and add it to a list stored as a member field
     *
     * @param p      OSM_Primitive
     * @param prefix String
     * @return void
     */
    private void getAgents(OSM_Primitive p, String prefix) {
        Agent agent;
        boolean agentExists = false;// boolean set to true if an agent for this user has already been created

        for (Agent a : mAgents) {
            //search the list of agents for one that has the same user id as the primitive. set agentExists to true if we find it
            agentExists = a.getId().getLocalPart().equals(p.getUid()) ? true : false;
        }

        //if the agent doesn't already exist
        if (!agentExists) {//create it
            agent = provFactory.newAgent(getQname(p.getUid(), prefix), p.getUserName());
            mAgents.add(agent);//add it to the list

        }
    }


    /**
     * if no Software agent has already been created for the software used to create the primitive this method will
     * create one and add it to a list stored as a member field. returns true is an agent was
     *
     * @param primitive OSM_Primitive
     * @return boolean
     */
    private boolean getSoftwareAgent(OSM_Primitive primitive) {
        SoftwareAgent agent;
        boolean agentCreated = false;

        boolean SWAgentExists = false;//boolean set to true if we find an agent on the list
        for (SoftwareAgent a : mSWAgents) { //search the list and if we find a corresponding id
            if (a.getId().getLocalPart() == primitive.getId()) {
                SWAgentExists = true; //set it to true
                agentCreated = true;// }
            }
        }

        if (primitive.getTags() != null) { //if the primitive has tags
            for (String[] tag : primitive.getTags()) {//for every tag...

                if (tag[0].equals("k = created_by") && !SWAgentExists) { //if it is a 'created by' tag and no software agent has already been created for it
//                    Agent sw=provFactory.newAgent(new SoftwareAgent());
//                    agent = provFactory.newAgent(getQname(tag[1], OSMPREFIX));
                    agent = oprov.createSoftwareAgent();//create the agent

                    String MrPloppy = cleanForProvN_QName(tag[1]);

                    agent.setId(getQname(cleanForProvN_QName(tag[1]), OSMPREFIX));//assign it a qname
                    mSWAgents.add(agent);//add it to the list
                    agentCreated = true;
                }
            }
        }

        return agentCreated;
    }


    private String cleanForProvN_QName(String s) {
        return getTagValue(s).replace(" ", "");
    }

    private String getTagValue(String tag) {


        return tag.substring(tag.indexOf("=") + 2);


    }


}
