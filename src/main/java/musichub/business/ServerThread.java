package musichub.business;

import java.io.*;
import java.net.*;
 
/**
 * This thread is responsible to handle client connection.
 */
public class ServerThread extends Thread {
    private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	public static final Integer OK_RESPONSE = 200;
	public static final Integer NOT_FOUND_RESPONSE = 404;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }
 
    public void run() {
        try {
        	MusicHub theHub = new MusicHub ();
    		  
        	Integer request;
      		//String choice = null;
    		//String albumTitle = null;
    		
			//create the streams that will handle the objects coming through the sockets
    		input = new ObjectInputStream(socket.getInputStream());
			output = new ObjectOutputStream(socket.getOutputStream());
			
			OutputStream out = socket.getOutputStream();
 
			output.writeObject(theHub.getAlbums());			//serialize and write the albums list object to the stream*/
			output.writeObject(theHub.getPlaylists());		//serialize and write the playlists list object to the stream*/
			output.writeObject(theHub.getElements());		//serialize and write the elements list object to the stream*/
			
			boolean exit = false;
			while (!exit) {
				try {
					request = (Integer) input.readObject();
					if (request.equals(SimpleClient.ASK_PLAY)) {
						String songTitle = (String) input.readObject();
						InputStream in = getClass().getClassLoader().getResourceAsStream("files/"+songTitle.toLowerCase().replaceAll("\\s+","_").replaceAll("'", "")+".wav");
						if (in == null) output.writeObject(NOT_FOUND_RESPONSE);
						else output.writeObject(OK_RESPONSE);
						byte[] bytes = new byte[4096];
					    int count;
					    while ((count = in.read(bytes)) > 0) {
					        out.write(bytes, 0, count);
					    }
					    in.close();
					}
					//if (request.equals(SimpleClient.ASK_UPDATE)) {
						
					//}
					if (request.equals(SimpleClient.QUIT)) {
						exit = true;
						System.out.println("Bye");
					}
				} catch (EOFException eofe) {
					System.out.println("Connexion lost with client: ");
					eofe.printStackTrace();
					exit = true;
				} catch (Exception ex) {
					System.out.println("Server exception: " + ex.getMessage());
					ex.printStackTrace();
				}
			}
 
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();

		}/*   catch (ClassNotFoundException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }*/ finally {
			try {
				output.close();
				input.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
    }
}