package provOSM;

import org.junit.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by baldy on 12/07/17.
 */
public class OSM_ExtractorTest {
    private static ArrayList<String[]> tags;
    private static OSM_Extractor extractor;
    private static OSM_Extractor extractorFixtureFile;
    private static String[] nodes = {"gegg5", "dsfs"};
    private static OSM_Way[] ways = {
            new OSM_Way("2345", "26636", "345", "wibble", "anytime", "1", tags, nodes),
            new OSM_Way("2345", "26636", "345", "wibble", "anytime", "3", tags, nodes),
            new OSM_Way("2345", "26636", "345", "wibble", "anytime", "2", tags, nodes),
            new OSM_Way("2323", "26636", "345", "wibble", "anytime", "1", tags, nodes),
            new OSM_Way("2323", "26636", "345", "wibble", "anytime", "2", tags, nodes),
            new OSM_Way("2321", "26636", "345", "wibble", "anytime", "1", tags, nodes),
            new OSM_Way("232345", "26636", "345", "wibble", "anytime", "1", tags, nodes),
            new OSM_Way("232345", "26636", "345", "wibble", "anytime", "2", tags, nodes),
            new OSM_Way("232345", "26636", "345", "wibble", "anytime", "1", tags, nodes),
            new OSM_Way("232345", "26636", "345", "wibble", "anytime", "2", tags, nodes)};

    ArrayList<OSM_Way> waylist = new ArrayList<OSM_Way>(Arrays.asList(ways));

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        tags = new ArrayList<String[]>();
        String[] s = {"wibble", "woo"};
        String[] y = {"wibble", "woo"};
        tags.add(s);
        tags.add(y);
        extractor = new OSM_Extractor("ptwdMiniTest.osm");
        // extractorFixtureFile = new OSM_Extractor("testfixture.osm");
    }

    @org.junit.Test
    public void testGetVersionsReturnsFourSets() throws Exception {
        OSM_Way[][] resultList = extractor.getVersions(waylist);

        assertTrue("there should be 4 arrays in the list, representing two ways. we saw " + resultList.length + " arrays.", resultList.length == 4);
    }

    @org.junit.Test
    public void testGetVersionsReturnsListsWithCorrectIDs() throws Exception {
        OSM_Way[][] resultList = extractor.getVersions(waylist);
        boolean firstIdOK = true;
        boolean secondIdOK = true;

        String whichValue = "";
        for (OSM_Primitive result : resultList[0]) {
            if (!result.getId().equals("2345")) {
                firstIdOK = false;

            }
        }
        for (OSM_Primitive result : resultList[1]) {
            if (!result.getId().equals("2323")) {
                secondIdOK = false;

            }
            if (firstIdOK && !secondIdOK) {
                whichValue = "The second value is";
            } else if (!firstIdOK && secondIdOK) {
                whichValue = "The first value is";
            } else whichValue = "Both values are";
        }
        assertTrue(whichValue + " not what we expected.", firstIdOK && secondIdOK);
    }

    @org.junit.Test
    public void extractWays() throws Exception {
    }

    @Test
    public void extractByTag() throws Exception {
    }

    @Test
    public void getVersionedElementReturnsCorrectNumberOfversionLists() {
        extractorFixtureFile = new OSM_Extractor("testfixture.osm");

        OSM_Way[][] list = extractorFixtureFile.getVersionedElements("k = building");
        assertTrue("we were expecting an array with 3 elements. We saw " + list.length, list.length == 3);

    }

    @Test
    public void getVersionedElementReturnsCorrectElements() {
        extractorFixtureFile = new OSM_Extractor("testfixture.osm");
        OSM_Way[][] list = extractorFixtureFile.getVersionedElements("k = building");
        boolean resultOK = false;
        resultOK = list[0][0].getId().equals("160668937") && list[1][0].getId().equals("162141721") && list[2][0].getId().equals("162141722");

        assertTrue("we saw the element ID's " + list[0][0].getId() + " + " + list[1][0].getId() + " + " + list[2][0].getId(), resultOK);

    }


    @Test
    public void getVersionedElementReturnsCorrectSizedElements() {

        extractorFixtureFile = new OSM_Extractor("testfixture.osm");
        OSM_Way[][] list = extractorFixtureFile.getVersionedElements("k = building");

        boolean resultOK = false;
        resultOK = list[0].length == 3 && list[1].length == 3 && list[2].length == 2;
        assertTrue("we were expecting element 0 to have length 3, element 1 to have length 3 ,element 2 to have length 2. We saw " + list[0].length + " for element 0, " + list[1].length + " for element 1, " + list[2].length + " for element 2", resultOK);

    }


}