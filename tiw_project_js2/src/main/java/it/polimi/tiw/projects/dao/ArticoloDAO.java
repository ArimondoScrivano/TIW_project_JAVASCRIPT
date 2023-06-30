package it.polimi.tiw.projects.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import it.polimi.tiw.projects.beans.Articolo;
import it.polimi.tiw.projects.beans.User;

public class ArticoloDAO {
	private Connection con;

	public ArticoloDAO(Connection connection) {
		this.con = connection;
	}
	
	
	public void createArticolo(String name, String description, InputStream image, double price, String state, int idUtente) throws SQLException {
		String query = "INSERT into articolo (name, description, image, price, state, id_utente) VALUES(?, ?, ?, ?, ?, ?)";
		PreparedStatement pstatement = con.prepareStatement(query);
		pstatement.setString(1, name);
		pstatement.setString(2, description);
		pstatement.setBlob(3, image);
		pstatement.setDouble(4, price);
		pstatement.setString(5, state);
		pstatement.setInt(6, idUtente);
	    pstatement.executeUpdate();
	    System.out.println("Abbiamo creato un nuovo articolo");
	}
	
	public List<Articolo> getArticoliDisponibili(int idUtente)  throws SQLException  {
		List<Articolo> lista= new ArrayList<Articolo>();
		String query = "SELECT * FROM articolo  WHERE state = ? AND id_utente = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, "available");
			pstatement.setInt(2, idUtente);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					while(result.next()){
						Articolo nuovoArticolo= new Articolo();
						nuovoArticolo.setIdArticolo(result.getInt("idArticolo"));
						nuovoArticolo.setName(result.getString("name"));
						nuovoArticolo.setDescription(result.getString("description"));
						byte[] imgData = result.getBytes("image");
						String encodedImg= Base64.getEncoder().encodeToString(imgData);
						nuovoArticolo.setImageLink(encodedImg);
						nuovoArticolo.setPrice(result.getDouble("price"));
						nuovoArticolo.setState(result.getString("state"));
						nuovoArticolo.setidUtente(result.getInt("id_utente"));
						lista.add(nuovoArticolo);
					}
					return lista;
				}
				}
			}
	}
	
	
	public Articolo getArticoloById(int idArticolo) throws SQLException{
		String query = "SELECT * FROM articolo WHERE idArticolo = ?";
		PreparedStatement pstat = con.prepareStatement(query);
			pstat.setInt(1, idArticolo);
			ResultSet result = pstat.executeQuery();
			Articolo articolo = new Articolo();
			if (!result.isBeforeFirst()) // no results
					return null;
				else {
					result.next();
					articolo.setIdArticolo(result.getInt("idArticolo"));
					articolo.setDescription(result.getString("description"));
					articolo.setName(result.getString("name"));
					articolo.setState(result.getString("state"));
					byte[] imgData = result.getBytes("image");
					String encodedImg= Base64.getEncoder().encodeToString(imgData);
					articolo.setImageLink(encodedImg);
					articolo.setPrice(result.getDouble("price"));
					articolo.setidUtente(result.getInt("id_utente"));
					return articolo;
			}
	}
	
	//Dopo aver inserito un articolo in asta devo settare come non più disponibile
	public void setArticoloInAsta(int idArticolo) throws SQLException{
		String query = "UPDATE articolo SET state = ? WHERE idArticolo = ?";
		PreparedStatement pstat = con.prepareStatement(query);
			pstat.setString(1, "unavailable");
			pstat.setInt(2, idArticolo);
			try {
				pstat.executeUpdate();
				con.commit();
			}catch(SQLException e) {
				e.printStackTrace();
			}
	}
}
