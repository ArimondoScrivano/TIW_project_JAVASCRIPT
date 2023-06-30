package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import it.polimi.tiw.projects.beans.User;

public class UserDAO {
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}

	//Checking the credentials
	public User checkCredentials(String usrn, String pwd) throws SQLException {
		//query "preparata" 
		String query = "SELECT idUtente,name,surname,username FROM utente  WHERE username = ? AND password =?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, usrn);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setidUtente(result.getInt("idUtente"));
					user.setUsername(result.getString("username"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					return user;
				}
			}
		}
	}
	
	//Getting the username and the shipping address
	public User getDetailsUtente(int idUtente) throws SQLException {
		String query = "SELECT * FROM utente WHERE idUtente = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idUtente);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results
					return null;
				else {
					result.next();
					User user = new User();
					user.setUsername(result.getString("username"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					user.setShippingAddress(result.getString("shippingAddress"));
					return user;
				}
			}
		}
	}
	
	public boolean checkNoDuplicate(String usrn, String name, String surname, String shippingAddress) throws SQLException{
		String query="SELECT * FROM utente WHERE username = ? AND name = ? AND surname = ? AND shippingAddress = ?";
		boolean check = false;
		try {
			PreparedStatement pstat = con.prepareStatement(query);
			pstat.setString(1, usrn);
			pstat.setString(2, name);
			pstat.setString(3, surname);
			pstat.setString(4, shippingAddress);
			try(ResultSet result = pstat.executeQuery();){
				if(!result.isBeforeFirst()) {
					//Non ci sono doppioni
					check=true;
				}else {
					check=false;
				}
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return check;
	}

	public User createUser(String usrn, String pwd, String name, String surname, String shippingAddress) throws SQLException {
		//Controllo che non ci siano duplicati tramite i parametri
		boolean check = checkNoDuplicate(usrn, name, surname, shippingAddress);
		if(check) {
			//No duplicati trovati
			User user = new User();
			user.setName(name);
			user.setShippingAddress(shippingAddress);
			user.setSurname(surname);
			user.setUsername(usrn);
			String query1 = "INSERT into utente (password, name, surname,shippingAddress, username) VALUES(?, ?, ?, ?, ?)";
			PreparedStatement pstatement1= null;
			
			pstatement1 = con.prepareStatement(query1);
			pstatement1.setString(1, pwd);
			pstatement1.setString(2, name);
			pstatement1.setString(3, surname);
			pstatement1.setString(4, shippingAddress);
			pstatement1.setString(5, usrn);
			pstatement1.executeUpdate();
			return user;
		}else {
			return null;
		}
	}
}
