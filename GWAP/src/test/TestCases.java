package test;

import static org.junit.Assert.*;
import gameClient.Client;
import gameClient.InputConsoleListener;
import gameServer.Server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestCases {
	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	
	
	@Test
	public final void testCorrectJoin(){
		//Launch a new Server
		
		//Launch a client and create a new game session
			//gameSessionID = ?
		
		//Launch a number of other clients to join the game session
		
		//Check the number of connected clients in the created game session
		fail("unimplemented");
	}
	
	@Test
	public final void testBadJoin(){
		boolean test1,test2,test3,test4;
		
		//test invalid sessionID
		test1 = testCommand("@join blabla")==Client.NORMAL;
		
		//invalid sessionID
		test2 = testCommand("@join  ")==Client.NORMAL;
		
		//try joining a non existing session
		test3 = testCommand("@join 91849184")==Client.NORMAL;
		
		//pass in another command as the session-ID
		test4 = testCommand("@join @list")==Client.NORMAL;
		
		assertTrue(test1&&test2&&test3&&test4);
	}
	
	/**
	 * Test to see how the client changes with a given input
	 * @param str
	 * @return the mode of the client
	 */
	public final int testCommand(String str) {
		int portNumber = 5000;
		int result = -1;
		//server
		Server mainServer = new Server(portNumber);
		mainServer.launchGameServer();
		
		//client
		Client client = new Client(portNumber);
		InputConsoleListener consoleListener = client.getConsole(); 
		
		//client input
		consoleListener.inputToConsole(str);
		
		result = client.getMode();
		client.close();
		return result;
	}
	
	@Test
	public final void testHostSession(){
		//Launch a new server
		
		//launch many clients
			//make each client host a game
		
		//assert list of active sessions == number or clients
	
	}
	
	@Test
	public final void testClientWaitingState(){
		//boolean result = true;
		//Launch a new Server
		
		//Launch a client and create a new game session
			//gameSessionID = ?
		//set min number of players to 'minPlayers'  
		
		//Launch a number of other clients and join the game session
		//if (minPlayers < gameSessionCount) && (client.getMode() != WAITING) 
			//result == false
		
		fail("unimplemented");
		
	}
	

}
