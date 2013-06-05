package util;

import org.junit.Before;
import org.junit.Test;

public class AWSUtilsTest {
    private AWSUtils aws;
    private String instanceId = "i-a4fef7fc";
    
    @Before
    public void init() throws InterruptedException {
        aws = new AWSUtils();
    }
    
    @Test
    public void testConnection() throws InterruptedException {
        System.out.println(aws.getInstanceStatus(instanceId));
        aws.startInstance(instanceId);
        System.out.println(aws.getInstanceStatus(instanceId));
        aws.stopInstance(instanceId);
        System.out.println(aws.getInstanceStatus(instanceId));
        
    }
}
