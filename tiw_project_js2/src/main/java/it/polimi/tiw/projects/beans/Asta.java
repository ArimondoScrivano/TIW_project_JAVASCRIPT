package it.polimi.tiw.projects.beans;

import java.sql.Timestamp;

public class Asta {
	private int idAsta;
	private Timestamp dateStart;
	private Timestamp dateExpiration;
	private int idCreator;
	private String state;
	private double currentPrice;
	private int minimumIncrease;
	
	public int getIdAsta() {
		return idAsta;
	}
	public void setIdAsta(int i) {
		this.idAsta = i;
	}
	public Timestamp getDateStart() {
		return dateStart;
	}
	public void setDateStart (Timestamp dateStart) {
		this.dateStart = dateStart;
	}
	public Timestamp getDateExpiration() {
		return dateExpiration;
	}
	public void setDateExpiration(Timestamp dateExpiration) {
		this.dateExpiration = dateExpiration;
	}
	public int getIdCreator() {
		return idCreator;
	}
	public void setIdCreator(int idCreator) {
		this.idCreator = idCreator;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public double getCurrentPrice() {
		return currentPrice;
	}
	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}
	public int getMinimumIncrease() {
		return minimumIncrease;
	}
	public void setMinimumIncrease(int minimumIncrease) {
		this.minimumIncrease = minimumIncrease;
	}

}
