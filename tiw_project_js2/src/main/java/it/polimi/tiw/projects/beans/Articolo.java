package it.polimi.tiw.projects.beans;

public class Articolo {
	private int idArticolo;
	private String name;
	private String description;
	private String imageLink;
	private Double price;
	private String state;
	private int idUtente;
	
	public int getIdArticolo() {
		return idArticolo;
	}
	public void setIdArticolo(int i) {
		this.idArticolo = i;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImageLink() {
		return imageLink;
	}
	public void setImageLink(String imageLink) {
		this.imageLink = imageLink;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	public int getidUtente() {
		return idUtente;
	}
	public void setidUtente(int idUtente) {
		this.idUtente = idUtente;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}

}
