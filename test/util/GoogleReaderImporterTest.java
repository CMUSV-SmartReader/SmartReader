package util;


import org.junit.Before;
import org.junit.Test;


public class GoogleReaderImporterTest {

    @Before
    public void setUp() {
        MorphiaObject.setUp();
    }
    
    @Test
    public void testLogin() {
        GoogleReaderImporter.importFromGoogle("seanlionheart@gmail.com",
                "314159265358979");
    }

}
