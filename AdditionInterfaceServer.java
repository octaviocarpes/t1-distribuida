import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AdditionInterfaceServer extends Remote {
	public void Add() throws RemoteException;
}
