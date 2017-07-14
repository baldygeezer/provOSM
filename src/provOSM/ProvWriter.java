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
    private ArrayList<String> mAgentIDs;

    public ProvWriter(OSM_Primitive[] OSM_Entities) {
        mNamespace = new Namespace();
        mNamespace.addKnownNamespaces();
        mNamespace.register("OSM", "htttp://openstreetmap.org/elements#");
        provFactory = InteropFramework.newXMLProvFactory();
        mDocument = provFactory.newDocument();
        mAgentIDs = new ArrayList<String>();


        for (OSM_Primitive OSM_Entity : OSM_Entities) {


            switch (OSM_Entity.getType()) {

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

    private void create_bundle(OSM_Primitive[] versions, String prefix) {
        Entity original = new org.openprovenance.prov.xml.Entity();//the original osm primitive
        Entity[] derivatives = new Entity[versions.length - 1];//list of subsequent versions
        ArrayList<StatementOrBundle> statements = new ArrayList<>();
        // for every item in the versioned list (i had a reason for wanting a counter but can't remember...
        for (int i = 0; i <= versions.length; i++) {
            //if the list of existing agents doesn't contain this id, make an agent
            if (!mAgentIDs.contains(versions[i].getId())) {
                Agent agent = provFactory.newAgent(getQname(versions[i].getId(), prefix), versions[i].getUserName());
            }
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
            for (Entity e : derivatives) {//for each later version
                if (i == 1) {//if we are on the second go round the loop the 1st item (v2) is derived from original
                    WasDerivedFrom der = provFactory.newWasDerivedFrom(derivatives[0].getId(), original.getId());
                    statements.add(der);
                } else {//if not then it is derived from the item before
                    WasDerivedFrom der = provFactory.newWasDerivedFrom(derivatives[i].getId(), derivatives[i - 1].getId());
                    statements.add(der);
                }
            }

            //   mDocument.getStatementOrBundle().
        }


    }

    private void getSoftwareAgent(OSM_Primitive primitive) {

    }



}
