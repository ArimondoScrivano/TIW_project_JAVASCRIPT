package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import it.polimi.tiw.projects.dao.ArticoloDAO;
import it.polimi.tiw.projects.beans.User;

@WebServlet("/createNewArticolo")
@MultipartConfig(
fileSizeThreshold=1024*1024*10, // 10MB
maxFileSize=1024*1024*10,      // 10MB
maxRequestSize=1024*1024*50)   // 50MB
public class createNewArticolo extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;


	public createNewArticolo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void init() throws ServletException {
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
	

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        User user= (User)session.getAttribute("user");
        int idUtente= user.getIdUtente();
        //Parametri in input
        String name = request.getParameter("name");
        Part imagePart = request.getPart("image");
        String description = request.getParameter("descrizione");
        String price = request.getParameter("price");
        
        //Gestione immagine come parametro
        InputStream imageStream=null;
        String mimeType = null;
        if(imagePart!=null) {
        	imageStream = imagePart.getInputStream();
        	String filename = imagePart.getSubmittedFileName();
        	mimeType = getServletContext().getMimeType(filename);
        }
        
        
        //Controllo paramentri
        if(imageStream == null || (imageStream.available()==0) || !mimeType.startsWith("image/")
        		|| price==null || price.isEmpty() || name==null || name.isEmpty() || description == null
        		|| description.isEmpty()) {
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
        }
        
        
        Double newPrice=0.0;
        try {
        	newPrice= Double.parseDouble(request.getParameter("price"));
        	if(newPrice <=0.0) {
        		throw new NumberFormatException();
        	}
        }catch(NumberFormatException e) {
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
        }
        
        //Creazione nuovo articolo
        ArticoloDAO articolo = new ArticoloDAO(connection);
        try {
        	articolo.createArticolo(name, description, imageStream, newPrice, "available", idUtente);
        }catch(SQLException e) {
        	response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore server");
        	return;
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

	

