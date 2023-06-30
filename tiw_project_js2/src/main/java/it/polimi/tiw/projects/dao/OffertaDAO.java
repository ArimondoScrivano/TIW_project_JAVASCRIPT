package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Articolo;
import it.polimi.tiw.projects.beans.Asta;
import it.polimi.tiw.projects.beans.Offerta;

public class OffertaDAO{
	private Connection con;

	public OffertaDAO(Connection connection) {
		this.con = connection;
	}
	
	public List<Offerta>  getListOffertebyId(int idAsta) throws SQLException{
	List<Offerta> offerteAsta = new ArrayList<>();
	String query = "SELECT * FROM offerta WHERE id_asta = ? ORDER BY date_hour_offer  DESC";
	
	try (PreparedStatement pstatement = con.prepareStatement(query)){
		pstatement.setInt(1, idAsta);
	try (ResultSet result = pstatement.executeQuery()) {
		
		if (!result.isBeforeFirst()) // no results, credential check failed
			return null;
		else {
			while(result.next()){
				Offerta nuovaOfferta= new Offerta();
				nuovaOfferta.setIdOfferta(result.getInt("idOfferta"));
				nuovaOfferta.setidUtente(result.getInt("idUtente"));
				nuovaOfferta.setOfferPrice(result.getDouble("offer_price"));
				nuovaOfferta.setDateHour(result.getTimestamp("date_hour_offer"));
				nuovaOfferta.setIdAsta(result.getInt("id_asta"));
				offerteAsta.add(nuovaOfferta);
			}
		return offerteAsta;
		}
						}
	
					}
				}
	
	public void createNewOfferta(int idAsta, int idUtente, double offerPrice, Timestamp time) throws SQLException{
		String query1 = "INSERT into offerta (idUtente, offer_price, date_hour_offer,id_asta) VALUES( ?, ?, ?, ?)";
		con.setAutoCommit(false); //disabilitiamo l'autocommit;
		PreparedStatement pstatement1= null;
		try {
			pstatement1= con.prepareStatement(query1);
			pstatement1.setInt(1,idUtente);
			pstatement1.setDouble(2,offerPrice);
			pstatement1.setTimestamp(3,time);
			pstatement1.setInt(4,idAsta);
			pstatement1.executeUpdate();
			
			AstaDAO AstaDAO= new AstaDAO(con);
			AstaDAO.updatePrezzoCorrente(idAsta, offerPrice);
			con.commit();
		}catch (SQLException e) {
			//con.rollback();
			e.printStackTrace();
			throw e;
			
		}finally {
			con.setAutoCommit(true);
		}
		
		
		
		
	}
	
	
	public int findidUtenteMaxOfferta(int idAsta) throws SQLException{
		String query = "SELECT idUtente FROM offerta WHERE offer_price = (SELECT MAX(offer_price) FROM offerta WHERE id_asta= ?)";
		try (PreparedStatement pstatement = con.prepareStatement(query)){
			pstatement.setInt(1, idAsta);
			try (ResultSet result = pstatement.executeQuery()) {
			
				if (!result.isBeforeFirst()) // no results, credential check failed
					return 0;
				
				else {
					result.next();
					return result.getInt("idUtente");
				}
	
			}
		}
		
		
	}
	
	
	public List<Asta> getAsteVinte(List<Asta> AstePotVinte, int idUtente) throws SQLException{
		List<Asta> AsteVinte= new ArrayList<>();
		for(int i=0; i<AstePotVinte.size(); i++ ) {
			if(findidUtenteMaxOfferta(AstePotVinte.get(i).getIdAsta())== idUtente) {
				AsteVinte.add(AstePotVinte.get(i));
			}
		}
		return AsteVinte;
		
	}
	
	
	
	
}

	

	
	
	
	
	
	
