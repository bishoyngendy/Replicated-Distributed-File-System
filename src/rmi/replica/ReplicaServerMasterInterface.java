package rmi.replica;

import java.rmi.RemoteException;
import java.util.List;

import data.FileContent;
import data.ReplicaLoc;

public interface ReplicaServerMasterInterface {
	
	public void createFile(FileContent data) throws RemoteException;
	
	public void toPrimary(FileContent data, List<ReplicaLoc> locs) throws RemoteException;
	
}
