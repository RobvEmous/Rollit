package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import clientAndServer.Command;
import clientAndServer.Commands;
import clientAndServer.GlobalData;
import clientAndServer.GlobalSettings;
import exceptions.ProtocolNotFollowedException;

/**
 * Class used for managing clients.<br>
 * The managed clients do not need to be logged in, but only clients who 
 * are logged in are able to join games or receive high-scores.<br>
 * At all times this server tries to be in the same state as the client
 * by responding to requests adequately. This is also requested from the
 * client. When a client is not able to do this he will be kicked:<br>
 * For example when a client sends game related commands while not playing
 * a game, like chat-messages or moves.<br>
 * Non-logged in clients will be kicked when trying to perform anything
 * else than logging in and these clients will also be removed after a
 * while. Because of that, clients should immediately try to log in after 
 * establishing a connection with the server.<br>
 * When clients log out their {@link ClientCommunicator} will be shut down 
 * and destroyed, so they have to reconnect and re-login when they want to
 * join again.
 * 
 * @author Rob van Emous
 * @version 0.3
 */
public class ClientManager implements Observer {
	/**
	 * Stores authenticated clients who can start playing a game or ask 
	 * for certain high-scores.
	 */
	private HashMap<ClientCommunicator, Player> clients;
	
	/**
	 * Stores unauthenticated clients and the time they joined so they can
	 * be kicked when necessary.
	 */
	private HashMap<ClientCommunicator, Long> guests;
	
	/**
	 * Is used to create new players or test passwords of existing 
	 * players.
	 */
	private PlayerRW playerRW;
	
	/**
	 * Used to send messages to the MainUI.
	 */
	private Main main;
	
	/**
	 * Used to send authenticated players who want to play a game to the 
	 * game 'waiting room'.
	 */
	private GameManager gameManager;
	
	private boolean stop = false;
	
	private String guestAdded = "Guest added: ";
	private String guestUpgraded = "Guest upgraded: ";
	private String guestRemoved = "Guest removed: ";
	private String allGuestsRemoved = "All guests removed";
	private String ClientRemoved = "Client removed: ";
	private String allClientsRemoved = "All clients removed";
	private String ClientToGame = "Client send to GameManager: ";
	
	/**
	 * Starts a client manager.
	 * @param main used to send messages to the MainUI.
	 * @param gameManager used to send authenticated players who want to 
	 * play a game to the game 'waiting room'. 
	 */
	public ClientManager(Main main, GameManager gameManager) {
		this.main = main;
		this.gameManager = gameManager;
		clients = new HashMap<ClientCommunicator, Player>();
		guests = new HashMap<ClientCommunicator, Long>();
		playerRW = new PlayerRW();
		playerRW.open();
		startInactiveGuestRemover();
	}
	
