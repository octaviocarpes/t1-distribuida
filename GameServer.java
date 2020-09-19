import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

public class GameServer extends UnicastRemoteObject implements GameInterfaceServer {
	// private static volatile int result;
	private static volatile String result;
	private static volatile boolean changed;
	private static volatile String remoteHostName;
	private static volatile int numberOfPlayers;
	private static volatile int playerCount;
	private static volatile ArrayList playerIds;
	private static volatile ArrayList playerAddresses;

	public GameServer() throws RemoteException {
	}
	
	public static void main(String[] args) throws RemoteException {
		if (args.length != 2) {
			System.out.println("Usage: java AdditionServer <server ip>");
			System.exit(1);
		}

		try {
			System.setProperty("java.rmi.server.hostname", args[0]);
			LocateRegistry.createRegistry(52369);
			System.out.println("java RMI registry created.");
		} catch (RemoteException e) {
			System.out.println("java RMI registry already exists.");
		}

		try {
			String server = "rmi://" + args[0] + ":52369/Hello";
			Naming.rebind(server, new GameServer());
			numberOfPlayers = Integer.parseInt(args[1]);
			playerCount = 0;
			playerIds = new ArrayList();
			playerAddresses = new ArrayList();
			System.out.println("Addition Server is ready, waiting for " + numberOfPlayers + " players.");
		} catch (Exception e) {
			System.out.println("Addition Serverfailed: " + e);
		}

	}

	@Override
	public int registra() {
		System.out.println("Adding player...");
		try {
			remoteHostName = getClientHost();
		} catch (Exception e) {
			System.out.println ("Failed to get client IP");
			e.printStackTrace();
		}
		String connectLocation = "rmi://" + remoteHostName + ":52369/Hello2";

		int playerId = playerCount;
		String playerAddress = remoteHostName;

		if (playerCount < numberOfPlayers) {
			playerIds.add(playerId);
			playerAddresses.add(playerAddress);
			playerCount++;
		} else if(playerCount == numberOfPlayers) {
			for (int i = 0; i < playerAddresses.size(); i++) {
				GameInterfaceClient hello = null;
				try {
					hello = (GameInterfaceClient) Naming.lookup(connectLocation);
					hello.inicia();
					System.out.println("Connecting to server at : " + connectLocation);
				} catch (Exception e) {
					System.out.println ("GameClient failed: ");
					e.printStackTrace();
				}
			}
			return -1;
		}

		return playerId;
	}

	@Override
	public int joga(int id) throws RemoteException {
		return 0;
	}

	@Override
	public int encerra(int id) throws RemoteException {
		return 0;
	}

}
