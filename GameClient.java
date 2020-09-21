import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;

public class GameClient extends UnicastRemoteObject implements GameInterfaceClient {
	private static volatile int i, j;
	private static volatile int playerId;

	public GameClient() throws RemoteException {
	}

	public static void main(String[] args) {
		int result;

		if (args.length != 2) {
			System.out.println("Usage: java GameClient <server ip> <client ip>");
			System.exit(1);
		}
	
		try {
			System.setProperty("java.rmi.server.hostname", args[1]);
			LocateRegistry.createRegistry(52369);
			System.out.println("java RMI registry created.");
		} catch (RemoteException e) {
			System.out.println("java RMI registry already exists.");
		}

		try {
			String client = "rmi://" + args[1] + ":52369/Hello2";
			Naming.rebind(client, new GameClient());
			System.out.println("Game Server is ready.");
		} catch (Exception e) {
			System.out.println("Addition Serverfailed: " + e);
		}

		String remoteHostName = args[0];
		String connectLocation = "rmi://" + remoteHostName + ":52369/Hello";

		GameInterfaceServer hello = null;
		try {
			System.out.println("Connecting to server at : " + connectLocation);
			hello = (GameInterfaceServer) Naming.lookup(connectLocation);
		} catch (Exception e) {
			System.out.println ("GameClient failed: ");
			e.printStackTrace();
		}

		try {
			System.out.println("Call to server..." );
			playerId = hello.registra();
			System.out.println("You are the player: " + playerId);
			hello.verificaSeUltimoPlayer();
		} catch (RemoteException e) {
			e.printStackTrace();
		}


	}


	@Override
	public void inicia() throws RemoteException {
		System.out.println("Player " + playerId + " turn");

	}

	@Override
	public void finaliza() throws RemoteException {

	}

	@Override
	public void cutuca() throws RemoteException {

	}
}
