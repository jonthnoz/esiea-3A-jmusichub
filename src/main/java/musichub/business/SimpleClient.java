package musichub.business;
import java.io.*;  
import java.net.*; 
import java.util.LinkedList;

public class SimpleClient {
	
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket socket;
	private ClientHub theHub;
	
	public void connect(String ip) //throws CommunicationErrorException
	{
		int port = 6666;
        try  {
			//create the socket; it is defined by an remote IP address (the address of the server) and a port number
			socket = new Socket(ip, port);

			//create the streams that will handle the objects coming and going through the sockets
			output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            
            theHub = new ClientHub((LinkedList<Album>) input.readObject(), (LinkedList<PlayList>) input.readObject(), (LinkedList<AudioElement>) input.readObject());
			
            System.out.println(theHub.getAlbumsTitlesSortedByDate());
			/*String textToSend = new String("send me the student info!");
			System.out.println("text sent to the server: " + textToSend);			
			output.writeObject(textToSend);		//serialize and write the String to the stream
 
			Student student = (Student) input.readObject();	//deserialize and read the Student object from the stream
			System.out.println("Received student id: " + student.getID() + " and student name:" + student.getName() + " from server");*/
	    } catch  (UnknownHostException uhe) {
			uhe.printStackTrace();
		}
		catch  (IOException ioe) {
			ioe.printStackTrace();
		}
		catch  (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
		finally {
			try {
				input.close();
				output.close();
				socket.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}