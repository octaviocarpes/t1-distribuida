import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameInterfaceClient extends Remote {
    public void inicia() throws RemoteException;
    public void indicaUltimoJogador(int playerId) throws RemoteException;
    public void finaliza(String data) throws RemoteException;
    public String cutuca() throws RemoteException;
 }