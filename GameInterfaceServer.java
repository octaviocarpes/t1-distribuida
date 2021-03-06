import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameInterfaceServer extends Remote {
    public int registra(int playerRemotePort) throws RemoteException;

    public int joga(int id) throws RemoteException;

    public int encerra(int id) throws RemoteException;
}
