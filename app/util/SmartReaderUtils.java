package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanMap;

import com.google.gson.GsonBuilder;

public class SmartReaderUtils {

    @SuppressWarnings("rawtypes")
    public List<Map> convertListObjs(List objs) {
        List<Map> results = new ArrayList<Map>();
        for (Object obj : objs) {
            results.add(new BeanMap(obj));
        }
        return results;
    }
    
    public static GsonBuilder builder = new GsonBuilder();
}
