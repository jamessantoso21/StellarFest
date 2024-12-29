package models;

public class VendorProduct {
	private String name;
	private String description;
	
	public VendorProduct(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
}
