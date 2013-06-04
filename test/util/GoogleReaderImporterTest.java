package util;

import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import models.Category;
import models.Feed;
import models.User;
import models.UserCategory;
import models.UserFeed;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;


public class GoogleReaderImporterTest {

    @Test
    public void testLogin() {
        GoogleReaderImporter.importFeeds("seanlionheart@gmail.com",
                "314159265358979");
    }

    @Test
    public void testOPMLParser() throws Exception {
        MorphiaObject.setUp();
        OPMLParser mRSSHandler = new OPMLParser();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        XMLReader xmlReader = parser.getXMLReader();
        xmlReader.setContentHandler(mRSSHandler);
        xmlReader.parse(new InputSource(getClass().getResourceAsStream("/resources/Sean-subscriptions.xml")));
//        xmlReader.parse(new InputSource(getClass().getResourceAsStream("/resources/lydian-subscriptions.xml")));
        List<Map<String, String>> dataList = mRSSHandler.getOutlineDataList();
        User user = User.findByEmail("seanlionheart@gmail.com");
        if (user == null) {
            user = new User();
            user.email = "seanlionheart@gmail.com";
            user.create();
        }
        Category category = null;
        UserCategory userCategory = null;
        for (Map<String, String> map : dataList) {
            
            if (!map.containsKey("xmlUrl")) {
                if (userCategory != null) {
                    userCategory.create();
                }
                category = new Category();
                category.name = map.get("title");
                category.create();
                userCategory = new UserCategory();
                userCategory.user = user;
                user.userCategories.add(userCategory);
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
                userCategory.feeds.add(feed);
            }
        }
        if (userCategory != null) {
            userCategory.create();
        }
    }
    
}
