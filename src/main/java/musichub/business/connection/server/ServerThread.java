package musichub.business.connection.server;

import java.io.*;
import java.net.*;
import musichub.business.media.*;
import musichub.business.connection.client.*;

/**
 * This thread is responsible to handle client connection.
 * @author Johanne Scemama, Jonathan Ozouf
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
    		
			//create the streams that will handle the objects coming through the sockets
    		input = new ObjectInputStream(socket.getInputStream());
			output = new ObjectOutputStream(socket.getOutputStream());
			
			//create the stream that will handle the audio files going through the sockets
			OutputStream out = socket.getOutputStream();
 
			output.writeObject(theHub.getAlbums());			//serialize and write the albums list object to the stream
			output.writeObject(theHub.getPlaylists());		//serialize and write the playlists list object to the stream
			output.writeObject(theHub.getElements());		//serialize and write the elements list object to the stream
			output.reset();
			
			boolean exit = false;
			
			while (!exit) {
				try {
					// read the request sent from the client
					request = (Integer) input.readObject();
					
					// answer audio stream request
					if (request.equals(SimpleClient.ASK_PLAY)) {
						// read title requested
						String songTitle = (String) input.readObject();
						// try to open it from the resources
						InputStream in = getClass().getClassLoader().getResourceAsStream("files/"+songTitle.toLowerCase().replaceAll("\\s+","_").replaceAll("'", "")+".wav");
						// handle file not found
						if (in == null) output.writeObject(NOT_FOUND_RESPONSE);
						else output.writeObject(OK_RESPONSE);
						// copy input stream from the file to the output stream of socket
						byte[] bytes = new byte[4096];
					    int count;
					    while ((count = in.read(bytes)) > 0) {
					        out.write(bytes, 0, count);
					    }
					    in.close();
					}
					
					// answer library refresh request
					else if (request.equals(SimpleClient.ASK_UPDATE)) {
						theHub.reloadAll();
						output.writeObject(theHub.getAlbums());			//serialize and write the albums list object to the stream
						output.writeObject(theHub.getPlaylists());		//serialize and write the playlists list object to the stream
						output.writeObject(theHub.getElements());		//serialize and write the elements list object to the stream
						output.reset();								// to be able to send updated versions of the objects later
					}
					
					// handle client disconnect
					else if (request.equals(SimpleClient.QUIT)) {
						exit = true;
						System.out.println("Bye");
					}
					
				} catch (EOFException | SocketException ex) {
					System.out.println("Connexion lost with client: ");
					ex.printStackTrace();
					exit = true;
				} catch (Exception ex) {
					System.out.println("Server exception: " + ex.getMessage());
					ex.printStackTrace();
				}
			}
 
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
			try {
				output.close();
				input.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
    }
}