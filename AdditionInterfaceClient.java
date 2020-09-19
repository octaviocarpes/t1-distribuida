import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AdditionInterfaceClient extends Remote {
	public String Result(String val) throws RemoteException;
}
