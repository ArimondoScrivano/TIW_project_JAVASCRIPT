package it.polimi.tiw.projects.controllers;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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

import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.beans.Articolo;
import it.polimi.tiw.projects.beans.Asta;
import it.polimi.tiw.projects.dao.ArticoloDAO;
import it.polimi.tiw.projects.dao.AstaDAO;
import it.polimi.tiw.projects.dao.ListaArticoliDAO;
import it.polimi.tiw.projects.dao.OffertaDAO;
import it.polimi.tiw.projects.dao.UserDAO;



@WebServlet("/goToPageAcquisto")
@MultipartConfig
public class goToPageAcquisto extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private List<Asta> aste;
	
	public goToPageAcquisto() {
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
		this.aste=new ArrayList<>();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
        //Ricerco offerte aggiudicate dall'utente
        //Utente in sessione
        User user= (User)session.getAttribute("user");
        int idUtente = user.getIdUtente();
        System.out.println("Utente in sessione "+idUtente);
        
        List<Asta> AstePotVinte= new ArrayList<>();
        AstaDAO AstaDAO= new AstaDAO(connection);
        //recupero Aste Potenzialmente vinte dall'utente in sessione
        try {
			AstePotVinte= AstaDAO.findAsteChiusePerAcquisto(idUtente);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore lato server");
			return;	
		}
        
        //recupero Aste vinte dall'utente in sessione
        List<Asta> AsteVinte= new ArrayList<>();
        OffertaDAO OffertaDAO= new OffertaDAO(connection);
        List<List<Articolo>> listaArticoli = new ArrayList<>();
        List<String> ListaCreatori= new ArrayList<>();

        if(AstePotVinte!=null) {
        	try {
        	AsteVinte= OffertaDAO.getAsteVinte(AstePotVinte,idUtente);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore server");
			return;
		}
        
        if(AsteVinte!=null) {
        	//recupero lista creatori delle aste vinte dall'utente
        UserDAO uDAO= new UserDAO(connection); 
        try {
        	for(int i=0; i<AsteVinte.size(); i++) {
        	User creator= new User();
        	creator= uDAO.getDetailsUtente(AsteVinte.get(i).getIdCreator());
        	ListaCreatori.add(creator.getUsername());
        	}
        } catch (SQLException e) {
        	response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore lato server");
			return;
		}
        
        //recupero le liste di Articoli per ogni asta
        
        ListaArticoliDAO ListaArticoliDAO= new ListaArticoliDAO(connection);
        ArticoloDAO ArticoloDAO= new ArticoloDAO(connection);
        for(int i=0; i<AsteVinte.size(); i++) {
        	List<Integer> ArticoliID= new ArrayList<>();
        	try {
        	ArticoliID= ListaArticoliDAO.listaArticoli(AsteVinte.get(i).getIdAsta());
        	List<Articolo> listaAdAsta= new ArrayList<>();
        	for(int j=0; j< ArticoliID.size(); j++) {
        		listaAdAsta.add(ArticoloDAO.getArticoloById(ArticoliID.get(j)));
        	}
        	
        	listaArticoli.add(listaAdAsta);
        	}catch (SQLException e) {
        		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore lato server");
    			return;	
    		}
            
        }
        }
        }
        
        Gson gson = new Gson();
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("AsteVinte", gson.toJsonTree(AsteVinte));
		jsonObject.add("ListaCreatori", gson.toJsonTree(ListaCreatori));
		jsonObject.add("ListaArticoli", gson.toJsonTree(listaArticoli));
		String jsonAcquisto = gson.toJson(jsonObject);
		// Converte la lista in formato JSON
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(jsonAcquisto);
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		//RicercaParolaChiave
		String keyword = request.getParameter("keyword");
		HttpSession session = request.getSession();
		//Controllo che non manchino parametri
		if (keyword == null || keyword.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Bad parameters");
			return;
		}
		
		AstaDAO asteDisponibili = new AstaDAO(connection);
		List<Asta> a = null;
		try {
			User user= (User)session.getAttribute("user");
	        int idUtente= user.getIdUtente();
	        Date dataInizio = new Date();
			long data = dataInizio.getTime();
			Timestamp actualTime = new Timestamp(data);
			a = asteDisponibili.ricercaPerParolaChiave(keyword,idUtente, actualTime);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore server");
			return;
		}
		
		this.aste=a;
		List<Integer> idAsta = new ArrayList<>();
		if(a!=null) {
			for(Asta as : a) {
				idAsta.add(as.getIdAsta());
			}
		}
		
		// Converte la lista in formato JSON
	    Gson gson = new Gson();
	    String json = gson.toJson(idAsta);
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(json);
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
