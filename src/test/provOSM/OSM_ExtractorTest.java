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
    private static String[] nodes = {"gegg5", "dsfs"};
    private static OSM_Primitive[] ways = {
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

    ArrayList<OSM_Primitive> waylist=new ArrayList<OSM_Primitive>(Arrays.asList(ways));

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        tags = new ArrayList<String[]>();
        String[] s = {"wibble", "woo"};
        String[] y = {"wibble", "woo"};
        tags.add(s);
        tags.add(y);
        extractor = new OSM_Extractor("ptwdMiniTest.osm");

    }

    @org.junit.Test
    public void testGetVersionsReturnsFourSets() throws Exception {
        OSM_Primitive[][] resultList = extractor.getVersions(waylist);

        assertTrue("there should be 4 arrays in the list, representing two ways. we saw " + resultList.length + " arrays.", resultList.length == 4);
    }

    @org.junit.Test
    public void testGetVersionsReturnsListsWithCorrectIDs() throws Exception {
        OSM_Primitive[][] resultList = extractor.getVersions(waylist);
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


}