package tool;

import java.rmi.Remote;
import java.rmi.RemoteException;



public interface SMSInterface extends Remote {
	
	public String sendMessage(final String phone, final String message) throws RemoteException;

}
