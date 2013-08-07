package controllers;

import models.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.google.gson.Gson;
import com.sun.syndication.feed.atom.Category;

import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
import util.FeedParser;
import util.OPMLParser;

public class FeedImportController extends Controller{

    public static Result upload() {
        MultipartFormData body = request().body().asMultipartFormData();
        
        FilePart importFile = body.getFile("import");
        
        if (importFile != null) {
            String fileName = importFile.getFilename();
            String fileType = importFile.getContentType();
            File file = importFile.getFile();
            System.out.println("fileName: " + fileName);
            System.out.println("fileType: " + fileType);
            try {
                if (parseImportedFeed(file)) {
                    return ok("file uploaded");
                } else {
                    flash("error", "invalid format");
                    return redirect(routes.Application.index());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            flash("error", "Missing File");
            return redirect(routes.Application.index());
        }
        return ok("ok");
      }
    
    private static boolean parseImportedFeed(File file) throws Exception {
        
        OPMLParser mRSSHandler = new OPMLParser();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        XMLReader xmlReader = parser.getXMLReader();
        xmlReader.setContentHandler(mRSSHandler);
        FileInputStream fis = new FileInputStream(file);
        xmlReader.parse(new InputSource(fis));
        List<Map<String, String>> dataList = mRSSHandler.getOutlineDataList();
        
        Identity identity = SecureSocial.currentUser();
        User user = User.findByEmail(identity.email().get());

        FeedCategory currFeedCategory = user.getDefaultCategory();

        if (currFeedCategory == null) {
            currFeedCategory = new FeedCategory();
            currFeedCategory.name = "Uncategorized";
            currFeedCategory.user = user;
            user.addUserCategory(currFeedCategory);
        }

        for (Map<String, String> map : dataList) {
            if (!map.containsKey("xmlUrl")) {
                // it is a category
                currFeedCategory = new FeedCategory();
                currFeedCategory.name = map.get("title");
                currFeedCategory.user = user;
                user.addUserCategory(currFeedCategory);
            } else {
                // it is a feed
                String feedXmlUrl = map.get("xmlUrl");
                Feed feedEntity = null;
                try {
                    feedEntity = FeedParser.parseFeedInfo(feedXmlUrl);
                } catch(Exception e) {
                    e.printStackTrace();
                    continue;
                }
                feedEntity = feedEntity.createUnique();
                feedEntity.addUser(user);
                user.feeds.add(feedEntity);
                UserFeed userFeed = new UserFeed();
                userFeed.feed = feedEntity;
                userFeed.user = user;
                userFeed.create();
                
                currFeedCategory.addUserFeed(userFeed);
                try {
                    feedEntity.crawl();
                } catch (Exception e){
//                    System.out.println("||||||caught exception when crawl");
                }
                
            }
        }
        return true;
    }
}
