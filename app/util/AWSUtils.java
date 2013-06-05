package util;

import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.StartInstancesRequest;

public class AWSUtils {
	private AmazonEC2Client ec2;
	private AWSCredentials credentials;

	public AWSUtils() {
		AWSCredentialsProvider credentialsProvider = new EnvironmentVariableCredentialsProvider() ;
		
		ec2 = new AmazonEC2Client(credentialsProvider);
		DescribeInstancesResult describeInstanceRequest = ec2.describeInstances();
		List<Reservation> reservations = describeInstanceRequest.getReservations();
		System.out.println(reservations.toString());
		//System.out.println(new StartInstancesRequest().getInstanceIds().toString());
		
		//StartInstancesRequest startRequest = new StartInstancesRequest().withInstanceIds("i-a4fef7fc");
		 //client.startInstances(startRequest);
		 

	}
}
