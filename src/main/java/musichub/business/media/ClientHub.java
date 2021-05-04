package musichub.business.media;

import java.io.*;
import java.util.*;
import javax.sound.sampled.*;
import musichub.business.exception.*;

/**
 * Contain all the methods to manipulate the jMusicHub library on a client
 * @author Melissa Genovese, Jonathan Ozouf, Eden Loukakou-Bataille
 */
public class ClientHub implements LineListener {
	private List<Album> albums;
	private List<PlayList> playlists;
	private List<AudioElement> elements;
    private AudioInputStream audioStream;
    private Clip audioClip = null;
    private long trackPosition;
    private List<Clip> queue;
	
	public ClientHub (LinkedList<Album> albums, LinkedList<PlayList> playlists, LinkedList<AudioElement> elements) {
		this.albums = albums;
		this.playlists = playlists;
		this.elements = elements;
		queue = new LinkedList<Clip>();
	}
	
	/**
	 * Access the albums of the ClientHub
	 * @return list containing all the albums
	 * @author Jonathan Ozouf 
	 */
	public List<Album> getAlbums() {
		return albums;
	}
	
	/**
	 * Assign a list of Songs and AudioBooks to the ClientHub
	 * @param elements list of audio elements
	 * @author Jonathan Ozouf 
	 */
	public void setElements(LinkedList<AudioElement> elements) {
		this.elements = elements;
	}
	
	/**
	 * Assign a list of Albums to the ClientHub
	 * @param albums list of albums
	 * @author Jonathan Ozouf 
	 */
	public void setAlbums(LinkedList<Album> albums) {
		this.albums = albums;
	}
	
	/**
	 * Assign a list of PlayLists to the ClientHub
	 * @param playlists list of playlists
	 * @author Jonathan Ozouf 
	 */
	public void setPlaylists(LinkedList<PlayList> playlists) {
		this.playlists = playlists;
	}
	
	public Iterator<Album> albums() { 
		return albums.listIterator();
	}
	
	public Iterator<PlayList> playlists() { 
		return playlists.listIterator();
	}
	
	public Iterator<AudioElement> elements() { 
		return elements.listIterator();
	}
	
	public String getAlbumsTitlesSortedByDate() {
		StringBuffer titleList = new StringBuffer();
		Collections.sort(albums, new SortByDate());
		for (Album al : albums)
			titleList.append(al.getTitle()+ "\n");
		return titleList.toString();
	}
	
	public String getAudiobooksTitlesSortedByAuthor() {
		StringBuffer titleList = new StringBuffer();
		List<AudioElement> audioBookList = new ArrayList<AudioElement>();
		for (AudioElement ae : elements)
				if (ae instanceof AudioBook)
					audioBookList.add(ae);
		Collections.sort(audioBookList, new SortByAuthor());
		for (AudioElement ab : audioBookList)
			titleList.append(ab.getArtist()+ "\n");
		return titleList.toString();
	}

	public List<AudioElement> getAlbumSongs (String albumTitle) throws NoAlbumFoundException {
		Album theAlbum = null;
		ArrayList<AudioElement> songsInAlbum = new ArrayList<AudioElement>();
		for (Album al : albums) {
			if (al.getTitle().toLowerCase().equals(albumTitle.toLowerCase())) {
				theAlbum = al;
				break;
			}
		}
		if (theAlbum == null) throw new NoAlbumFoundException("No album with this title in the MusicHub!");

		List<UUID> songIDs = theAlbum.getSongs();
		for (UUID id : songIDs)
			for (AudioElement el : elements) {
				if (el instanceof Song) {
					if (el.getUUID().equals(id)) songsInAlbum.add(el);
				}
			}
		return songsInAlbum;		
	}
	
	public List<Song> getAlbumSongsSortedByGenre (String albumTitle) throws NoAlbumFoundException {
		Album theAlbum = null;
		ArrayList<Song> songsInAlbum = new ArrayList<Song>();
		for (Album al : albums) {
			if (al.getTitle().toLowerCase().equals(albumTitle.toLowerCase())) {
				theAlbum = al;
				break;
			}
		}
		if (theAlbum == null) throw new NoAlbumFoundException("No album with this title in the MusicHub!");

		List<UUID> songIDs = theAlbum.getSongs();
		for (UUID id : songIDs)
			for (AudioElement el : elements) {
				if (el instanceof Song) {
					if (el.getUUID().equals(id)) songsInAlbum.add((Song)el);
				}
			}
		Collections.sort(songsInAlbum, new SortByGenre());
		return songsInAlbum;		
		
	}
	
