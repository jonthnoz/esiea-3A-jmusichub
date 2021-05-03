package musichub.business;

import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.LineEvent;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class ClientHubTest {
	
	private MusicHub testHub;
	private ClientHub testClientHub;
	
	@BeforeEach                                         
    public void setUp() throws Exception {
		testHub = new MusicHub();
		testClientHub = new ClientHub((LinkedList<Album>) testHub.getAlbums(), (LinkedList<PlayList>) testHub.getPlaylists(), (LinkedList<AudioElement>) testHub.getElements());
    }

    @Test                                               
    public void getAlbumsTest() {
        assertEquals(testHub.getAlbums(), testClientHub.getAlbums());              
    }
    
    /*
    @Test
    @DisplayName("When we add an album it should be at the end of the album list")   
    private void addAlbum(Album album) {
    	
    }
    */
    
    @Test
    @DisplayName("Ensure the albums are well sorted by date")
    public void getAlbumsTitlesSortedByDateTest() {
    	LinkedList<Album> al = new LinkedList<Album>();
    	al.add( new Album("Album2", "testArtist", 10, "0001-01-02"));
    	al.add( new Album("Album3", "testArtist", 10, "1978-01-01"));
    	al.add( new Album("firstAlbum", "testArtist", 10, "0001-01-01"));
    	testClientHub.setAlbums(al);
    	
    	String result = testClientHub.getAlbumsTitlesSortedByDate();
    	String expectedResult = "firstAlbum\nAlbum2\nAlbum3\n";
    	assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("Ensure the audiobooks are well sorted by author")
    public void getAudiobooksTitlesSortedByAuthorTest() {
    	LinkedList<AudioElement> ab = new LinkedList<AudioElement>();
    	ab.add( new AudioBook("Book2", "Michel", 10, "book2.mp3", "italian", "youth"));
    	ab.add( new AudioBook("Book3", "marc", 10, "book3.mp3", "french", "novel"));
    	ab.add( new AudioBook("firstBook", "Marc", 10, "book1.mp3", "english", "theater"));
    	ab.add( new Song("not a book", "artist", 10, "song.mp3", "pop"));
    	testClientHub.setElements(ab);
    	
    	String result = testClientHub.getAudiobooksTitlesSortedByAuthor();
    	String expectedResult = "Marc\nMichel\nmarc\n";
    	assertEquals(expectedResult, result);
    }

    @Test
    public void findElementTest() {
    	LinkedList<AudioElement> ae = new LinkedList<AudioElement>();
    	ae.add( new Song("songA", "artist", 10, "songA.mp3", "pop"));
    	ae.add( new Song("songB", "artist", 10, "songB.mp3", "rock"));
    	ae.add( new Song("Song to Find", "artist", 10, "songC.mp3", "pop"));
    	ae.add( new Song("songD", "artist", 10, "songD.mp3", "classic"));
    	ae.add( new AudioBook("firstBook", "auteur", 10, "book1.mp3", "english", "theater"));
    	testClientHub.setElements(ae);
    	
    	assertAll(
    	        () -> assertEquals(true, testClientHub.findElement("song to find")),
    	        () -> assertEquals(false, testClientHub.findElement("absent song"))
    		);
    }
    
}
