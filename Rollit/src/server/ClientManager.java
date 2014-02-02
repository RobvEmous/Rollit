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
import clientAndServer.Password;
import exceptions.ProtocolNotFollowedException;
import java.util.*;

/**
 * Class used for managing clients.<br> The managed clients do not need to be logged in, but only clients who  are logged in are able to join games or receive high-scores.<br> At all times this server tries to be in the same state as the client by responding to requests adequately. This is also requested from the client. When a client is not able to do this he will be kicked:<br> For example when a client sends game related commands while not playing a game, like chat-messages or moves.<br> Non-logged in clients will be kicked when trying to perform anything else than logging in and these clients will also be removed after a while. Because of that, clients should immediately try to log in after  establishing a connection with the server.<br> When clients log out their                 {@link ClientCommunicator}         will be shut down and destroyed, so they have to reconnect and re-login when they want to join again.
 * @author         Rob van Emous
 * @version         1.0
 */
public class ClientManager implements Observer {
	/**
	 * Stores authenticated clients who can start playing a game or ask 
	 * for certain high-scores.
	 */
	private HashMap<ClientCommunicator, String> clients;
	
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
	 * Is used to read highScores.
	 */
	private ScoreRW scoreRW;
	
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
	private String guestUpgraded = "Guest logged in: ";
	private String guestKicked = "Guest kicked: ";
	private String guestTimeOut = "Guest timed out: ";
	private String allGuestsRemoved = "All guests removed";
	private String clientDowngraded= "Client logged out: ";
	private String clientKicked = "Client kicked: ";
	private String clientDisconnected = "Guest disconnected: ";
	private String allClientsRemoved = "All clients removed";
	private String clientToGame = "Client send to GameManager: ";
	private String clientFromGame = "Client got from GameManager: ";
	
	/**
	 * Starts a client manager.
	 * @param main used to send messages to the MainUI.
	 * @param gameManager used to send authenticated players who want to 
	 * play a game to the game 'waiting room'. 
	 */
	/*@
	  requires main != null;
	  ensures this != null;
	 */
	public ClientManager(Main main) {
		this.main = main;	
		clients = new HashMap<ClientCommunicator, String>();
		guests = new HashMap<ClientCommunicator, Long>();
		playerRW = new PlayerRW();
		playerRW.open();
		scoreRW = new ScoreRW();
		//scoreRW.open();		
		gameManager = new GameManager(main, this, scoreRW);
		main.setGameManager(gameManager);
		startInactiveGuestRemover();
	}
	
