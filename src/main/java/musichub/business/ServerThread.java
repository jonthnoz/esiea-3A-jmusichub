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
 
    public ServerThread(Socket socket) {
        this.socket = socket;
    }
 
    public void run() {
        try {
        	MusicHub theHub = new MusicHub ();
    		   		
      		//String choice = null;
    		//String albumTitle = null;
    		
			//create the streams that will handle the objects coming through the sockets
    		input = new ObjectInputStream(socket.getInputStream());
			output = new ObjectOutputStream(socket.getOutputStream());
 
			output.writeObject(theHub.getAlbums());			//serialize and write the albums list object to the stream*/
			output.writeObject(theHub.getPlaylists());		//serialize and write the playlists list object to the stream*/
			output.writeObject(theHub.getElements());		//serialize and write the elements list object to the stream*/
			
 
 			/*String text = (String)input.readObject();  //read the object received through the stream and deserialize it
			System.out.println("server received a text:" + text);
			
			Student student = new Student(1234, "john.doe");
			output.writeObject(student);		//serialize and write the Student object to the stream*/
 
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