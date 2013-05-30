package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class OPMLParser extends DefaultHandler {

    private List<Map<String, String>> outlineDataList;
    private HashMap<String, String> outlineData;

    public List<Map<String, String>> getOutlineDataList() {
        return outlineDataList;
    }
    
    public void startDocument() throws SAXException {
        outlineDataList = new ArrayList<Map<String,String>>();
    }

    public void endDocument() throws SAXException {
    }

    public void startElement(String uri, String localName, String qName,
        Attributes atts) throws SAXException {
        if(qName.equalsIgnoreCase("OUTLINE")) {
            outlineData = new HashMap<String, String>();
            outlineData.put("title", atts.getValue("title"));
            outlineData.put("type", atts.getValue("type"));
            outlineData.put("text", atts.getValue("text"));
            if (atts.getValue("xmlUrl") != null) {
                outlineData.put("xmlUrl", atts.getValue("xmlUrl"));
            }
            if (atts.getValue("htmlUrl") != null) {
                outlineData.put("htmlUrl", atts.getValue("htmlUrl"));
            }
            outlineDataList.add(outlineData);
        }
    }

    public void endElement(String uri, String localName, String qName)
    throws SAXException {
    }
}