	private void startInactiveGuestRemover() {
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
						removeGuests(guestsToBeRemoved, false);
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
	/*@
	  requires guest != null;
	 */
	public void addGuest(ClientCommunicator guest) {
		long startTime = System.currentTimeMillis();
		synchronized (guests) {
			guests.put(guest, startTime);
			sendMessage(guestAdded + guestInfo(guest));
		}	
		guest.addObserver(this);		
	}
	
	private void upgradeGuest(ClientCommunicator guest, String guestName) {
		synchronized (guests) {
			guests.remove(guest);
		}
		synchronized (clients) {
			clients.put(guest, guestName);
		}
		sendMessage(guestUpgraded + clientInfo(guest));
	}
	
	/*@
	  requires client != null && name != null;
	 */
	public void addClient(ClientCommunicator client, String name) {
		sendMessage(clientFromGame + clientInfo(client));
		synchronized (clients) {
			clients.put(client, name);
		}		

	}
	
	private void downgradeClient(ClientCommunicator client) {
		long startTime = System.currentTimeMillis();
		sendMessage(clientDowngraded + clientInfo(client));
		synchronized (clients) {
			clients.remove(client);
		}		
		synchronized (guests) {
			guests.put(client, startTime);
		}	
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
	private void removeClients(ArrayList<ClientCommunicator> theClients, boolean kick) {
		for (ClientCommunicator client : theClients) { 
			removeClient(client, kick);
		}
	}
	
	/**
	 * Removes a client {@link ClientCommunicator} from the collection of 
	 * clients and shuts this client down.
	 * 
	 * @param client the client that will be removed
	 */
	private void removeClient(ClientCommunicator client, boolean kick) {
		client.deleteObserver(this);
		client.shutdown();	
		if (kick) {
			sendMessage(clientKicked + clientInfo(client));
		} else {
			sendMessage(clientDisconnected + clientInfo(client));
		}		
		synchronized (clients) {
			clients.remove(client);
		}		
	}
	
	/**
	 * Hands the client over to the GameManager to start playing.
	 * 
	 * @param client the client that will be handed over
	 */
	private void upgradeClient(ClientCommunicator client) {
		client.deleteObserver(this);
		sendMessage(clientToGame + clientInfo(client));
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
		sendMessage(allClientsRemoved);
	}
	
	/**
	 * Removes the specified guests {@link ClientCommunicator} from the collection of 
	 * guests and shuts those guests down.
	 */
	private void removeGuests(ArrayList<ClientCommunicator> theGuests, boolean kick) {
		for (ClientCommunicator guest : theGuests) { 
			removeGuest(guest, kick);
		}
	}
	
	/**
	 * Removes a guest {@link ClientCommunicator} from the collection of 
	 * guests and shuts this guest down.
	 * 
	 * @param guest the guest that will be removed
	 */
	private void removeGuest(ClientCommunicator guest, boolean kick) {
		guest.deleteObserver(this);
		guest.shutdown();	
		if (kick) {
			sendMessage(guestKicked + guestInfo(guest));
		} else {
			sendMessage(guestTimeOut + guestInfo(guest));
		}
		
		synchronized (guests) {
			guests.remove(guest);
		}
	}
	
	private String clientInfo(ClientCommunicator client) {
		return clients.get(client) + " - " + client.toString();
	}
	
	private String guestInfo(ClientCommunicator guest) {
		return guest.toString();
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
		playerRW.close();
	}
	
	/*@
	  requires o != null; 
	*/
	@Override
	public void update(Observable o, Object arg) {
		Command comm = (Command) arg;
		ClientCommunicator client = (ClientCommunicator) o;
		if (guests.containsKey(o)) {
			if (comm.getId().equals(Commands.COM_LOGIN)) {
				try {
					String name = "";
					String password = "";
					try {
						name = comm.getArgs()[0];
						password = comm.getArgs()[1];				
					} catch (ArrayIndexOutOfBoundsException e) {
						throw new ProtocolNotFollowedException();
					}
					if (performLogin(client, name, password)) {
						client.sendAck(comm.getId(), Commands.ANS_LOGIN_GOOD);
					} else {
						client.sendAck(comm.getId(), Commands.ANS_LOGIN_BAD);
					}
				} catch (ProtocolNotFollowedException e) {
					try {
						client.sendAck(comm.getId(), Commands.ANS_GEN_BAD);
					} catch (IOException e1) {			
						e1.printStackTrace();
					}
					removeClient(client, true);
				} catch (IOException e) {
					removeGuest(client, false);
					e.printStackTrace();
				}
			} else {
				try {
					client.sendAck(comm.getId(), Commands.ANS_GEN_BAD);
				} catch (IOException e) {			
					e.printStackTrace();
				}
				removeClient(client, true);
			}
		} else if (clients.containsKey(o)) {
			if (comm.getId().equals(Commands.COM_LOGOUT)) {
				try {
					downgradeClient(client);
					client.sendAck(comm.getId(), Commands.ANS_GEN_GOOD);
				} catch (IOException e) {
					removeClient(client, false);
					e.printStackTrace();
				}
			} else if (comm.getId().equals(Commands.COM_JOIN)) {
				try {
					int nrOfPlayers = 0;
					try {
						nrOfPlayers = Integer.parseInt(comm.getArgs()[0]);
					} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
						throw new ProtocolNotFollowedException();
					}
					joinGame(client, nrOfPlayers);
					client.sendAck(comm.getId(), Commands.ANS_GEN_GOOD);
				} catch (ProtocolNotFollowedException e) {
					try {
						client.sendAck(comm.getId(), Commands.ANS_GEN_BAD);
					} catch (IOException e1) {			
						e1.printStackTrace();
					}
					removeClient(client, true);
				} catch (IOException e) {
					removeGuest(client, false);
					e.printStackTrace();
				}
			} else {
				try {
					client.sendAck(comm.getId(), Commands.ANS_GEN_BAD);
				} catch (IOException e) {			
					e.printStackTrace();
				}
				removeClient(client, true);
			}
		}
	}
	
	private boolean performLogin(ClientCommunicator client, String name, String password) throws ProtocolNotFollowedException {
		if (Password.isValidPassword(password)) {
			Password pass = new Password();
			pass.setPassword(Password.INITIAL, password);
			Player player = new Player(name, pass);
			if (playerRW.hasPlayer(player)) {
				if (playerRW.checkPlayerPass(player)) {
					upgradeGuest(client, player.getName());
					return true;
				}
			} else {
				playerRW.writeNewPlayer(player);
				upgradeGuest(client, player.getName());
				return true;
			}
		} else {
			throw new ProtocolNotFollowedException();			
		}
		return false;
	} 
	
	private void joinGame(ClientCommunicator client, int nrOfPlayers) {
		gameManager.addWaiter(client, clients.get(client), nrOfPlayers);
		upgradeClient(client);
	}

}

