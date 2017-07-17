package provOSM;

import org.openprovenance.prov.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.openprovenance.prov.interop.*;
import org.openprovenance.prov.model.Agent;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.QualifiedName;
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
    private static final String OSMns = "OSM";
    private Document mDocument;
    private ArrayList<Agent> mAgents;
    private ArrayList<SoftwareAgent> mSWAgents;
    ObjectFactory oprov;

    public ProvWriter(OSM_Primitive[][] OSM_Entities) {
        mNamespace = new Namespace();
        mNamespace.addKnownNamespaces();
        mNamespace.register("OSM", "htttp://openstreetmap.org/elements#");
        provFactory = InteropFramework.newXMLProvFactory();
        mDocument = provFactory.newDocument();
        mAgents = new ArrayList<>();
        oprov = new ObjectFactory();
        mSWAgents = new ArrayList<>();

        for (OSM_Primitive OSM_Entity[] : OSM_Entities) {


            switch (OSM_Entity[0].getType()) {

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
            }
        }

    }


    private QualifiedName getQname(String name, String prefix) {
        return mNamespace.qualifiedName("OSM", name, provFactory);
    }

    private void createEntities(OSM_Primitive[] entities) {


    }

    protected void create_bundle(OSM_Primitive[] versions, String prefix) {
        Entity original = new org.openprovenance.prov.xml.Entity();//the original osm primitive
        Entity[] derivatives = new Entity[versions.length - 1];//list of subsequent versions
        ArrayList<StatementOrBundle> statements = new ArrayList<>();
        int i;
        // for every item in the versioned list (i had a reason for wanting a counter but can't remember...
        for (i = 0; i <= versions.length - 1; i++) {


            //having made an agent we need to attribute the primitive to it
            // WasAttributedTo madeBy = provFactory.newWasAttributedTo(null, versions[i].agent.getId(), )

            //if this is the original...
            if (versions[i].getVersion() == 1) {//store it as original
                original = provFactory.newEntity(getQname(versions[i].getId(), prefix), "way");//need to handle labels better
                statements.add(original);
                //add software agents here
            } else {//if it is a later version
                Entity entity = provFactory.newEntity(getQname(versions[i].getId(), prefix), "way");//store it as local var
                derivatives[versions[i].getVersion() - 2] = entity;//put it into an index in the array that correspond to its version (v2 goes in index 0)
                statements.add(entity);
            }

            //create a software agent for the primitive
            getSoftwareAgent(versions[i]);
            //create an agent for the primitive
            getAgents(versions[i], OSMPREFIX);

            //   mDocument.getStatementOrBundle().
        }
        // create the derivedFrom relations
        for (Entity e : derivatives) {//for each later version
            int i2 = 0;
            if (i2 == 0) {//if we are on the second go round the loop the 1st item (v2) is derived from original
                WasDerivedFrom der = provFactory.newWasDerivedFrom(derivatives[0].getId(), original.getId());
                statements.add(der);
                i2++;
            } else {//if not then it is derived from the item before
                WasDerivedFrom der = provFactory.newWasDerivedFrom(derivatives[i2].getId(), derivatives[i2 - 1].getId());
                statements.add(der);
            }
        }


        // todo create attributions to Agents

        //todo create attibution to SW agents




    }


    /**
     * if no agent has already been created for the software used to create the primitive this method will
     * create one and add it to a list stored as a member field
     *
     * @param p OSM_Primitive
     * @return void
     */
    private void getAgents(OSM_Primitive p, String prefix) {
        Agent agent;
        boolean agentExists = false;// boolean set to true if an agent for this user has already been created

        for (Agent a : mAgents) {
            //search the list of agents for one that has the same user id as the primitive. set agentExists to true if we find it
            agentExists = a.getId().getLocalPart().equals(p.getId()) ? true : false;
        }

        //if the agent doesn't already exist
        if (!agentExists) {//create it
            agent = provFactory.newAgent(getQname(p.getId(), prefix), p.getUserName());
            mAgents.add(agent);//add it to the list

        }
    }


    /**
     * if no Software agent has already been created for the software used to create the primitive this method will
     * create one and add it to a list stored as a member field
     *
     * @param primitive OSM_Primitive
     * @return void
     */
    private void getSoftwareAgent(OSM_Primitive primitive) {
        SoftwareAgent agent;

        boolean SWAgentExists = false;//boolean set to true if we find an agent on the list
        for (SoftwareAgent a : mSWAgents) { //search the list and if we find a corresponding id
            if (a.getId().getLocalPart() == primitive.getId()) SWAgentExists = true; //set it to true
        }

        if (primitive.getTags() != null) { //if the primitive has tags
            for (String[] tag : primitive.getTags()) {//for every tag...

                if (tag[0] == "created by" && !SWAgentExists) { //if it is a 'created by' tag and no software agent has already been created for it
//                    Agent sw=provFactory.newAgent(new SoftwareAgent());
//                    agent = provFactory.newAgent(getQname(tag[1], OSMPREFIX));
                    agent = oprov.createSoftwareAgent();//create the agent
                    agent.setId(getQname(tag[1], OSMPREFIX));//assign it a qname
                    mSWAgents.add(agent);//add it to the list

                }
            }
        }


    }


}
