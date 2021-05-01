package musichub.business;

import java.io.*;
import java.util.*;
import javax.sound.sampled.*;
	
public class ClientHub {
	private List<Album> albums;
	private List<PlayList> playlists;
	private List<AudioElement> elements;
	private AudioInputStream audioStream;
    private Clip audioClip = null;
	
	public ClientHub (LinkedList<Album> albums, LinkedList<PlayList> playlists, LinkedList<AudioElement> elements) {
		this.albums = albums;
		this.playlists = playlists;
		this.elements = elements;
	}
	
	public List<Album> getAlbums() {
		return albums;
	}
	
	public void setElements(LinkedList<AudioElement> elements) {
		this.elements = elements;
	}
	
	public void setAlbums(LinkedList<Album> albums) {
		this.albums = albums;
	}
	
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
	
	public void playSound(InputStream in) {
		InputStream bufferedIn = new BufferedInputStream(in);
	 	try {
	 		audioStream = AudioSystem.getAudioInputStream(bufferedIn);
	        if (audioClip == null) {
	        	audioClip = AudioSystem.getClip();
	        	audioClip.addLineListener(new LineListener() {
	        	    public void update(LineEvent myLineEvent) {
	        	      if (myLineEvent.getType() == LineEvent.Type.STOP && audioClip.getMicrosecondPosition() == audioClip.getMicrosecondLength())
	        	    	  audioClip.close();
	        	    }
	        	  });
	        }
	        if (audioClip.isActive()) 
	        	audioClip.stop();
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
	
	/*public void addElementToAlbum(String elementTitle, String albumTitle) throws NoAlbumFoundException, NoElementFoundException
	{
		Album theAlbum = null;
		int i = 0;
		boolean found = false; 
		for (i = 0; i < albums.size(); i++) {
			if (albums.get(i).getTitle().toLowerCase().equals(albumTitle.toLowerCase())) {
				theAlbum = albums.get(i);
				found = true;
				break;
			}
		}

		if (found == true) {
			AudioElement theElement = null;
			for (AudioElement ae : elements) {
				if (ae.getTitle().toLowerCase().equals(elementTitle.toLowerCase())) {
					theElement = ae;
					break;
				}
			}
            if (theElement != null) {
                theAlbum.addSong(theElement.getUUID());
                //replace the album in the list
                albums.set(i,theAlbum);
            }
            else throw new NoElementFoundException("Element " + elementTitle + " not found!");
		}
		else throw new NoAlbumFoundException("Album " + albumTitle + " not found!");
		
	}
	
	public void addElementToPlayList(String elementTitle, String playListTitle) throws NoPlayListFoundException, NoElementFoundException
	{
		PlayList thePlaylist = null;
        int i = 0;
		boolean found = false; 
		
        for (i = 0; i < playlists.size(); i++) {
			if (playlists.get(i).getTitle().toLowerCase().equals(playListTitle.toLowerCase())) {
				thePlaylist = playlists.get(i);
				found = true;
				break;
			}
		}

		if (found == true) {
			AudioElement theElement = null;
			for (AudioElement ae : elements) {
				if (ae.getTitle().toLowerCase().equals(elementTitle.toLowerCase())) {
					theElement = ae;
					break;
				}
			}
            if (theElement != null) {
                thePlaylist.addElement(theElement.getUUID());
                //replace the album in the list
                playlists.set(i,thePlaylist);
            }
            else throw new NoElementFoundException("Element " + elementTitle + " not found!");
			
		} else throw new NoPlayListFoundException("Playlist " + playListTitle + " not found!");
		
	}
	
	private void loadAlbums () {
		NodeList albumNodes = xmlHandler.parseXMLFile(ALBUMS_FILE_PATH);
		if (albumNodes == null) return;
				
		for (int i = 0; i < albumNodes.getLength(); i++) {
			if (albumNodes.item(i).getNodeType() == Node.ELEMENT_NODE)   {
				Element albumElement = (Element) albumNodes.item(i);
				if (albumElement.getNodeName().equals("album")) 	{
					try {
						this.addAlbum(new Album (albumElement));
					} catch (Exception ex) {
						System.out.println ("Something is wrong with the XML album element");
					}
				}
			}  
		}
	}
	
	private void loadPlaylists () {
		NodeList playlistNodes = xmlHandler.parseXMLFile(PLAYLISTS_FILE_PATH);
		if (playlistNodes == null) return;
		
		for (int i = 0; i < playlistNodes.getLength(); i++) {
			if (playlistNodes.item(i).getNodeType() == Node.ELEMENT_NODE)   {
				Element playlistElement = (Element) playlistNodes.item(i);
				if (playlistElement.getNodeName().equals("playlist")) 	{
					try {
						this.addPlaylist(new PlayList (playlistElement));
					} catch (Exception ex) {
						System.out.println ("Something is wrong with the XML playlist element");
					}
				}
			}  
		}
	}
	
	private void loadElements () {
		NodeList audioelementsNodes = xmlHandler.parseXMLFile(ELEMENTS_FILE_PATH);
		if (audioelementsNodes == null) return;
		
		for (int i = 0; i < audioelementsNodes.getLength(); i++) {
			if (audioelementsNodes.item(i).getNodeType() == Node.ELEMENT_NODE)   {
				Element audioElement = (Element) audioelementsNodes.item(i);
				if (audioElement.getNodeName().equals("song")) 	{
					try {
						AudioElement newSong = new Song (audioElement);
						this.addElement(newSong);
					} catch (Exception ex) 	{
						System.out.println ("Something is wrong with the XML song element");
					}
				}
				if (audioElement.getNodeName().equals("audiobook")) 	{
					try {
						AudioElement newAudioBook = new AudioBook (audioElement);
						this.addElement(newAudioBook);
					} catch (Exception ex) 	{
						System.out.println ("Something is wrong with the XML audiobook element");
					}
				}
			}  
		}
	}


	public void saveAlbums () {
		Document document = xmlHandler.createXMLDocument();
		if (document == null) return;
		
		// root element
		Element root = document.createElement("albums");
		document.appendChild(root);

		//save all albums
		for (Iterator<Album> albumsIter = this.albums(); albumsIter.hasNext();) {
			Album currentAlbum = albumsIter.next();
			currentAlbum.createXMLElement(document, root);
		}
		xmlHandler.createXMLFile(document, ALBUMS_FILE_PATH);
	}
	
	public void savePlayLists () {
		Document document = xmlHandler.createXMLDocument();
		if (document == null) return;
		
		// root element
		Element root = document.createElement("playlists");
		document.appendChild(root);

		//save all playlists
		for (Iterator<PlayList> playlistsIter = this.playlists(); playlistsIter.hasNext();) {
			PlayList currentPlayList = playlistsIter.next();
			currentPlayList.createXMLElement(document, root);
		}
		xmlHandler.createXMLFile(document, PLAYLISTS_FILE_PATH);
	}
	
	public void saveElements() {
		Document document = xmlHandler.createXMLDocument();
		if (document == null) return;

		// root element
		Element root = document.createElement("elements");
		document.appendChild(root);

		//save all AudioElements
		Iterator<AudioElement> elementsIter = elements.listIterator(); 
		while (elementsIter.hasNext()) {
			
			AudioElement currentElement = elementsIter.next();
			if (currentElement instanceof Song)
			{
				((Song)currentElement).createXMLElement(document, root);
			}
			if (currentElement instanceof AudioBook)
			{ 
				((AudioBook)currentElement).createXMLElement(document, root);
			}
		}
		xmlHandler.createXMLFile(document, ELEMENTS_FILE_PATH);
 	}	
 	*/
}