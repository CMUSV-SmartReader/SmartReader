package util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import play.Logger;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;

public class AWSUtils {
    private AmazonEC2Client ec2;
    public AWSUtils() throws InterruptedException {
        AWSCredentialsProvider credentialsProvider = new EnvironmentVariableCredentialsProvider() ;
        ec2 = new AmazonEC2Client(credentialsProvider);
        ec2.setEndpoint("ec2.us-west-1.amazonaws.com");
	}
    public void startInstance(String instanceId) throws InterruptedException{
        StartInstancesRequest startRequest = new StartInstancesRequest().withInstanceIds(instanceId);
        StartInstancesResult result = ec2.startInstances(startRequest);
        Logger.debug("Started instance.  State is " + result.getStartingInstances().get(0).getCurrentState());
        while(true){
            if(!getInstanceStatus(instanceId).equals("pending")) break;
            Thread.sleep(1000);
        }
    }
    public String getInstanceStatus(String instanceId){
        DescribeInstancesRequest instanceRequest = new DescribeInstancesRequest().withInstanceIds(instanceId);
        Instance instance = ec2.describeInstances(instanceRequest).getReservations().get(0).getInstances().get(0);
        return instance.getState().getName();
    }
    
    public void stopInstance(String instanceId) throws InterruptedException{
        StopInstancesRequest stopInstancesRequest = new StopInstancesRequest().withInstanceIds(instanceId);
        StopInstancesResult result = ec2.stopInstances(stopInstancesRequest);
        Logger.debug("Stopping instance.  State is " + result.getStoppingInstances().get(0).getCurrentState());
        while(true){
            if(!getInstanceStatus(instanceId).equals("stopping")) break;
            Thread.sleep(1000);
        }
    }
    public void printStatus(){
        System.out.println("You have access to " + ec2.describeAvailabilityZones().getAvailabilityZones().size() + " Availability Zones.");
        
        DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
        List<Reservation> reservations = describeInstancesRequest.getReservations();
        Set<Instance> instances = new HashSet<Instance>();

        for (Reservation reservation : reservations) {
            instances.addAll(reservation.getInstances());
        }
        System.out.println("You have " + instances.size() + " Amazon EC2 instance(s) running.");

    }
}