	private void startInactiveGuestRemover() {
		final ClientManager cm = this;   
		Thread remover = new Thread(new Runnable() {			
			@Override
			public void run() {
				int timeBeforeKick = GlobalSettings.TIME_BEFORE_KICK;
				int sleepCounts = GlobalSettings.SEARCH_TIME / GlobalSettings.SLEEP_TIME;
				while (!stop) {		
					ArrayList<ClientCommunicator> guestsToBeRemoved = 
							new ArrayList<ClientCommunicator>();
					synchronized (guests) {
						long currentTime = System.currentTimeMillis();
						for (ClientCommunicator guest : guests.keySet()) {
							if (currentTime - guests.get(guest) >= timeBeforeKick) {
								guestsToBeRemoved.add(guest);
							}
						}
						removeGuests(guestsToBeRemoved);
						guestsToBeRemoved.clear();
					}
					int counter = 0;
					while (!stop && counter < sleepCounts) {
						try {
							Thread.sleep(GlobalSettings.SLEEP_TIME);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						counter++;
					}

				}	
				
			}
		});
		remover.start();		
	}

	/**
	 * Add a {@link ClientCommunicator} to the collection of 
	 * ClientCommunicators.
	 * 
	 * @param guest ClientHandler that will be added
	 */
	public void addGuest(ClientCommunicator guest) {
		long startTime = System.currentTimeMillis();
		synchronized (guests) {
			guests.put(guest, startTime);
			sendMessage(guestAdded + guest.toString());
		}	
		guest.addObserver(this);
		
	}
	
	private void upgradeGuest(ClientCommunicator guest, Player guestPlayer) {
		synchronized (guests) {
			guests.remove(guest);
		}
		synchronized (clients) {
			clients.put(guest, guestPlayer);
		}
		sendMessage(guestUpgraded + guestPlayer.getName());
	}
	
	/**
	 * Removes all clients {@link ClientCommunicator} from the collection 
	 * of clients and shuts all clients down. 
	 */
	private void removeAllClients() {
		for (ClientCommunicator client : clients.keySet()) { 
			client.deleteObserver(this);
			client.shutdown();	
		}
		clients.clear();
		sendMessage(allGuestsRemoved);
	}

	/**
	 * Removes the specified guests {@link ClientCommunicator} from the collection of 
	 * guests and shuts those guests down.
	 */
	private void removeClients(ArrayList<ClientCommunicator> theClients) {
		for (ClientCommunicator client : theClients) { 
			removeClient(client);
		}
	}
	
	/**
	 * Removes a client {@link ClientCommunicator} from the collection of 
	 * clients and shuts this client down.
	 * 
	 * @param client the client that will be removed
	 */
	private void removeClient(ClientCommunicator client) {
		client.deleteObserver(this);
		client.shutdown();	
		synchronized (clients) {
			clients.remove(client);
		}
	}
	
	/**
	 * Removes all guests {@link ClientCommunicator} from the collection of 
	 * guests and shuts all guests down.
	 */
	private void removeAllGuests() {
		for (ClientCommunicator guest : guests.keySet()) { 
			guest.deleteObserver(this);
			guest.shutdown();	
		}
		guests.clear();
	}
	
	/**
	 * Removes the specified guests {@link ClientCommunicator} from the collection of 
	 * guests and shuts those guests down.
	 */
	private void removeGuests(ArrayList<ClientCommunicator> theGuests) {
		for (ClientCommunicator guest : theGuests) { 
			removeGuest(guest);
		}
	}
	
	/**
	 * Removes a guest {@link ClientCommunicator} from the collection of 
	 * guests and shuts this guest down.
	 * 
	 * @param guest the guest that will be removed
	 */
	private void removeGuest(ClientCommunicator guest) {
		guest.deleteObserver(this);
		guest.shutdown();	
		synchronized (guests) {
			guests.remove(guest);
		}
	}
	
	private void sendMessage(String text) {
		main.addMessage(this, text);
	}
	
	/**
	 * Removes and shuts down all guests and clients
	 */
	public void shutDown() {
		stop = true;
		removeAllGuests();
		removeAllClients();
	}

	@Override
	public void update(Observable o, Object arg) {
		Command comm = (Command) arg;
		ClientCommunicator client = (ClientCommunicator) o;
		if (guests.containsKey(o)) {
			if (comm.getId().equals(Commands.COM_LOGIN)) {
				try {
					String name = "";
					Password password = null;
					try {
						name = comm.getArgs()[0];
						password = new Password();
						password.setPassword(Password.INITIAL, comm.getArgs()[1]);
					} catch (ArrayIndexOutOfBoundsException e) {
						throw new ProtocolNotFollowedException();
					}
					Player player = new Player(name, password);
					if (playerRW.hasPlayer(player)) {
						if (playerRW.checkPlayerPass(player)) {
							client.sendAck(comm.getId(), Commands.ANS_LOGIN_GOOD);
							upgradeGuest(client, player);
						} else {
							client.sendAck(comm.getId(), Commands.ANS_LOGIN_BAD);
						}
					} else {
						client.sendAck(comm.getId(), Commands.ANS_LOGIN_GOOD);
						playerRW.writeNewPlayer(player);
						upgradeGuest(client, player);
					}
				} catch (IOException e) {
					removeGuest(client);
				} catch (ProtocolNotFollowedException e) {
					removeGuest(client);
				}	
			} else {
				try {
					client.sendAck(comm.getId(), Commands.ANS_GEN_BAD);
					removeClient(client);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (clients.containsKey(o)) {
			//TODO
		}
	}
}
