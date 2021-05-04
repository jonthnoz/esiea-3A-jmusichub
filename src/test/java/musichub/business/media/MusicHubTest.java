package musichub.business.media;

import java.util.List;
import musichub.business.exception.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class MusicHubTest {
	private MusicHub testHub = new MusicHub();
	
	
    @Test
    @DisplayName("When we add an album it should be at the end of the album list")   
    public void addAlbumTest() {
    	Album al = new Album("testAlbum", "testArtist", 10, "2000-01-02");
    	testHub.addAlbum(al);
    	
    	List<Album> result = testHub.getAlbums();
    	assertEquals(al, result.get(result.size() - 1));
    }
    
    @Test
    @DisplayName("When we add an audiobook and a song they should be at the end of the elments list")   
    public void addElementTest() {
    	AudioBook ab = new AudioBook("testBook", "testArtist", 10, "book.mp3", "english", "theater");
    	testHub.addElement(ab);
    	Song song = new Song("testSong", "testArtist", 10, "song.mp3", "pop");
    	testHub.addElement(song);
    	
    	List<AudioElement> result = testHub.getElements();
    	assertAll(
    	        () -> assertEquals(ab, result.get(result.size() - 2)),
    	        () -> assertEquals(song, result.get(result.size() - 1))
    		);
    }
    
    @Test
    @DisplayName("When we add a playlist it should be at the end of the playlists list")   
    public void addPlaylistTest() {
    	PlayList pl = new PlayList("testPlaylists");
    	testHub.addPlaylist(pl);
    	
    	List<PlayList> result = testHub.getPlaylists();
    	assertEquals(pl, result.get(result.size() - 1));
    }
    
    @Test
    @DisplayName("When we delete a playlist the number of playlists should decrease")   
    public void deletePlaylistTest() {
    	int i = testHub.getPlaylists().size();
    	try {	
    		testHub.deletePlayList("my favourite songs");	// the initial xml in test resources contain 2 playlists
        	assertEquals(i - 1, testHub.getPlaylists().size());
    	} catch (NoPlayListFoundException ex){
    		fail("unexpected exception thrown");
    	}   	
    }
    
    @Test
    @DisplayName("When we add a song to a new album it should contain 1 song")
    public void addElementToAlbumOkTest() {
    	Album al = new Album("testAlbum", "testArtist", 10, "2000-01-02");
    	testHub.addAlbum(al);
    	Song song = new Song("testSong", "testArtist", 10, "song.mp3", "pop");
    	testHub.addElement(song);
   
    	try {	
	    	testHub.addElementToAlbum("testSong", "testAlbum");
	    	List<Song> result = testHub.getAlbumSongsSortedByGenre("testAlbum");
	    	
	    	assertEquals(1, result.size());
    	} catch (NoAlbumFoundException | NoElementFoundException ex){
    		fail("unexpected exception thrown");
    	}
    }
    
    @Test
    @DisplayName("When we try to add a song to a non existing album it should raise a NoAlbumFoundException")
    public void addElementToAlbumNoAlbumTest() {
    	//Album al = new Album("testAlbum", "testArtist", 10, "2000-01-02");
    	//testHub.addAlbum(al);
    	Song song = new Song("testSong", "testArtist", 10, "song.mp3", "pop");
    	testHub.addElement(song);
   
    	Throwable exception = assertThrows(NoAlbumFoundException.class, () -> testHub.addElementToAlbum("testSong", "testAlbum"));
        assertEquals("Album testAlbum not found!", exception.getMessage());
    }
    
    @Test
    @DisplayName("When we try to add a non existing song to an album it should raise a NoElementFoundException")
    public void addElementToAlbumNoSongTest() {
    	Album al = new Album("testAlbum", "testArtist", 10, "2000-01-02");
    	testHub.addAlbum(al);
    	//Song song = new Song("testSong", "testArtist", 10, "song.mp3", "pop");
    	//testHub.addElement(song);
   
    	Throwable exception = assertThrows(NoElementFoundException.class, () -> testHub.addElementToAlbum("testSong", "testAlbum"));
        assertEquals("Element testSong not found!", exception.getMessage());
    }
    
    @Test
    @DisplayName("When we add a song to a new playlist it should contain 1 song")
    public void addElementToPlaylistOkTest() {
    	PlayList pl = new PlayList("testPl");
    	testHub.addPlaylist(pl);
    	Song song = new Song("testSong", "testArtist", 10, "song.mp3", "pop");
    	testHub.addElement(song);
   
    	try {
    		testHub.addElementToPlayList("testSong", "testPl");
    	} catch (NoPlayListFoundException | NoElementFoundException ex) {
    		fail("unexpected exception thrown");
    	}
    	
    	List<PlayList> result = testHub.getPlaylists();
    	assertEquals(1, result.get(result.size() - 1).getElements().size());
    }
    
    @Test
    @DisplayName("When we try to add a song to a non existing playlist it should raise a NoPlayListFoundException")
    public void addElementToPlaylistNoPlaylistTest() {
    	Song song = new Song("testSong", "testArtist", 10, "song.mp3", "pop");
    	testHub.addElement(song);
   
    	Throwable exception = assertThrows(NoPlayListFoundException.class, () -> testHub.addElementToPlayList("testSong", "testPl"));
        assertEquals("Playlist testPl not found!", exception.getMessage());
    }
    
    @Test
    @DisplayName("When we try to add a non existing song to an album it should raise a NoElementFoundException")
    public void addElementToPlaylistNoSongTest() {
    	PlayList pl = new PlayList("testPl");
    	testHub.addPlaylist(pl);
   
    	Throwable exception = assertThrows(NoElementFoundException.class, () -> testHub.addElementToPlayList("testSong", "testPl"));
        assertEquals("Element testSong not found!", exception.getMessage());
    }
    
}
