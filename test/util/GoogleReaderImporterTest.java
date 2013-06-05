package util;



import org.junit.Before;
import org.junit.Test;


public class GoogleReaderImporterTest {

    @Before
    public void setUp() {
        MorphiaObject.setUp();
    }
    
    @Test
    public void testImport() {
    }
    
    @Test
    public void testOAuthImport() {
        GoogleReaderImporter.oAuthImportFromGoogle("seanlionheart@gmail.com", "ya29.AHES6ZQUYDS4vtQALl1b8suwkoLVVs8B4EVTMtxYQSTXBCvUpPz8xA");
    }
    
}
