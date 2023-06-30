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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import it.polimi.tiw.projects.beans.Offerta;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.beans.Asta;
import it.polimi.tiw.projects.dao.AstaDAO;
import it.polimi.tiw.projects.dao.UserDAO;
import it.polimi.tiw.projects.dao.OffertaDAO;

@WebServlet("/goToDettaglioAsta")
@MultipartConfig
public class goToDettaglioAsta extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	

	public goToDettaglioAsta() {
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
        //Utente in sessione
      	User user= (User)session.getAttribute("user");
		String idAstaS = request.getParameter("id");
		if(idAstaS==null || idAstaS.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		 
		int astaId=0;
		try {
			astaId=Integer.parseInt(idAstaS);
		}catch (NumberFormatException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		
		//Controllo che il creatore dell'asta e asta id coincidano
		AstaDAO AstaDAO = new AstaDAO(connection);
		Asta asta = new Asta();
		try {
			asta=AstaDAO.findAstaById(astaId);
			if(asta.getIdCreator()!=user.getIdUtente()) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Operazione non permessa");
				return;
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore server");
        	return;
		}
		
		if(asta.getState().equals("open")) {
			//Asta aperta
			OffertaDAO OffertaDAO = new OffertaDAO(connection);
			List<Offerta> listOfferte = new ArrayList<>();
			try {
				listOfferte=OffertaDAO.getListOffertebyId(astaId);
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore server");
	        	return;
			}
			//Recupero nome utente data l'offerta
			List<User> nomeUtenteOfferta = new ArrayList<>();
			if(listOfferte!=null) {
				for(Offerta o : listOfferte) {
				int idUtenteOfferta = o.getidUtente();
				UserDAO UserDAO = new UserDAO(connection);
				User userOfferta = new User();
				try {
					userOfferta=UserDAO.getDetailsUtente(idUtenteOfferta);
					nomeUtenteOfferta.add(userOfferta);
				} catch (SQLException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore server");
		        	return;
				}
				}
			}
			
			Gson gson = new Gson();
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("asta", gson.toJsonTree(asta));
			jsonObject.add("listOfferte", gson.toJsonTree(listOfferte));
			jsonObject.add("nomeUtente", gson.toJsonTree(nomeUtenteOfferta));
			jsonObject.add("nomeCreatore", gson.toJsonTree(user.getUsername()));
			String jsonDettaglio = gson.toJson(jsonObject);
			// Converte la lista in formato JSON
		    response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    response.getWriter().write(jsonDettaglio);
			
		}else {
			//Asta chiusa			
			User U= new User();
			UserDAO UserDAO= new UserDAO(connection);
			OffertaDAO OffertaDAO=  new OffertaDAO(connection);
			try {
			
				U= UserDAO.getDetailsUtente(OffertaDAO.findidUtenteMaxOfferta(astaId));
			}catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore server");
	        	return;
			}
			
			Gson gson = new Gson();
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("asta", gson.toJsonTree(asta));
			if(U!=null) {
				jsonObject.add("nomeAggiudicatario", gson.toJsonTree(U.getUsername()));
				jsonObject.add("indirizzoSpedizione", gson.toJsonTree(U.getShippingAddress()));
				jsonObject.add("prezzo", gson.toJsonTree(asta.getCurrentPrice()));
			}else {
				jsonObject.add("nomeAggiudicatario", gson.toJsonTree("Nessuno"));
				jsonObject.add("indirizzoSpedizione", gson.toJsonTree("Nessuno"));
				jsonObject.add("prezzo", gson.toJsonTree("Non venduto"));
			}
			jsonObject.add("nomeCreatore", gson.toJsonTree(user.getUsername()));
			String jsonDettaglio = gson.toJson(jsonObject);
			// Converte la lista in formato JSON
		    response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    response.getWriter().write(jsonDettaglio);
		}
		
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
