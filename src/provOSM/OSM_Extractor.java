package provOSM;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import javax.xml.XMLConstants;
import javax.xml.stream.*;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class OSM_Extractor {
    private int ctr;
    private XMLInputFactory mXMLInputFactory;
    private XMLEventReader mXMLEventReader;
    private String mInputFile;
    private String mOutPutFile;
    protected final String OSMDATAPATH = "data/";

    /***
     * Extracts data from an OSH history file. Should work on OSM files
     * too, but will not be very efficient way of parsing them as it will
     * still deal with versions even if there is only one
     * The methods make the assumption that the elmements are all ordered by id
     * which currently seems to be the case for OSH files from Geofabrik, OSM and Osmium output
     *
     * @param mInputFile

     */
    public OSM_Extractor(String mInputFile) {

        ctr = 0;
        try {

            this.mXMLInputFactory = XMLInputFactory.newFactory();
            this.mXMLEventReader = mXMLInputFactory.createXMLEventReader(new FileReader(OSMDATAPATH + mInputFile));
            this.mInputFile = mInputFile;
            // this.mOutPutFile = mOutPutFile;

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XMLStreamException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /***
     *
     * Returns an array of arrays containing each version of  primitive elements having a specific tag
     * @param tag
     */
    public OSM_Way[][] getVersionedElements(String tag) {
        OSM_Way[][] vList;
        ArrayList<OSM_Way> wayList = extractByTag(tag);

        return getVersions(wayList);
    }

    /***
     *
     * Returns an array of arrays containing each version of  primitive elements having a specific tag
     *
     */
    public OSM_Way[][] getVersionedElements() {
        OSM_Way[][] vList;
        ArrayList<OSM_Way> wayList = extractAll();

        return getVersions(wayList);
    }


    /***
     * @// TODO: 18/07/2017 pull these methods togther into on method to get the versioned list form an OSM file! 
     */


    /**
     * method to return an ArrayList of OSMWay objects
     *
     * @return ArrayList<OSM_Way>
     */
    public ArrayList<OSM_Way> extractWays() {
        Map<String, String> attributeKeysValues = new HashMap<>(); // store  the  Way's  attributes
        // Map<String, String> tags = new HashMap<String, String>();// store the
        // Tag's attributes
        ArrayList<String[]> tags = new ArrayList<>();
        ArrayList<String> nodeList = new ArrayList<>();// store the member
        // node IDs
        ArrayList<OSM_Way> wayList = new ArrayList<>();
        boolean insideWayElement = false;// make a boolean to track whether the
        // parser is inside a Way element
        boolean flag = false;//boolean to flag the element as a target feature
        int waysFound = 0;
        try {

            while (mXMLEventReader.hasNext()) {// keep pulling events from the
                // parser and while there is one
                // to pull
                XMLEvent event = mXMLEventReader.nextEvent();

                // ....check if it is a start element (opening tag)
                if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {

                    StartElement startElement = event.asStartElement();
                    String qName = startElement.getName().toString();

                    // if the element is a Way we need to get its attributes and
                    // process it
                    if (qName.equals("way")) {
                        waysFound++;
                        // make a new empty insance of the hashmap to store the
                        // attributes
                        attributeKeysValues = new HashMap<>();
                        // make a new list of node ids
                        nodeList = new ArrayList<>();
                        // make a new HashMap for tags
                        tags = new ArrayList<>();
                        // set inside way to true so we know we are processing a
                        // way element
                        insideWayElement = true;

                        // get each attribute (for each attribute in the
                        // collection returned by the
                        // start element...
                        for (Iterator<Attribute> attributes = startElement.getAttributes(); attributes.hasNext(); ) {
                            Attribute a = attributes.next();// grab it into
                            // 'a'...
                            // ...then store it in the map
                            attributeKeysValues.put(a.getName().toString(), a.getValue());
                            ctr++;
                        }

                    }
                    // if the element is not an opening tag then check if it is
                    // a member node,

                    else if (qName.equals("nd") && insideWayElement) {
                        for (Iterator<Attribute> NodeAttributes = startElement.getAttributes(); NodeAttributes
                                .hasNext(); ) {
                            Attribute nd = NodeAttributes.next();

                            nodeList.add(nd.getValue());
                        } // end for

                    }
                    // if the element is not a member node then it should be a
                    // tag
                    // because of vagaries of OSM data model we will still check
                    else if (qName.equals("tag") && insideWayElement) {
                        int i = 1;
                        String[] tag = new String[2];
                        // get each attribute
                        for (Iterator<Attribute> NodeAttributes = startElement.getAttributes(); NodeAttributes
                                .hasNext(); ) {
                            Attribute tg = NodeAttributes.next();

                            if (i % 2 == 0) {
                                tag[0] = tg.getName().toString() + " = " + tg.getValue();

                                flag = tag[0].contains("fixme") || tag[1].contains("fixme") || tag[0].contains("FIXME") || tag[1].contains("FIXME")? true : flag;//set whether or not there is a target feature
                            } else {
                                tag[1] = tg.getName().toString() + " = " + tg.getValue();

                                i++;
                                tags.add(tag);

                            }

                            // flag = ;//set whether or not there is a target feature
                            // tags.add(tg.getName().toString() + " = "+
                            // tg.getValue());
                        } // end for

                    }

                }
                // if we reached the end of a way element
                else if (event.getEventType() == XMLStreamConstants.END_ELEMENT
                        && event.asEndElement().getName().toString().equals("way")) {
                    String id = attributeKeysValues.get("id");
                    String changeSet = attributeKeysValues.get("changeset");
                    String uid = attributeKeysValues.get("uid");
                    String userName = attributeKeysValues.get("user");
                    String timeStamp = attributeKeysValues.get("timestamp");
                    String version = attributeKeysValues.get("version");

                    String[] nodes = new String[nodeList.size()];
                    nodeList.toArray(nodes);

                    OSM_Way way = new OSM_Way(id, changeSet, uid, userName, timeStamp, version, tags, nodes);
                    way.setFlag(flag);

                    wayList.add(way);
                    insideWayElement = false;// set this to false to stop adding
                    // tags to the collection
                }

            }

        } catch (XMLStreamException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // System.out.println(nodeList.toArray()[2]);
       // System.out.println("Way versions found: " + waysFound);
        return wayList;

    }// end of method

    /**
     * Method to return all the elements with a specific tag
     *
     * @return ArrayList<OSM_Way>
     */

    public ArrayList<OSM_Way> extractByTag(String tagKey) {
        ArrayList<OSM_Way> taglist = new ArrayList<>();
        ArrayList<OSM_Way> inputList = extractWays();

        for (OSM_Way w : inputList) {
            for (String[] wl : w.getTags()) {
                if (wl[0].equals(tagKey)) {
                    taglist.add(w);
                }
            }
        }

        return taglist;
    }

    /**
     * @return
     */
    public ArrayList<OSM_Way> extractAll() {

        return extractWays();

    }


    /***
     * returns an array of arrays: each array in the list is a sorted list of each
     * version of a specific way this is horrible...
     * should this be in the OSM_extractor?
     * @param pList
     * @return OSMPrimitive[][]
     */
    protected OSM_Way[][] getVersions(ArrayList<OSM_Way> pList) {
        ArrayList<OSM_Way[]> versionsList = new ArrayList<>();// the array of arrays!
        ArrayList<OSM_Way> vList = new ArrayList<>();// a list to store the versions of each way
        String pId = pList.get(0).getId();// stores the id of an item, starting with the first...
        OSMDataType type = pList.get(0).getType();// ...Store the type to as OSM primitives can have duplicate IDs for different types
        int ctr = 0;

        for (OSM_Way p : pList) { // for every item...
            ctr++;
            if (p.getId().equals(pId) && p.getType().equals(type)) {// ...if the item id and type are the same values as the ones we have stored...

                vList.add(p); // ...add the item to a list
                if (ctr == pList.size()) {
                    prepareList(vList, versionsList);
                }

            } else { // if not...
                // we store the old list
                vList.sort(Comparator.comparing(OSM_Primitive::getVersion));// prepare the list by sorting by version
                OSM_Way[] v = vList.toArray(new OSM_Way[vList.size()]); // convert to primitive array
                versionsList.add(v);// add it to the final arraylist
                // then make a new one
                pId = p.getId(); // store the new id
                vList = new ArrayList<OSM_Way>();// make a new list
                vList.add(p);// store the item that has a different id or type in the new list
                // update the stored type and id values with the new ones ready to go round the loop again
                pId = p.getId();
                type = p.getType();

            }

        }

        OSM_Way[][] versions = versionsList.toArray(new OSM_Way[versionsList.size()][]);
      //  System.out.println("processed " +versions.length + " ways");
        return versions;
    }

    /***
     * method to store a list of the different versions of an element
     * @param vList the list that need sorting and storing
     * @param versionsList the list used for storage
     * @return
     */
    private void prepareList(ArrayList<OSM_Way> vList, ArrayList<OSM_Way[]> versionsList) {
        vList.sort(Comparator.comparing(OSM_Primitive::getVersion));// prepare the list by sorting by version
        OSM_Way[] v = vList.toArray(new OSM_Way[vList.size()]); // convert to primitive array
        versionsList.add(v);// add it to the final arraylist
    }
}
