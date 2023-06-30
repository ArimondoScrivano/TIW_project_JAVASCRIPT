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

public class AstaDAO {
	private Connection con;

	public AstaDAO(Connection connection) {
		this.con = connection;
	}
	
	public int findIdMaxAsta() throws SQLException {
		String query = "SELECT MAX(idAsta) FROM asta";
		PreparedStatement pstatement= null;
		int idAstaRes = 0;
		try {
			pstatement = con.prepareStatement(query);
			ResultSet result = pstatement.executeQuery();
			
			if(result.isBeforeFirst()) {
				result.next();
				idAstaRes=result.getInt(1)+1;
			}else {
				//Non trovo un risultato quindi prima asta creata
				idAstaRes=1;
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return idAstaRes;
	}
	
	public Asta findAstaById(int idAsta) throws SQLException {
		String query = "SELECT * FROM asta WHERE idAsta = ?";
		PreparedStatement pstat = con.prepareStatement(query);
		pstat.setInt(1, idAsta);
		ResultSet result = pstat.executeQuery();
		Asta a = new Asta();
		if(result.isBeforeFirst()) {
			result.next();
			a.setIdAsta(result.getInt("idAsta"));
			a.setDateStart(result.getTimestamp("Date_start"));
			a.setDateExpiration(result.getTimestamp("Date_Expiration"));
			a.setIdCreator(result.getInt("id_creator"));
			a.setCurrentPrice(result.getDouble("Current_price"));
			a.setState(result.getString("state"));
			a.setMinimumIncrease(result.getInt("Minimum_increase"));
			return a;
		}else {
			return null;
		}
	}
	
	
	public double getPrezzoCorrente(int idAsta) throws SQLException{
		String query = "SELECT Current_price FROM asta WHERE idAsta = ?";
		PreparedStatement pstat = con.prepareStatement(query);
		pstat.setInt(1, idAsta);
		ResultSet result = pstat.executeQuery();
		double price;
		if(!result.isBeforeFirst()) {
			price = result.getDouble("Current_price");
			return price;
		}else {
			return 0;
		}
	}
	
	
	
	public void updatePrezzoCorrente(int idAsta, double newPrezzo) throws SQLException {
		String query = "UPDATE asta SET Current_price = ? WHERE idAsta = ?";
		PreparedStatement pstat = con.prepareStatement(query);
		pstat.setDouble(1, newPrezzo);
		pstat.setInt(2, idAsta);
		pstat.executeUpdate();
		
	}
	
	public void updateStato(int idAsta) throws SQLException {
		String query = "UPDATE asta SET state=? WHERE idAsta = ?";
		PreparedStatement pstat = con.prepareStatement(query);
		pstat.setString(1, "closed");
		pstat.setInt(2, idAsta);
		pstat.executeUpdate();
		
	}
	
	
	

	//questo metodo fa uso di rollback, deve eseguire due stringhe concatenate oppure non eseguirne nessuna e fare rollback
	public void createAsta(List<Articolo> articoli, Timestamp DateStart, Timestamp DateExpiration, int idCreator,String state, Double currentPrice, int minimumIncrease, List<Integer> idArticoli )throws SQLException  {
		String query1 = "INSERT into asta (idAsta, date_start, Date_expiration, id_creator,state,Current_price,Minimum_increase) VALUES(?, ?, ?, ?, ?, ?, ?)";
		
		int idAstaRes;
	    con.setAutoCommit(false); //disabilitiamo l'autocommit;
		PreparedStatement pstatement1= null;
		try {
			
			//tentativo di modifica dello stato di tutti gli articoli selezionati
			
			//Query 1
			idAstaRes = findIdMaxAsta();
			//Query 2
			pstatement1= con.prepareStatement(query1);
			pstatement1.setInt(1,idAstaRes);
			pstatement1.setTimestamp(2,DateStart);
			pstatement1.setTimestamp(3,DateExpiration);
			pstatement1.setInt(4,idCreator);
			pstatement1.setString(5,state);
			pstatement1.setDouble(6,currentPrice);
			pstatement1.setDouble(7, minimumIncrease);
			pstatement1.executeUpdate();
			
			//ora inseriamo la correlazione tra idArticolo e idAsta nella tabella listaArticoli
			ListaArticoliDAO listaArticoli=new ListaArticoliDAO(con);
			listaArticoli.insertArticoloAsta(idArticoli, idAstaRes);
			int i;
			for(int j=0; j<idArticoli.size(); j++) {
				i=idArticoli.get(j);
				ArticoloDAO a = new ArticoloDAO(con);
				//Modifico lo stato dell'articolo
				a.setArticoloInAsta(i);
			}
			
			con.commit();
			System.out.println("Abbiamo eseguito tutto");
		}catch (SQLException e) {
			con.rollback();
			e.printStackTrace();
			throw e;
		}finally {
			con.setAutoCommit(true);
		}
		return;
	}
	
	public List<Asta> ricercaPerParolaChiave(String keyword, int idUtente, Timestamp actualTime) throws SQLException{
		String query1 = "SELECT DISTINCT asta.idAsta, asta.Date_start, asta.Date_expiration, asta.id_creator, asta.Current_price, asta.Minimum_increase "
				+ "FROM lista_articoli JOIN asta ON lista_articoli.id_asta=asta.idAsta JOIN articolo ON lista_articoli.id_articolo=articolo.idArticolo "
				+ "WHERE asta.state = ? AND asta.id_creator<> ? AND asta.Date_expiration > ? AND (articolo.name LIKE CONCAT('%', ?, '%') OR articolo.description LIKE CONCAT('%', ?, '%'))"
				+ "GROUP BY asta.idAsta, articolo.idArticolo "
				+ "ORDER BY (asta.Date_expiration - asta.Date_start) DESC";

		PreparedStatement pstatement0 = con.prepareStatement(query1);
		pstatement0.setString(1, "open");
		pstatement0.setInt(2, idUtente);
		pstatement0.setTimestamp(3, actualTime);
		pstatement0.setString(4, keyword);
		pstatement0.setString(5, keyword);
		ResultSet result = pstatement0.executeQuery();
		List<Asta> listaAsteDisponibili = new ArrayList<Asta>();
		if (!result.isBeforeFirst())
			return null;
		else {
			while(result.next()) {
				Asta a= new Asta();
				a.setIdAsta(result.getInt("idAsta"));
				a.setDateStart(result.getTimestamp("Date_start"));
				a.setDateExpiration(result.getTimestamp("Date_expiration"));
				a.setIdCreator(result.getInt("id_creator"));
				a.setCurrentPrice(result.getDouble("Current_price"));
				a.setMinimumIncrease(result.getInt("Minimum_increase"));
				listaAsteDisponibili.add(a);
			}
			return listaAsteDisponibili;
		}
	
	}
	
	//Per pagina VENDO recupero aste non chiuse create dall'utente
	public List<Asta> findAsteNonChiuse(int idUtente) throws SQLException{
		String query = "SELECT * FROM asta WHERE id_creator = ? AND state = ? ORDER BY asta.Date_start ASC";
		PreparedStatement pstatement0 = con.prepareStatement(query);
		pstatement0.setInt(1, idUtente);
		pstatement0.setString(2, "open");
		ResultSet result = pstatement0.executeQuery();
		List<Asta> listaAsteNonChiuse = new ArrayList<Asta>();
		if (!result.isBeforeFirst())
			return null;
		else {
			while(result.next()) {
				Asta a= new Asta();
				a.setIdAsta(result.getInt("idAsta"));
				a.setDateStart(result.getTimestamp("Date_start"));
				a.setDateExpiration(result.getTimestamp("Date_expiration"));
				a.setIdCreator(result.getInt("id_creator"));
				a.setCurrentPrice(result.getDouble("Current_price"));
				a.setMinimumIncrease(result.getInt("Minimum_increase"));
				listaAsteNonChiuse.add(a);
			}
			return listaAsteNonChiuse;
		}
	}
	
	//Per pagina VENDO recupero aste chiuse create dall'utente
	public List<Asta> findAsteChiuse(int idUtente) throws SQLException{
		String query = "SELECT * FROM asta WHERE id_creator = ? AND state = ? ORDER BY asta.Date_start ASC";
		PreparedStatement pstatement0 = con.prepareStatement(query);
		pstatement0.setInt(1, idUtente);
		pstatement0.setString(2, "closed");
		ResultSet result = pstatement0.executeQuery();
		List<Asta> listaAsteChiuse = new ArrayList<Asta>();
		if (!result.isBeforeFirst())
			return null;
		else {
			while(result.next()) {
				Asta a= new Asta();
				a.setIdAsta(result.getInt("idAsta"));
				a.setDateStart(result.getTimestamp("Date_start"));
				a.setDateExpiration(result.getTimestamp("Date_expiration"));
				a.setIdCreator(result.getInt("id_creator"));
				a.setCurrentPrice(result.getDouble("Current_price"));
				a.setMinimumIncrease(result.getInt("Minimum_increase"));
				listaAsteChiuse.add(a);
			}
			return listaAsteChiuse;
		}
	}
	
	public List<Asta> findAsteChiusePerAcquisto(int idUtente) throws SQLException{
		String query="SELECT * FROM asta WHERE id_creator <> ? AND state = ?";
		PreparedStatement pstatement0 = con.prepareStatement(query);
		pstatement0.setInt(1, idUtente);
		pstatement0.setString(2, "closed");
		ResultSet result = pstatement0.executeQuery();
		List<Asta> listaAsteChiuse = new ArrayList<Asta>();
		if (!result.isBeforeFirst())
			return null;
		else {
			while(result.next()) {
				Asta a= new Asta();
				a.setIdAsta(result.getInt("idAsta"));
				a.setDateStart(result.getTimestamp("Date_start"));
				a.setDateExpiration(result.getTimestamp("Date_expiration"));
				a.setIdCreator(result.getInt("id_creator"));
				a.setCurrentPrice(result.getDouble("Current_price"));
				a.setMinimumIncrease(result.getInt("Minimum_increase"));
				listaAsteChiuse.add(a);
			}
			return listaAsteChiuse;
		}
	}
	
}
