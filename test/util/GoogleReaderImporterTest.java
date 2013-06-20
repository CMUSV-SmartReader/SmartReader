package util;


import org.junit.Before;
import org.junit.Test;


public class GoogleReaderImporterTest {

    @Before
    public void setUp() {
        ReaderDB.setUp();
    }
    
    @Test
    public void testImportingUsingAccountAndPassword() {
        GoogleReaderImporter.importFromGoogle("seanlionheart@gmail.com",
                "314159265358979");
    }

}
