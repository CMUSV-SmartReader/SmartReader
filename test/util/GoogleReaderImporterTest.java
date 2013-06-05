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

        FeedCategory feedCategory = null;
        for (Map<String, String> map : dataList) {
            
            if (!map.containsKey("xmlUrl")) {
                if (feedCategory != null) {
                    feedCategory.create();
                    user.feedCategories.add(feedCategory);
                }
                feedCategory = new FeedCategory();
                feedCategory.name = map.get("title");
                feedCategory.user = user;
                user.feedCategories.add(feedCategory);
            }
            else {
                Feed feed = new Feed();
                feed.htmlUrl = map.get("htmlUrl");
                feed.xmlUrl = map.get("xmlUrl");
                feed.title = map.get("title");
                feed.type = map.get("type");
                feed.create();
                UserFeed userFeed = new UserFeed();
                userFeed.user = user;
                userFeed.feed = feed;
                userFeed.create();
                feedCategory.feeds.add(feed);
            }
        }
        if (feedCategory != null) {
            feedCategory.create();
        }
        user.update();

        GoogleReaderImporter.importWithEmail(user, inputSource);

    }
    
}
