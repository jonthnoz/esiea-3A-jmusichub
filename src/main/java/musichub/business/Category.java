package musichub.business;

/**
 * Possible values for the category of an AudioBook
 */
public enum Category {
	YOUTH ("youth"), NOVEL ("novel"), THEATER ("theater"), DOCUMENTARY ("documentary"), SPEECH("speech");
	
	private String category;
	
	private Category (String category) {
		this.category = category;
	}
	
	public String getCategory() {
		return category;
	}
}