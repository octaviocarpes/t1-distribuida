import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class GameClient extends UnicastRemoteObject implements GameInterfaceClient {
	private static volatile int i, j;
	private static volatile int playerId;
	private static volatile boolean playerReady;
	private static volatile String playerRemoteAddress;
	private static volatile int playerRemoteAddressPort;
	private static volatile GameInterfaceServer hello;

	public GameClient() throws RemoteException {
	}

	public static void main(String[] args) {


		if (args.length != 3) {
			System.out.println("Usage: java GameClient <server ip> <client ip> <client port>");
			System.exit(1);
		}

		playerRemoteAddress = args[1];
		playerRemoteAddressPort = Integer.parseInt(args[2]);

		try {
			System.setProperty("java.rmi.server.hostname", args[1]);
			LocateRegistry.createRegistry(Integer.parseInt(args[2]));
			System.out.println("java RMI registry created.");
		} catch (RemoteException e) {
			System.out.println("java RMI registry already exists.");
		}

		try {
			String client = "rmi://" + args[1] + ":" + playerRemoteAddressPort + "/Hello2";
			Naming.rebind(client, new GameClient());
			System.out.println("Game Server is ready.");
		} catch (Exception e) {
			System.out.println("Addition Serverfailed: " + e);
		}

		String remoteHostName = args[0];
		String connectLocation = "rmi://" + remoteHostName + ":52369/Hello";

		hello = null;
		try {
			System.out.println("Connecting to server at : " + connectLocation);
			hello = (GameInterfaceServer) Naming.lookup(connectLocation);
			menu();
		} catch (Exception e) {
			System.out.println ("GameClient failed: ");
			e.printStackTrace();
		}
	}

	public static void menu() throws RemoteException{
		boolean menu = true;
		while(menu != false) {
			System.out.println("Game Client:");
			System.out.println("1 - Register to game server");
			System.out.println("2 - Play on game server");
			System.out.println("3 - Exit");

			int scanner = new Scanner(System.in).nextInt();

			switch (scanner) {
				case 1:
					System.out.println();
					int id = hello.registra(playerRemoteAddressPort);
					playerId = id;
					System.out.println("Registered as the player: " + playerId);
					System.out.println();
					break;
				case 2:
					System.out.println();
					System.out.println("Player " + playerId + " is playing...");
					play();
					System.out.println();
					hello.joga(playerId);
					break;
				case 3:
					System.out.println();
					System.out.println("Quit");
					System.out.println();
					hello.encerra(playerId);
					System.exit(1);
					break;
				default:
					menu = false;
					break;
			}
		}
	}

	private static void play() {
		if (playerReady) {
			int count = 0;
			try {
				while (count < 50) {
					System.out.println("Making a move...(" + count + ")");
					hello.joga(playerId);
					count++;
				}
				hello.encerra(playerId);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("You are not ready! Be sure to register in the game server before playing.");
		}
	}

	@Override
	public void inicia() throws RemoteException {
		playerReady = true;
		System.out.println("Player " + playerId + " is ready");
	}

	@Override
	public void indicaUltimoJogador(int playerId) throws RemoteException {
		this.playerId = playerId;
		playerReady = true;
	}

	@Override
	public void finaliza(String data) throws RemoteException {
		System.out.println(data);
		System.exit(1);
	}

	@Override
	public void cutuca(String data) throws RemoteException {
		System.out.println("Player " + playerId + " pokes");
	}
}
