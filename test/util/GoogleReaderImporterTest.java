package util;

import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import models.Category;
import models.Feed;

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
        xmlReader.parse(new InputSource(getClass().getResourceAsStream("/resources/subscriptions.xml")));
        xmlReader.parse(new InputSource(getClass().getResourceAsStream("/resources/lydian-subscriptions.xml")));
        List<Map<String, String>> dataList = mRSSHandler.getOutlineDataList();
        for (Map<String, String> map : dataList) {
            if (!map.containsKey("xmlUrl")) {
                Category category = new Category();
                category.name = map.get("title");
                category.create();
            }
            else {
                Feed feed = new Feed();
                feed.htmlUrl = map.get("htmlUrl");
                feed.xmlUrl = map.get("xmlUrl");
                feed.title = map.get("title");
                feed.type = map.get("type");
                feed.create();
            }
        }
    }
}
