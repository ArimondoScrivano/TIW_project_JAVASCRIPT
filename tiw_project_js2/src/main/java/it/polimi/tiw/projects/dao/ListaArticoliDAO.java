package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Articolo;

public class ListaArticoliDAO {
	private Connection con;

	public ListaArticoliDAO(Connection connection) {
		this.con = connection;
	}
	
	public int getIdArticolo(int idAsta) throws SQLException {
		String query = "SELECT * FROM lista_articoli WHERE id_asta = ?";
		PreparedStatement pstat = con.prepareStatement(query);
		pstat.setInt(1, idAsta);
		ResultSet result = pstat.executeQuery();
		if(!result.isBeforeFirst()) {
			return 0;
		}else {
			return result.getInt("id_articolo");
		}
	}
	
	public int getIdAsta(int idArticolo) throws SQLException {
		String query = "SELECT * FROM lista_articoli WHERE id_articolo = ?";
		PreparedStatement pstat = con.prepareStatement(query);
		pstat.setInt(1, idArticolo);
		ResultSet result = pstat.executeQuery();
		if(!result.isBeforeFirst()) {
			return 0;
		}else {
			return result.getInt("id_asta");
		}
	}
	
	public void insertArticoloAsta(List<Integer> articoli, int idAsta) throws SQLException{
		String query = "INSERT into lista_articoli (id_asta, id_articolo) VALUES(?,?)";
		PreparedStatement pstat= null;
		pstat =con.prepareStatement(query);
		//Inserimento di ogni coppia idAsta - idArticolo
		for(int i=0; i< articoli.size(); i++) {
			pstat.setInt(1,idAsta);
			pstat.setInt(2, articoli.get(i));
			pstat.executeUpdate();
		}
	}
	
	public List<Integer> listaArticoli(int id) throws SQLException{
		String query = "SELECT * FROM lista_articoli WHERE id_asta = ?";
		PreparedStatement pstat = con.prepareStatement(query);
		pstat.setInt(1, id);
		ResultSet result = pstat.executeQuery();
		if(!result.isBeforeFirst()) {
			return null;
		}else {
				List<Integer> listaArticoli= new ArrayList<>();
				while(result.next()) {
					int a= result.getInt("id_articolo");
					listaArticoli.add(a);
					
				}
				return listaArticoli;
			}
		
	}
	
	
	
	
	
	
	
}