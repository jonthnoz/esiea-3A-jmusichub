package musichub.business;
import java.io.*;  
import java.net.*; 
import java.util.*;

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
			
    		System.out.println("Type h for available commands");
            
            Scanner scan = new Scanner(System.in);
    		String choice = scan.nextLine();
    		
    		String albumTitle = null;
    		    		
    		while (choice.length() > 0 && choice.charAt(0)!= 'q') 	{
    			switch (choice.charAt(0)) 	{
    				case 'h':
    					printAvailableCommands();
    					choice = scan.nextLine();
    				break;
    				case 't':
    					//album titles, ordered by date
    					System.out.println(theHub.getAlbumsTitlesSortedByDate());
    					printAvailableCommands();
    					choice = scan.nextLine();
    				break;
    				case 'g':
    					//songs of an album, sorted by genre
    					System.out.println("Songs of an album sorted by genre will be displayed; enter the album name, available albums are:");
    					System.out.println(theHub.getAlbumsTitlesSortedByDate());
    					
    					albumTitle = scan.nextLine();
    					try {
    						System.out.println(theHub.getAlbumSongsSortedByGenre(albumTitle));
    					} catch (NoAlbumFoundException ex) {
    						System.out.println("No album found with the requested title " + ex.getMessage());
    					}
    					printAvailableCommands();
    					choice = scan.nextLine();
    				break;
    				case 'd':
    					//songs of an album
    					System.out.println("Songs of an album will be displayed; enter the album name, available albums are:");
    					System.out.println(theHub.getAlbumsTitlesSortedByDate());
    					
    					albumTitle = scan.nextLine();
    					try {
    						System.out.println(theHub.getAlbumSongs(albumTitle));
    					} catch (NoAlbumFoundException ex) {
    						System.out.println("No album found with the requested title " + ex.getMessage());
    					}
    					printAvailableCommands();
    					choice = scan.nextLine();
    				break;
    				case 'l':
    					//audiobooks ordered by author
    					System.out.println(theHub.getAudiobooksTitlesSortedByAuthor());
    					printAvailableCommands();
    					choice = scan.nextLine();
    				break;
    				/*case 'u':
    					//save elements, albums, playlists
    					theHub.saveElements();
    					theHub.saveAlbums();
    					theHub.savePlayLists();
    					System.out.println("Elements, albums and playlists saved!");
    					printAvailableCommands();
    					choice = scan.nextLine();
    				break;*/
    				default:
    				
    				break;
    			}
    		}
    		scan.close();
    	
            
            
            
            
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

	private static void printAvailableCommands() {
		System.out.println("t: display the album titles, ordered by date");
		System.out.println("g: display songs of an album, ordered by genre");
		System.out.println("d: display songs of an album");
		System.out.println("l: display audiobooks ordered by author");
		//System.out.println("u: save elements, albums, playlists");
		System.out.println("q: quit program");
	}
}