package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.projects.beans.Asta;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.AstaDAO;

@WebServlet("/chiudiAsta")
@MultipartConfig
public class chiudiAsta extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	

	public chiudiAsta() {
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
		// If the user is not logged in (not present in session) redirect to the login
        HttpSession session = request.getSession();
        //Utente in sessione
      	User user= (User)session.getAttribute("user");
        
      	String idAstaS = request.getParameter("idAsta");
		if(idAstaS==null || idAstaS.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri mancanti");
			return;
		}
		 
		int astaId=0;
		try {
			astaId=Integer.parseInt(idAstaS);
		}catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Errore parametri");
			return;
		}
		
		//Recupero data e ora attuale per controllo di chiusura
		Date dataInizio = new Date();
		long data = dataInizio.getTime();
		Timestamp RealTimeDate = new Timestamp(data);
		AstaDAO AstaDAO= new AstaDAO(connection);
		Asta a= new Asta();
		try {
			a= AstaDAO.findAstaById(astaId);
		}catch (SQLException e){
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore lato server");
			return;
		}
		
		if(a.getDateExpiration().compareTo(RealTimeDate)<0 && user.getIdUtente()==a.getIdCreator()) {
			try {
				AstaDAO.updateStato(astaId);
			}catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore lato server");
				return;
			}
			return;
		}else {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Asta non possibile da chiudere");
			return;
		}

	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
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