	public List<AudioElement> getPlaylistSongs (String plTitle) throws NoPlayListFoundException {
		PlayList pl = null;
		ArrayList<AudioElement> songsInPl = new ArrayList<AudioElement>();
		for (PlayList p : playlists) {
			if (p.getTitle().toLowerCase().equals(plTitle.toLowerCase())) {
				pl = p;
				break;
			}
		}
		if (pl == null) throw new NoPlayListFoundException("No playlist with this title in the MusicHub!");

		List<UUID> songIDs = pl.getElements();
		for (UUID id : songIDs)
			for (AudioElement el : elements) {
				if (el instanceof Song) {
					if (el.getUUID().equals(id)) songsInPl.add(el);
				}
			}
		return songsInPl;		
	}
	
	/**
	 * Check if an AudioElement is available
	 * @param elementTitle title of the song wanted 
	 * @author Jonathan Ozouf 
	 */
	public boolean findElement (String elementTitle) {
		AudioElement theElement = null;
		for (AudioElement ae : elements) {
			if (ae.getTitle().toLowerCase().equals(elementTitle.toLowerCase())) {
				theElement = ae;
				break;
			}
		}
        if (theElement != null) {
            return true;
        }
        else return false;
	}
	
	/**
	 * Play music in a Clip
	 * @param in InpoutStream with the content of a wav file
	 * @author Jonathan Ozouf
	 */
	public void startNewSound(InputStream in) {
		InputStream bufferedIn = new BufferedInputStream(in);
	 	try {
	  	    audioStream = AudioSystem.getAudioInputStream(bufferedIn);
	  	    // create the Clip if never done and make the ClientHub listen to the Line events
	  	    if (audioClip == null) {
	        	audioClip = AudioSystem.getClip();
	        	audioClip.addLineListener(this);
	  	    }
	  	    // close any previous line
			if (audioClip.isOpen()) 
				audioClip.close();

			audioClip.open(audioStream);
			audioClip.start();
			
	 	} catch (UnsupportedAudioFileException ex) {
            System.out.println("The specified audio file is not supported.");
            ex.printStackTrace();
        } catch (LineUnavailableException ex) {
	        System.out.println("Audio line for playing back is unavailable.");
	        ex.printStackTrace();
	    } catch (IOException ex) {
	        System.out.println("Error playing the audio file.");
	        ex.printStackTrace();
	    }    
	}
	
	/**
	 * Stop the music if it is playing or resume lecture if stopped
	 * @author Jonathan Ozouf
	 */
	public void playPauseSound() {
		if (audioClip != null) {
			if (audioClip.isOpen()) {
				if (audioClip.isActive()) {
					trackPosition = audioClip.getMicrosecondPosition();
					audioClip.stop();
				}
				else {
					audioClip.setMicrosecondPosition(trackPosition);
					audioClip.start();
				}	
			}
		}
	}
	
	/**
	 * Start the  next music clip from the queue and set the audioClip value to it
	 *@author Eden Loukakou-Bataille 
	 */
	public void playNext() {
		if (!queue.isEmpty()) {
		  	try {
		  		if (audioClip == null) {
		        	audioClip = AudioSystem.getClip();
		        }
		        if (audioClip.isOpen()) {
		        	audioClip.close();
		        	audioClip.removeLineListener(this);
		        }
		  	  	audioClip = queue.get(0);
		  	  	audioClip.addLineListener(this);
		  	  	audioClip.start();
		  	  	queue.remove(0);
			} catch (LineUnavailableException ex) {
		        System.out.println("Audio line for playing back is unavailable.");
		        ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Create a Clip from an audio file in an InputStream a add it in queue
	 * @param in in InpoutStream with the content of a wav file
	 * @author Eden Loukakou-Bataille 
	 */
	public void addSongToQueue(InputStream in) {
		InputStream bufferedIn = new BufferedInputStream(in);
	 	try {
	  	    audioStream = AudioSystem.getAudioInputStream(bufferedIn);
	  	    Clip newClip = AudioSystem.getClip();
	  	    newClip.open(audioStream);
	  	    queue.add(newClip);	 
	 	} catch (UnsupportedAudioFileException ex) {
            System.out.println("The specified audio file is not supported.");
            ex.printStackTrace();
        } catch (LineUnavailableException ex) {
	        System.out.println("Audio line for playing back is unavailable.");
	        ex.printStackTrace();
	    } catch (IOException ex) {
	        System.out.println("Error playing the audio file.");
	        ex.printStackTrace();
	    } 
	}
		
	
	/**
     * Listens to the START and STOP events of the audio line.
     */
    @Override
	public void update(LineEvent event) {
		LineEvent.Type type = event.getType();
		if (type == LineEvent.Type.STOP) {
			// log playback stopped
			if (audioClip.getMicrosecondPosition() == audioClip.getMicrosecondLength()) 
				playNext();
		} else if (type == LineEvent.Type.START) {
            //log playback started;
        }
    }

}