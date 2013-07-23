package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
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
//        for (Map<String, String> map : dataList) {
//            if (!map.containsKey("xmlUrl")) {
//                Category category = new Category();
//                category.name = map.get("title");
//                category.create();
//            }
//            else {
//                Feed feed = new Feed();
//                feed.htmlUrl = map.get("htmlUrl");
//                feed.xmlUrl = map.get("xmlUrl");
//                feed.title = map.get("title");
//                feed.type = map.get("type");
//                feed.create();
//            }
//        }
        return true;
    }
}
