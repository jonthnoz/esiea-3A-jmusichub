package musichub.business;
import java.io.*;  
import java.net.*; 
import java.util.*;

public class SimpleClient {
	
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private InputStream in;
	private Socket socket;
	private ClientHub theHub;
	public static final Integer ASK_PLAY = 0;
	public static final Integer ASK_UPDATE = 1;
	public static final Integer QUIT = 2;

	public void connect(String ip) //throws CommunicationErrorException
	{
		int port = 6666;
        try  {
			//create the socket; it is defined by an remote IP address (the address of the server) and a port number
			socket = new Socket(ip, port);

			//create the streams that will handle the objects coming and going through the sockets
			output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            
			in = socket.getInputStream();
    		
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
    				case 's':
    					// play a song from list of existing songs
    					System.out.println("Type the name of the song you wish to listen. Available songs: ");
    					Iterator<AudioElement> itae = theHub.elements();
    					while (itae.hasNext()) {
    						AudioElement ae = itae.next();
    						if ( ae instanceof Song) System.out.println(ae.getTitle());
    					}
    					String songTitle = scan.nextLine();
    					if (theHub.findElement(songTitle)) 
    					{ 
    						output.writeObject(ASK_PLAY); 
    						output.writeObject(songTitle); 
    						output.reset();
    						Integer response = (Integer) input.readObject();
    						if (response.equals(ServerThread.OK_RESPONSE)) {
    					        System.out.println("stream received"); // log
    					        theHub.startNewSound(in);
    						} 
    						else System.out.println ("File not found on server!");
    					}
    					else System.out.println ("Song " + songTitle + " not found!");
    					printAvailableCommands();
                        choice = scan.nextLine();
                    break;
    				case 'p' :
    					// pause/play music
    					theHub.playPauseSound();
    					printAvailableCommands();
                        choice = scan.nextLine();
    				break;
    				default:
    				
    				break;
    			}
    		}
    		scan.close();     
    		output.writeObject(QUIT);
            
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
				in.close();
				input.close();
				output.close();
				socket.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	private static void printAvailableCommands() {
		System.out.println("\n");
		System.out.println("t: display the album titles, ordered by date");
		System.out.println("g: display songs of an album, ordered by genre");
		System.out.println("d: display songs of an album");
		System.out.println("l: display audiobooks ordered by author");
		//System.out.println("u: update elements, albums, playlists");
		System.out.println("s: select a song to play");
		System.out.println("p: play/pause the current track");

		//System.out.println("a: add a song to queue");
		System.out.println("q: quit program");
	}
}