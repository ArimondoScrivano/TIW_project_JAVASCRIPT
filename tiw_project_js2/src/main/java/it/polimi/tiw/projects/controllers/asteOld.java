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
import com.google.gson.JsonObject;

import it.polimi.tiw.projects.beans.Asta;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.AstaDAO;
import it.polimi.tiw.projects.dao.ListaArticoliDAO;
import it.polimi.tiw.projects.dao.ArticoloDAO;


@WebServlet("/asteOld")
@MultipartConfig
public class asteOld extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public asteOld() {
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
        int idUtente = user.getIdUtente();
      	String idAsteOld[] = request.getParameterValues("values");
		if(idAsteOld==null || idAsteOld.length==0) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri mancanti");
			return;
		}
		List<Integer> idAste = new ArrayList<Integer>();
		for(int i=0; i<idAsteOld.length; i++) {
			int id=0;
			try {
				//Creo una lista di id degli articoli che sono stati selezionati
				 id=Integer.parseInt(idAsteOld[i]);
			}catch(NumberFormatException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Not a id");
				return;
			}
			idAste.add(id);
		}
		
		//Recupero aste tramite il loro id
		AstaDAO AstaDAO = new AstaDAO(connection);
		List<Asta> asteToCheck = new ArrayList<>();
		for(Integer i : idAste) {
			try {
				Asta a = AstaDAO.findAstaById(i);
				if(a.getIdCreator()!=idUtente) {
					asteToCheck.add(a);
				}
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore lato server");
				return;
			}
		}
		//Recupero gli id degli articoli nell'asta
		ListaArticoliDAO ListaArticoliDAO = new ListaArticoliDAO(connection);
		List<List<Integer>> idArticoliPerAsta = new ArrayList<>();
		for(int i = 0; i<asteToCheck.size(); i++) {
			
			try {
				List<Integer> id = ListaArticoliDAO.listaArticoli(asteToCheck.get(i).getIdAsta());
				idArticoliPerAsta.add(id);
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore lato server");
				return;
			}
		}
		
		//Recupero i nomi degli articoli
		ArticoloDAO ArticoloDAO = new ArticoloDAO(connection);
		List<List<String>> nomeArticoli = new ArrayList<>();
		for(int i = 0; i<idArticoliPerAsta.size(); i++) {
			List<String> nomi = new ArrayList<>();
			for(int j=0; j<idArticoliPerAsta.get(i).size() && asteToCheck.get(i).getState().equals("open"); j++) {
				try {
					String name = ArticoloDAO.getArticoloById(idArticoliPerAsta.get(i).get(j)).getName();
					nomi.add(name);
				} catch (SQLException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore lato server");
					return;
				}
			}
			nomeArticoli.add(nomi);
		}
		Gson gson = new Gson();
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("nomiArticoli",gson.toJsonTree(nomeArticoli));
		String json = gson.toJson(jsonObject);
		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(json);
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
