package provOSM;

import org.junit.*;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by baldy on 16/07/17.
 */
public class ProvWriterTest {
    private static ArrayList<String[]> tags = new ArrayList<String[]>();
    private static String[] nodes = {"gegg5", "dsfs"};
    private static OSM_Primitive[] way1 = {
            new OSM_Way("2345", "26636", "345", "wibble", "anytime", "1", tags, nodes),
            new OSM_Way("2345", "26636", "345", "wibble", "anytime", "2", tags, nodes),
            new OSM_Way("2345", "26636", "345", "wibble", "anytime", "3", tags, nodes),
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

    private static OSM_Primitive[][] waylist = {way1, way2, way3};
    private  static ProvWriter provWriter;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {


        String[] s = {"wibble", "woo"};
        String[] y = {"created by", "woo"};
        tags.add(s);
        tags.add(y);
        provWriter=new ProvWriter(waylist);
    }

    @Before
    public void setUp() throws Exception {




    }

    @After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void create_bundle() throws Exception {

        provWriter.create_bundle(way1, "OSM");
    }

}