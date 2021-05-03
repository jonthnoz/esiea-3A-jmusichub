package musichub.business;

/**
 * Values possible for the genre of a song
 */
public enum Genre {
	JAZZ ("jazz"), CLASSIC ("classic"), HIPHOP ("hiphop"), ROCK ("rock"), POP("pop"), RAP("rap");
	
	private String genre;
	
	private Genre (String genre) {
		this.genre = genre;
	}
	
	public String getGenre() {
		return genre;
	}
}