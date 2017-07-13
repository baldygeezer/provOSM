package provOSM;

import org.openprovenance.prov.model.*;

import java.util.ArrayList;
import java.util.Comparator;

import org.openprovenance.prov.interop.*;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.xml.*;

public class ProvWriter {

    // probably shouldn't use this...
    private static final String OSM_NAMESPACE = "https://www.openstreetmap.org/";

    private final ProvFactory provFactory;
    private final Namespace mNamespace;
    private static final String OSMPREFIX = "OSM";
    private static final String WAYPREFIX = "WAY";
    private static final String USRPREFIX = "USR";
    private static final String OSMns = "OSM";

    public ProvWriter(OSM_Primitive[] OSM_Entities) {
        mNamespace = new Namespace();
        mNamespace.addKnownNamespaces();
        mNamespace.register("OSM", "htttp://openstreetmap.org/elements#");


        provFactory = InteropFramework.newXMLProvFactory();

        for (OSM_Primitive OSM_Entity : OSM_Entities) {


            switch (OSM_Entity.getType()) {

                case WAY:


                    Entity original = provFactory.newEntity(getQname(OSM_Entity.getId()));


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


    private QualifiedName getQname(String name) {
        return mNamespace.qualifiedName("OSM", name, provFactory);
    }

    private void createEntity() {


    }

    private void create_agent() {

    }

}
