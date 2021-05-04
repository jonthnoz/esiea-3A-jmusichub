package musichub.main;

import musichub.business.connection.server.*;
import musichub.business.connection.client.*;

/**
 * Main class, launching a server instance, a client console, or a server console according to the argument given at execution
 * @author Melissa Genovese, Johanne Scemama
 */
public class Main
{
 	public static void main (String[] args) {

 		if (args.length > 0) {
 			// create a server instance to accept clients connection
			if ("server".equals(args[0])) {
	            AbstractServer as = new FirstServer();
				String ip = "localhost";
				as.connect(ip);;
	        }
			
			// start a client instance and call its connect function
	        if ("client".equals(args[0])) {
	            SimpleClient c1 = new SimpleClient();
				c1.connect("localhost");		
	        }
	        
	        // launch a console thread to manipulate data on a server
	        if ("serverConsole".equals(args[0])) {
	        	new ConsoleThread().start();
	        }
	    }
	}
}