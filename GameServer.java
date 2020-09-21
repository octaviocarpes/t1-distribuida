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
			System.out.println("Game Server is ready, waiting for " + numberOfPlayers + " players.");

		} catch (Exception e) {
			System.out.println("Game Serverfailed: " + e);
		}
	}

	@Override
	public int registra() {
		System.out.println("Adding player: ");
		GameInterfaceClient hello = null;
		playerCount++;
		try {
			int playerId = playerCount;
			remoteHostName = getClientHost();
			String connectLocation = "rmi://" + remoteHostName + ":52369/Hello2";

			System.out.println("Player id: " + playerId);
			System.out.println("Player address: " + connectLocation);

			return addPlayer(playerId, connectLocation);
		} catch (Exception e) {
			System.out.println ("Failed to get client IP");
			e.printStackTrace();
		}

		return playerCount;
	}

	private int addPlayer(int playerId, String connectLocation) {
		playerIds.add(playerId);
		playerAddresses.add(connectLocation);
		System.out.println("Current players: " + playerIds);
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

	@Override
	public void verificaSeUltimoPlayer() throws RemoteException {
		if (numberOfPlayers == playerCount) {
			System.out.println("All players are registered!");
		}
	}
}
