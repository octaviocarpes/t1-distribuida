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
	private static volatile ArrayList<String> playerAddresses;

	public GameServer() throws RemoteException {
	}
	
	public static void main(String[] args) throws RemoteException {
		if (args.length != 2) {
			System.out.println("Usage: java GameServer <server ip> <number of players>");
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
		playerCount++;
		try {
			remoteHostName = getClientHost();
			String connectLocation = "rmi://" + remoteHostName + ":52369/Hello2";

			int playerId = playerCount;

			if (playerCount == numberOfPlayers) {
				System.out.println("Game room is full!");
				System.out.println("Game starting...");
				GameInterfaceClient hello = null;
				for (int i = 0; i < playerAddresses.size() ; i++) {
					String playerAddress = playerAddresses.get(i);
					hello = (GameInterfaceClient) Naming.lookup(playerAddress);
					hello.inicia();
				}
				return -1;
			}

			if (playerCount < numberOfPlayers) {
				playerIds.add(playerId);
				playerAddresses.add(connectLocation);
				System.out.println("Current players: " + playerIds);
				return playerId;
			}
		} catch (Exception e) {
			System.out.println ("Failed to get client IP");
			e.printStackTrace();
		}

		return -1;
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
