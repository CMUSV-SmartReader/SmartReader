package models;


import java.util.Map;

import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;


import com.google.code.morphia.annotations.Entity;
import com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.morphia.annotations.Id;

@Entity
public class Article extends MongoModel{

	@Id
    public ObjectId id;
	
	public Class<?> reference;
    
	public ObjectId referenceId;
	
    public Map<String,Object> data;
    
    @SuppressWarnings("unchecked")
    public Article(SyndEntry entry) {
        ObjectMapper m = new ObjectMapper();
        this.data = m.convertValue(entry, Map.class);
    }
    
    public Article() {
        
    }
    
    public void setReference(Class<?> reference, ObjectId referenceId){
    	this.reference = reference;
    	this.referenceId = referenceId;
    }
}
