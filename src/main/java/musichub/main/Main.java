package musichub.main;

import musichub.business.*;
	
public class Main
{
 	public static void main (String[] args) {

 		if (args.length > 0) {
			if ("server".equals(args[0])) {
	            AbstractServer as = new FirstServer();
				String ip = "localhost";
				as.connect(ip);;
	        }

	        if ("client".equals(args[0])) {
	            SimpleClient c1 = new SimpleClient();
				c1.connect("localhost");		
	        }
	        
	        if ("serverConsole".equals(args[0])) {
	        	new ConsoleThread().start();
	        }
	    }
	}
}