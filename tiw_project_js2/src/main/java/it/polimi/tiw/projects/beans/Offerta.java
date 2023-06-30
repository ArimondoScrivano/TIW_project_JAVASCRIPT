package it.polimi.tiw.projects.beans;

import java.sql.Timestamp;

public class Offerta {
	private int idOfferta;
	private int idUtente;
	private double offerPrice; //offer_price
	private Timestamp dateHour;
	private int idAsta;
	
	public int getIdOfferta() {
		return idOfferta;
	}
	public void setIdOfferta(int id) {
		this.idOfferta = id;
	}
	public int getidUtente() {
		return idUtente;
	}
	public void setidUtente(int userId) {
		this.idUtente = userId;
	}
	public double getOfferPrice() {
		return offerPrice;
	}
	public void setOfferPrice(double offerPrice) {
		this.offerPrice = offerPrice;
	}
	public Timestamp getDateHour() {
		return dateHour;
	}
	public void setDateHour(Timestamp dateHour) {
		this.dateHour = dateHour;
	}
	public int getIdAsta() {
		return idAsta;
	}
	public void setIdAsta(int idAsta) {
		this.idAsta = idAsta;
	}


}
