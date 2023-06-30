package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
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

import it.polimi.tiw.projects.beans.Asta;
import it.polimi.tiw.projects.beans.Articolo;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.AstaDAO;
import it.polimi.tiw.projects.dao.ListaArticoliDAO;
import it.polimi.tiw.projects.dao.ArticoloDAO;


@WebServlet("/createAsta")
@MultipartConfig
public class createAsta extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public createAsta() {
		super();
	}

	public void init() throws ServletException {
		ServletContext context = getServletContext();
		String driver = context.getInitParameter("dbDriver");
		String url = context.getInitParameter("dbUrl");
		String user = context.getInitParameter("dbUser");
		String password = context.getInitParameter("dbPassword");
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
        HttpSession session = request.getSession();
		
		//PARAMETRI TRAMITE FORM
		//Recupero articoli scelti tramite paramentri nella request
		String[] articoliSelezionati = request.getParameterValues("values");
		//Aumento minimo
		String aumentoMinimo = request.getParameter("minimumIncrease");
		String dataFine = request.getParameter("dateExpiration");
		//Controllo che non manchino parametri
		if (articoliSelezionati == null || articoliSelezionati.length==0 || dataFine == null || dataFine.isEmpty() 
				|| aumentoMinimo == null || aumentoMinimo.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		//Utente che crea l'asta
		User user= (User)session.getAttribute("user");
		int idUtente= user.getIdUtente();
		//Adatto parametri
		List<Integer> idArticoli = new ArrayList<Integer>();
		for(int i=0; i<articoliSelezionati.length; i++) {
			int id=0;
			try {
				//Creo una lista di id degli articoli che sono stati selezionati
				 id=Integer.parseInt(articoliSelezionati[i]);
			}catch(NumberFormatException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Not a id");
				return;
			}
			idArticoli.add(id);
		}
		ArticoloDAO articoloDAO = new ArticoloDAO(connection);
		//Controllo che gli articoli siano disponibili e che siano miei
		for(int i=0; i<idArticoli.size(); i++) {
			Articolo articoloToCheck = new Articolo();
			try {
				articoloToCheck = articoloDAO.getArticoloById(idArticoli.get(i));
				if(articoloToCheck.getState().equals("unavailable") || articoloToCheck.getidUtente()!=idUtente) {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bad input");
					return;
				}
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore server");
	        	return;
			}
			
		}
		//Data fine
		LocalDateTime ldt = LocalDateTime.parse(dataFine);
		Timestamp endDateToUse = Timestamp.valueOf(ldt);
		
		//Data inizio
		//Recupero data e ora attuale per iniziare la nuova asta
		Date dataInizio = new Date();
		long data = dataInizio.getTime();
		Timestamp dateStart2 = new Timestamp(data);
		//Controllo che dateStart<DateEnd
		if(dateStart2.getTime() >= endDateToUse.getTime()) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Parameters error");
			return;
		}
		//Recupero gli articoli tramite query dati gli id
		List<Articolo> articoliInAsta = new ArrayList<Articolo>();
		int i;
		for(int j=0; j<idArticoli.size(); j++) {
			i=idArticoli.get(j);
			
			Articolo articoloDaAggiungere = new Articolo();
			try {
				//Aggiungo articolo alla lista di articoli in asta dopo averlo trovato tramite id
				
				articoloDaAggiungere=articoloDAO.getArticoloById(i);
				articoliInAsta.add(articoloDaAggiungere);
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore server");
	        	return;
			}
		}
		//Prezzo iniziale dell'asta dato dalla somma di tutti i prezzi degli articoli
		double prezzoInizialeAsta = 0;
		for(Articolo a : articoliInAsta) {
			prezzoInizialeAsta += a.getPrice();
		}
		
		int minimumIncrease=0;
		try {
			minimumIncrease = Integer.parseInt(aumentoMinimo);
		}catch (NumberFormatException e){
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Wrong parameters, not a number inserted");
			return;
		}
		
        //Creazione asta
		AstaDAO asta = new AstaDAO(connection);
		try {
			//creiamo l'asta, modifichiamo lo stato degli articoli selezionati e aggiungiamo idArticolo e IdAsta nella tabella listaArticoli altrimenti ROLLBACK
			asta.createAsta(articoliInAsta, dateStart2, endDateToUse, idUtente, "open", prezzoInizialeAsta, minimumIncrease,idArticoli);
			
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore server");
        	return;
		}
			System.out.println("Asta creata");
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
