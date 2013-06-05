package util;


import models.User;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;


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
    

    @Test
    public void testOPMLParser() throws Exception {
        InputSource inputSource = new InputSource(getClass().getResourceAsStream("/resources/Sean-subscriptions.xml"));
        User user = User.findByEmail("seanlionheart@gmail.com");
        if (user == null) {
            user = new User();
            user.email = "seanlionheart@gmail.com";
            user.create();
        }

        GoogleReaderImporter.importWithEmail(user, inputSource);

    }
    
}
