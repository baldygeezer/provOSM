package provOSM;

import org.junit.*;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by baldy on 15/08/17.
 */
public class GraphWriterTest {

    private static ArrayList<String[]> tags = new ArrayList<String[]>();
    private static String[] nodes = {"gegg5", "dsfs"};
    private static OSM_Primitive[] way1 = {
            new OSM_Way("2345", "26636", "345", "wibble", "anytime", "1", tags, nodes),
            new OSM_Way("2345", "26636", "345", "wibble", "anytime", "2", tags, nodes),
            new OSM_Way("2345", "26636", "2356", "wibble", "anytime", "3", tags, nodes),
    };
    private static OSM_Primitive[] way2 = {

            new OSM_Way("2323", "26636", "345", "wibble", "anytime", "1", tags, nodes),
            new OSM_Way("2323", "26636", "345", "wibble", "anytime", "2", tags, nodes),
    };
    private static OSM_Primitive[] way3 = {
            new OSM_Way("232345", "26636", "345", "wibble", "anytime", "1", tags, nodes),
            new OSM_Way("232345", "26636", "345", "wibble", "anytime", "2", tags, nodes),
            new OSM_Way("232345", "26636", "345", "wibble", "anytime", "3", tags, nodes),
            new OSM_Way("232345", "26636", "345", "wibble", "anytime", "4", tags, nodes)};

    private static OSM_Primitive[] way4 = {
            new OSM_Way("232345", "2636", "345", "wibble", "anytime", "1", tags, nodes),
            new OSM_Way("232345", "26636", "345", "wibble", "anytime", "2", tags, nodes),
            new OSM_Way("232345", "2663", "3457", "wibble", "anytime", "3", tags, nodes),
            new OSM_Way("232345", "6636", "345", "wibble", "anytime", "4", tags, nodes),
            new OSM_Way("232345", "266", "3457", "wibble", "anytime", "5", tags, nodes),
            new OSM_Way("232345", "266369", "3458", "wibble", "anytime", "6", tags, nodes)};

    private static OSM_Primitive[][] waylist = {way1, way2, way3};
    private  static GraphWriter graphWriter;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {


        String[] s = {"wibble", "woo"};
        String[] y = {"created_by", "woo"};
        tags.add(s);
        tags.add(y);
        graphWriter=new GraphWriter(new OSM_Extractor("testfixture.osm"));
    }



    @Before
    public void setUp() throws Exception {
        graphWriter=new GraphWriter(new OSM_Extractor("testfixture.osm"));




    }

    @After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void createVectorCreatesGraphWithCCorrectNumNodes() throws Exception {

        //graphWriter.create_feature(way4, "wibble");

        assertTrue("poopy", graphWriter.getVector(way4,"wibble")[18]==2.0d);
    }
}