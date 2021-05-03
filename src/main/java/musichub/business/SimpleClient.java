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

	public void connect(String ip) 
	{
		int port = 6666;
        try  {
			//create the socket; it is defined by an remote IP address (the address of the server) and a port number
			socket = new Socket(ip, port);

			//create the streams that will handle the objects coming and going through the sockets
			output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            
            //creat the stream that will handle the audio stream coming through the sockets
			in = socket.getInputStream();
    		
			// create the music manager with lists of albums, playlists and elements from the server
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
    				case 'u':
    					//load latest albums, playlists, elements
    					output.writeObject(ASK_UPDATE);
    					theHub.setAlbums((LinkedList<Album>) input.readObject());
    					theHub.setPlaylists((LinkedList<PlayList>) input.readObject());
    					theHub.setElements((LinkedList<AudioElement>) input.readObject());
    					System.out.println("Elements, albums and playlists are up to date!");
    					printAvailableCommands();
    					choice = scan.nextLine();
    				break;
    				case 's':
    					// play or queue a song from list of existing songs
    					System.out.println("Type the name of the song you wish to listen. Available songs: ");
    					// select a song
    					Iterator<AudioElement> itae = theHub.elements();
    					while (itae.hasNext()) {
    						AudioElement ae = itae.next();
    						if ( ae instanceof Song) System.out.println(ae.getTitle());
    					}
    					String songTitle = scan.nextLine();
    					if (theHub.findElement(songTitle)) 
    					{ 	
    						// retrieve it from server
    						output.writeObject(ASK_PLAY); 
    						output.writeObject(songTitle); 
    						output.reset();
    						Integer response = (Integer) input.readObject();
    						if (response.equals(ServerThread.OK_RESPONSE)) {
    					        System.out.println("stream received"); // log
    					        System.out.println("Type f to add '" + songTitle + "' to the queue or x to start it");
        						String command = scan.nextLine();
        						// select play now or queue
        						if (command.length() > 0 && command.charAt(0) == 'f') 
        								theHub.addSongToQueue(in);
        						else 
        						theHub.startNewSound(in);
    						} 
    						else System.out.println ("File not found on server!");
							
    					}
    					else System.out.println ("Song " + songTitle + " not found!");
    					printAvailableCommands();
                        choice = scan.nextLine();
                    break;
    				case 'a' :
    					//  add songs of an album to queue
    					System.out.println("Which album do you want to listen? Available albums:");
    					System.out.println(theHub.getAlbumsTitlesSortedByDate());    					
    					albumTitle = scan.nextLine();
    					try {
    						for (AudioElement el : theHub.getAlbumSongs(albumTitle)) {
    							output.writeObject(ASK_PLAY); 
        						output.writeObject(el.getTitle()); 
        						output.reset();
        						Integer response = (Integer) input.readObject();
        						if (response.equals(ServerThread.OK_RESPONSE)) {
        					        //System.out.println("stream received"); // log		 
            						theHub.addSongToQueue(in);           						
        						} 
        						else System.out.println ("File not found on server : " + el.getTitle());
    						}
    						System.out.println("Added to queue");
    					} catch (NoAlbumFoundException ex) {
    						System.out.println("No album found with the requested title " + ex.getMessage());
    					}
    					printAvailableCommands();
                        choice = scan.nextLine();
    				break;
    				case 'j' :
    					// add songs of a playlist to queue
    					System.out.println("Which playlist do you want to listen? Available playlists:");
    					Iterator<PlayList> itp = theHub.playlists();
    					while (itp.hasNext()) {
    						PlayList p = itp.next();
    						System.out.println(p.getTitle());
    					}
    					String plTitle = scan.nextLine();    					
    					try {
    						for (AudioElement el : theHub.getPlaylistSongs(plTitle)) {
	    							output.writeObject(ASK_PLAY); 
	        						output.writeObject(el.getTitle()); 
	        						output.reset();
	        						Integer response = (Integer) input.readObject();
	        						if (response.equals(ServerThread.OK_RESPONSE)) {
	        					        //System.out.println("stream received"); // log		 
	            						theHub.addSongToQueue(in);           						
	        						} 
	        						else System.out.println ("File not found on server : " + el.getTitle());
    						}
    						System.out.println("Added to queue");
    					} catch (NoPlayListFoundException ex) {
    						System.out.println("No album found with the requested title " + ex.getMessage());
    					}
    					printAvailableCommands();
                        choice = scan.nextLine();
    				break;
    				case 'p' :
    					// pause/play music
    					theHub.playPauseSound();
    					printAvailableCommands();
                        choice = scan.nextLine();
    				break;
    				case 'n' :
    					// play next music
    					theHub.playNext();
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
		System.out.println("s: select a song to play");
		System.out.println("a: select an album to play");
		System.out.println("j: select a playlist to play");
		System.out.println("p: play/pause the current track");
		System.out.println("n: play the next song in the queue");
		System.out.println("u: update elements, albums, playlists");
		System.out.println("q: quit program");
	}
}