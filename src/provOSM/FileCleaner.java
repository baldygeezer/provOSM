package provOSM;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.stream.*;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class FileCleaner {
	private int ctr;
	private XMLInputFactory mXMLInputFactory;
	private XMLEventReader mXMLEventReader;
	private String mInputFile;
	private String mOutPutFile;
	private final String OSMDATAPATH = "/home/baldy/Documents/OSMData/";

	public FileCleaner(String mInputFile, String mOutPutFile) {

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
	 * at the moment this class strips out buildings i.e. way elements tagged as
	 * building
	 * 
	 * @return boolean
	 */
	public boolean cleanFile() {

		try {

			while (mXMLEventReader.hasNext()) {
				XMLEvent event = mXMLEventReader.nextEvent();

				switch (event.getEventType()) {

				case XMLStreamConstants.START_ELEMENT:

					StartElement startElement = event.asStartElement();
					String qName = startElement.getName().toString();

					// if the element is a Way we need to get its attributes
					if (qName.equals("way")) {
						// make a map to store the attributes
						Map<String, String> attributeKeysValues = new HashMap<String, String>();

						// get each attribute (for each attribute in the collection returned by the start element...
						for (Iterator<Attribute> attributes = startElement.getAttributes(); attributes.hasNext();) {
							Attribute a = attributes.next();//grab it into 'a'...
							// ...then store it in the map
							attributeKeysValues.put(a.getName().toString(), a.getValue());
							ctr++;
						}

						// System.out.println(attr);

					}
					
					else if (true) {

					}

					break;

				}

			}

		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(ctr);
		return true;

	}

}
