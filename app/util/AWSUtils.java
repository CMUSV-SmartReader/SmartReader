package util;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.StartInstancesRequest;



public class AWSUtils {
	private AmazonEC2Client ec2; 
	private AWSCredentials credentials;

	public AWSUtils(){
		credentials = new BasicAWSCredentials("AKIAIFGISYDAY7USMOHA","3BCrM5t81frUS+tdfLfZC5zSeG6bOza1FWSamaqI" );
		ec2 = new AmazonEC2Client(credentials);
		System.out.println(new StartInstancesRequest().getInstanceIds().toString());
		/*
		StartInstancesRequest startRequest = new StartInstancesRequest().withInstanceIds("i-a4fef7fc");
		client.startInstances(startRequest);
		*/
		
	}
}
