package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.projects.beans.Asta;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.AstaDAO;
import it.polimi.tiw.projects.dao.OffertaDAO;


@WebServlet("/checkOfferta")
@MultipartConfig
public class checkOfferta extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	

	public checkOfferta() {
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


		HttpSession session = request.getSession();
		String prezzoOfferta= StringEscapeUtils.escapeJava(request.getParameter("offerprice"));
		String idAstaS = StringEscapeUtils.escapeJava(request.getParameter("idAstaOffer"));
		
		if(prezzoOfferta.isEmpty() || prezzoOfferta==null || idAstaS.isEmpty() || idAstaS==null ) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		
		double prezzoOff=0.0;
		int idAsta=0;
		try {
			idAsta=Integer.parseInt(idAstaS);
			prezzoOff= Double.parseDouble(prezzoOfferta);
		}catch(NumberFormatException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		System.out.println("Asta "+idAsta+" Prezzo offerto: "+ prezzoOff);
		//controllo validità offerta
		AstaDAO AstaDAO= new AstaDAO(connection);
		Asta a= new Asta();
		try {
			a= AstaDAO.findAstaById(idAsta);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ERROR, unreachable server");
			return;
		}
      
      
		//insufficient price
		if(prezzoOff < (a.getCurrentPrice()+ a.getMinimumIncrease())) {
			System.out.println("Test1");
			
			response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "ERROR, price too low");
			return;
		}
		System.out.println("Test2");
		OffertaDAO OffertaDAO= new OffertaDAO(connection);
		try {
			Date dataAttuale = new Date();
			long data = dataAttuale.getTime();
			Timestamp actualTime = new Timestamp(data);
		
			//Utente che crea l'offerta
  			User user= (User)session.getAttribute("user");
  	        int idUtente= user.getIdUtente();
  	        if(idUtente!=a.getIdCreator()) {
  	        	OffertaDAO.createNewOfferta(idAsta, idUtente, prezzoOff , actualTime);
  	        }else {
  	        	response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error! Can't offer for your auction!");
  	        }
		}catch(SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ERROR, unreachable server");
			String paginaPrecedente = request.getHeader("Referer");
	        response.sendRedirect(paginaPrecedente);
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
