import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class GameClient extends UnicastRemoteObject implements GameInterfaceClient {
	private static volatile int i, j;
	private static volatile int playerId;
	private static volatile GameInterfaceServer hello;

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
					System.out.println("Register");
					playerId = hello.registra();
					System.out.println("You are the player: " + playerId);
					System.out.println();
					break;
				case 2:
					System.out.println();
					System.out.println("Play");
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


	@Override
	public void inicia() throws RemoteException {
		System.out.println("Player " + playerId + " intiates");
	}

	@Override
	public void finaliza() throws RemoteException {
		System.out.println("Player " + playerId + " finalizes");
	}

	@Override
	public void cutuca() throws RemoteException {
		System.out.println("Player " + playerId + " pokes");
	}
}
