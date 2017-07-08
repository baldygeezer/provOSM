package provOSM;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
	private final String OSMDATAPATH = "data/";

	public OSM_Extractor(String mInputFile, String mOutPutFile) {

		ctr = 0;
		try {
            
			this.mXMLInputFactory = XMLInputFactory.newFactory();
			this.mXMLEventReader = mXMLInputFactory.createXMLEventReader(new FileReader(OSMDATAPATH + mInputFile));
			this.mInputFile = mInputFile;
			this.mOutPutFile = mOutPutFile;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * method to return an ArrayList of OSMWay objects
	 * 
	 * @return ArrayList<OSM_Way>
	 */
	public ArrayList<OSM_Way> extractWays() {
		Map<String, String> attributeKeysValues = new HashMap<String, String>(); // store the Way's attributes
		//Map<String, String> tags = new HashMap<String, String>();// store the Tag's attributes
		ArrayList<String> tags =new ArrayList<String>();
		ArrayList<String> nodeList = new ArrayList<String>();// store the member node IDs
		ArrayList<OSM_Way> wayList = new ArrayList<OSM_Way>();
		boolean insideWayElement = false;//make a boolean to track whether the parser is inside a Way element
		

		try {

			while (mXMLEventReader.hasNext()) {// keep pulling events from the parser and while there is one
												// to pull
				XMLEvent event = mXMLEventReader.nextEvent();

				// ....check if it is a start element (opening tag)
				if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {

					StartElement startElement = event.asStartElement();
					String qName = startElement.getName().toString();

					// if the element is a Way we need to get its attributes and process it
					if (qName.equals("way")) {
						// make a new empty insance of the hashmap to store the attributes
						attributeKeysValues = new HashMap<String, String>();
						// make a new list of node ids
						nodeList = new ArrayList<String>();
						// make a new HashMap for tags
						 tags = new ArrayList<String>();
						 // set inside way to true so we know we are processing a way element
						 insideWayElement = true;

						// get each attribute (for each attribute in the collection returned by the
						// start element...
						for (Iterator<Attribute> attributes = startElement.getAttributes(); attributes.hasNext();) {
							Attribute a = attributes.next();// grab it into 'a'...
							// ...then store it in the map
							attributeKeysValues.put(a.getName().toString(), a.getValue());
							ctr++;
						}

					}
					// if the element is not an opening tag then check if it is a member node,

					else if (qName.equals("nd")) {
						for (Iterator<Attribute> NodeAttributes = startElement.getAttributes(); NodeAttributes
								.hasNext();) {
							Attribute nd = NodeAttributes.next();

							nodeList.add(nd.getValue());
						} // end for

					}
					// if the element is not a member node then it should be a tag
					// because of vagaries of OSM data model we will still check
					else if (qName.equals("tag") && insideWayElement) {
						// get each attribute
						for (Iterator<Attribute> NodeAttributes = startElement.getAttributes(); NodeAttributes
								.hasNext();) {
							Attribute tg = NodeAttributes.next();

							tags.add(tg.getName().toString() + " = "+ tg.getValue());
						} // end for

					}

				}
				// if we reached the end of a way element
				else if (event.getEventType() == XMLStreamConstants.END_ELEMENT
						&& event.asEndElement().getName().toString().equals("way")) {
					String changeSet = attributeKeysValues.get("changeset");
					String uid = attributeKeysValues.get("uid");
					String userName = attributeKeysValues.get("user");
					String timeStamp = attributeKeysValues.get("timestamp");
					String version = attributeKeysValues.get("version");

					String[] nodes = new String[nodeList.size()];
					nodeList.toArray(nodes);

					OSM_Way way = new OSM_Way(changeSet, uid, userName, timeStamp, version, tags, nodes);
					wayList.add(way);
					insideWayElement = false;
				}

			}

		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(nodeList.toArray()[2]);

		return wayList;

	}// end of method

	
	
	
	
	
	
	
	
	
	
	

}
