package musichub.business.media;

/**
 * Values possible for the language of an audio book
 */
public enum Language {
	FRENCH ("french"), ENGLISH ("english"), ITALIAN ("italian"), SPANISH ("spanish"), GERMAN("german");
	
	private String language;
	
	private Language (String language) {
		this.language = language;
	}
	
	public String getLanguage() {
		return language;
	}
}