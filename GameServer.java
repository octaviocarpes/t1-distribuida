import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameServer extends UnicastRemoteObject implements GameInterfaceServer {
	// private static volatile int result;
	private static volatile String result;
	private static volatile boolean changed;
	private static volatile String remoteHostName;
	private static volatile boolean isGameRoomFull;
	private static volatile int numberOfPlayers;
	private static volatile int playerCount;
	private static volatile ArrayList playerIds;
	private static volatile ArrayList<String> playerAddresses;
	private static volatile GameInterfaceClient hello;
	private static volatile int kill;
	private static volatile String [] killMessages = {
			"You were killed by the mighty server",
			"So long loser",
			"At least you've tried",
			"Lost to RNG... Pathetic",
	};

	static class SayHello extends TimerTask {
		public void run() {
			for (int i = 0; i < playerCount; i++) {
				try {
					System.out.println();
					System.out.println();
					System.out.println("Poking client at: " + playerAddresses.get(i));
					hello = (GameInterfaceClient) Naming.lookup(playerAddresses.get(i));
					System.out.println(hello.cutuca());
					System.out.println();
					System.out.println();
				} catch (NotBoundException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					System.out.println(playerAddresses.get(i) + " is not responding!");
				}
			}
		}
	}


	public GameServer() throws RemoteException {
	}
	
	public static void main(String[] args) throws RemoteException {
		hello = null;
		isGameRoomFull = false;
		Random r = new Random();
		kill = r.nextInt(100);

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

	private int addPlayer(int playerId, String connectLocation) {
		playerIds.add(playerId);
		playerAddresses.add(connectLocation);
		System.out.println("Current players: " + playerIds);
		return playerId;
	}

	private void notifyLastPlayer(String connection, int port) throws RemoteException, NotBoundException, MalformedURLException {
		try {
			hello = (GameInterfaceClient) Naming.lookup(connection);
			hello.indicaUltimoJogador(port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void notifyAllPlayersToBeReady() {
		for (int i = 0; i < playerAddresses.size(); i++) {
			try {
				String connection = playerAddresses.get(i);
				hello = (GameInterfaceClient) Naming.lookup(connection);
				hello.inicia();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void pokeClients() {
		// And From your main() method or any other method
		Timer timer = new Timer();
		timer.schedule(new SayHello(), 0, 5000);
	}

	private static void pokeClient(String clientAddress) {

	}

	@Override
	public int registra(int playerRemotePort) {
//		Try to add a player
//		if the game room is not getting full,
//			add a player and return its id to the client
//		else if the game room is getting full,
//			add the player to game and tell him he is the last one,
//			after this, tell all the players to get ready.
//		after everything
//			pokes all clients

		try {
			playerCount++;
			remoteHostName = getClientHost();
			String connectLocation = "rmi://" + remoteHostName + ":" + playerRemotePort + "/Hello2";

			if (playerCount < numberOfPlayers) {
				return addPlayer(playerRemotePort, connectLocation);
			} else if (playerCount == numberOfPlayers) {
				addPlayer(playerRemotePort, connectLocation);
				notifyLastPlayer(connectLocation, playerRemotePort);
				notifyAllPlayersToBeReady();
				pokeClients();
				return playerRemotePort;
			}
		} catch (ServerNotActiveException | NotBoundException | MalformedURLException | RemoteException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private void tryToFinishPlayer(int playerId) throws RemoteException, MalformedURLException, NotBoundException {
		try {
			System.out.println("Trying to kill the player " + playerId + "...");
			Random r = new Random();
			int killNumber = r.nextInt(100);
			System.out.println(killNumber + ", " + kill);
			if (kill == killNumber) {
				String playerAddress = playerAddresses.get(playerIds.indexOf(playerId));
				hello = (GameInterfaceClient) Naming.lookup(playerAddress);
				hello.finaliza(killMessages[r.nextInt(3)]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int joga(int id) throws RemoteException {
		try {
			System.out.println();
			System.out.println();
			System.out.println("Player " + id + " is playing...");
			Random r = new Random();
			int timer = r.nextInt(1500-500) + 500;
			Thread.sleep(timer);
			tryToFinishPlayer(id);
			System.out.println("Player " + id + " finished playing!");
			System.out.println();
			System.out.println();
		} catch (InterruptedException | MalformedURLException | NotBoundException e) {
			e.printStackTrace();
		}

		return 0;
	}

	@Override
	public int encerra(int id) throws RemoteException {
		return 0;
	}
}
