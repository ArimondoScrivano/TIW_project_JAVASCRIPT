package it.polimi.tiw.projects.beans;

import java.sql.Timestamp;

public class User {
	private int idUtente;
	private String name;
	private String surname;
	private String username;
	private String shippingAddress;
	private Timestamp loginTime;

	public int getIdUtente() {
		return idUtente;
	}

	
	public void setidUtente(int i) {
		idUtente = i;
	}
	
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String un) {
		username = un;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}


	public Timestamp getLoginTime() {
		return loginTime;
	}


	public void setLoginTime(Timestamp loginTime) {
		this.loginTime = loginTime;
	}

}
