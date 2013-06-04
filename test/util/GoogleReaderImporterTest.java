package util;

import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import models.Feed;
import models.User;
import models.FeedCategory;
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
        FeedCategory feedCategory = null;
        for (Map<String, String> map : dataList) {
            
            if (!map.containsKey("xmlUrl")) {
                if (feedCategory != null) {
                    feedCategory.create();
                    user.userCategories.add(feedCategory);
                }
                feedCategory = new FeedCategory();
                feedCategory.name = map.get("title");
                feedCategory.user = user;
                user.userCategories.add(feedCategory);
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
    }
    
}
