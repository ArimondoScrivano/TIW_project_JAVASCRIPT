package it.polimi.tiw.projects.controllers;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.*;

import it.polimi.tiw.projects.beans.Articolo;
import it.polimi.tiw.projects.beans.Asta;
import it.polimi.tiw.projects.beans.Offerta;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.ArticoloDAO;
import it.polimi.tiw.projects.dao.AstaDAO;
import it.polimi.tiw.projects.dao.ListaArticoliDAO;
import it.polimi.tiw.projects.dao.OffertaDAO;

@WebServlet("/goToPageOfferta")
@MultipartConfig
public class goToPageOfferta extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	

	public goToPageOfferta() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public void init() {
		ServletContext servletContext = getServletContext();
		String driver = servletContext.getInitParameter("dbDriver");
		String url = servletContext.getInitParameter("dbUrl");
		String user = servletContext.getInitParameter("dbUser");
		String password = servletContext.getInitParameter("dbPassword");
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			connection = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		doGet(request, response);		 
	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// If the user is not logged in (not present in session) redirect to the login
        HttpSession session = request.getSession();
        
		String itemId = request.getParameter("id");
		if(itemId==null || itemId.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		 
		int astaId=0;
		try {
			astaId=Integer.parseInt(itemId);
		}catch (NumberFormatException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		
	
		AstaDAO AstaDAO= new AstaDAO(connection);
		User user= (User)session.getAttribute("user");
        int idUtente= user.getIdUtente();
        try {
        	Asta a= AstaDAO.findAstaById(astaId);
        	if(a.getIdCreator()==idUtente || a.getState().equals("closed")) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "CREDENZIALI NEGATE");
			return;
		}
        }catch (SQLException e) {
        	response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore server");
        	return;
        }
		
		
		
		
		List<Articolo> ArticoliSelAsta= new ArrayList<>();
		List<Offerta> OffertaSelAsta= new ArrayList<>();
		try {
			//recuperiamo gli id di tutti gli articoli dell'asta cliccata
			ListaArticoliDAO listaArticoliDao= new ListaArticoliDAO(connection);
			List<Integer> listaArticoliID= new ArrayList<>();
			listaArticoliID.addAll(listaArticoliDao.listaArticoli(astaId));
			
			//recuperiamo tutti gli articoli dati i loro ID
			ArticoloDAO articoloDAO= new ArticoloDAO(connection);
			
			for(int i=0; i<listaArticoliID.size(); i++) {
				ArticoliSelAsta.add(articoloDAO.getArticoloById(listaArticoliID.get(i)));
				
			}
		
			//recuperiamo le offerte data quell'asta
			OffertaDAO OffertaDAO= new OffertaDAO(connection);
			try {
				OffertaSelAsta= OffertaDAO.getListOffertebyId(astaId);
			}catch (SQLException  e){
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore server");
	        	return;			
	        }
			
			
		}catch (SQLException  e){
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore server");
        	return;
		}
		Gson gson = new Gson();
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("articoliSelAsta", gson.toJsonTree(ArticoliSelAsta));
		jsonObject.add("offertaSelAsta", gson.toJsonTree(OffertaSelAsta));
		String jsonOfferta = gson.toJson(jsonObject);
		// Converte la lista in formato JSON
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(jsonOfferta);
	}
	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}
	
	
	
}
