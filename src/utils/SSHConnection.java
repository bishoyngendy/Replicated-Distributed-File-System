package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import args.Args;
import args.ClientArgs;
import args.MasterArgs;

public class SSHConnection {
	private ChannelExec p;
	private BufferedReader in;
	private static final String RMI_CLIENT = "client.jar";
	private static final String RMI_MASTER = "master.jar";
	private static final String RMI_REPLICA = "replica.jar";
	private Session session;
	private Channel channel;
	
	
	//creating new ssh connection 
	public boolean openConnection(String hostAdd, String password, String username, Args args, String path) {
		boolean success = false;
		try {
			java.util.Properties config = new java.util.Properties(); 
	    	config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();
			JSch.setConfig(config);
			session=jsch.getSession(username, hostAdd, 22);
	    	session.setPassword(password);
	    	session.setConfig(config);
	    	session.connect(10000);
			System.out.println("Connected");
	    	
			
			String jar;
			if (args instanceof ClientArgs) {
				jar = RMI_CLIENT;
			} else if (args instanceof MasterArgs){
				jar = RMI_MASTER;
			} else {
				jar = RMI_REPLICA;
			}
			channel=session.openChannel("exec");
	        ((ChannelExec)channel).setCommand(" cd " + path + " ; java -jar " +
	        			jar + " " + args.toString() +" ;");
	        System.out.println(" cd " + path + " ; java -jar " +
	        			jar + " " + args.toString() +" ;");
	        //channel.setInputStream(null);
	        p = (ChannelExec) channel;
	        //((ChannelExec)channel).setErrStream(System.err);
	        in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
	        channel.connect();
	        
			
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSchException e) {
			e.printStackTrace();
		}
		return success;
	}
	
	//recieving data from ssh server
	public String recvData () {
		String input = "";
		try {
			String s = in.readLine();
			input += s;
			while (in.ready()) {
			  s = in.readLine();
			  input += s;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return input;
	}
	
	public ChannelExec closeConnection () {
		ChannelExec k = null;
		try {
			k = p;
			in.close();
			p = null;
			in = null;
			channel.disconnect();
	        session.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return k;
	}
}
