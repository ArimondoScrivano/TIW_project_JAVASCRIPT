{
		// page components
	  let pageOrchestrator = new PageOrchestrator(); // main controller

	  window.addEventListener("load", () => {
		  //NON HA TROVATO L'UTENTE IN SESSIONE
	    if (sessionStorage.getItem("username") == null) {
	      window.location.href = "index.html";
	    } else {
			//CONTROLLO SUL LOCAL STORAGE
			var lastActionSaved = recuperaUltimaAzione(sessionStorage.getItem("username"));
			if(lastActionSaved==null || lastActionSaved!=="Creazione asta"){
				//Caricamente pagina acquisto
				pageOrchestrator.start();
				
			}else{
				//Caricamento pagina vendo
				pageOrchestrator.startVendo();
			}
	    } 
	  }, false);
	  
	  //FUNZIONE CHE PRINTA IL NOME UTENTE
	  function PersonalMessage(_username, messagecontainer) {
	    this.username = _username;
	    this.show = function() {
	      messagecontainer.textContent = this.username;
	    }
	  }
	  
	  function PageOrchestrator() {
	    var alertContainer = document.getElementById("errormessage");
	    
	    
	    
	    //FUNZIONE CHE PASSA AL PERSONAL MESSAGE L'ELEMENTO HTML DOVE INSERIRE IL NOME
	    this.start = function() {
	      personalMessage = new PersonalMessage(sessionStorage.getItem('username'),
	      document.getElementById("id_username"));
	      personalMessage.show();
	      
	      
	      //FUNZIONE DI LOGOUT
	      document.querySelector("a[href='Logout']").addEventListener('click', () => {
	        window.sessionStorage.removeItem('username');
	      });
	      //Caricamento pagina acquisto
	      LoadAcquisto();
	      //CONTROLLO DELLE ASTE PRECEDENTEMENTE VISITATE
	      var ultimaAzione = localStorage.getItem(sessionStorage.getItem('username'));
			var asteVisitateOld;
			if(ultimaAzione){
				ultimaAzione=JSON.parse(ultimaAzione);
				var dataAttuale = new Date();
				var dataScadenza = new Date(ultimaAzione.data);
				
				if(dataScadenza>dataAttuale){
					asteVisitateOld = ultimaAzione.aste;
				}
			}
			if(asteVisitateOld){
				loadAsteAcquisto(asteVisitateOld);
			}
	      document.getElementById("acquisto").style.display="block";
	  	}
	  	
	  	
	  	//FUNZIONE CHE INIZIALIZZA LA PAGINA VENDO
	  	this.startVendo = function(){
			personalMessage = new PersonalMessage(sessionStorage.getItem('username'),
	      document.getElementById("id_username"));
	      personalMessage.show();
	      
	      
	      
	      document.querySelector("a[href='Logout']").addEventListener('click', () => {
	        	window.sessionStorage.removeItem('username');
	      });
	      //FUNZIONE CHE CARICA LA PAGINA VENDO
	      loadVendo();
	      document.getElementById("vendo").style.display="block";
	      document.getElementById("acquisto").style.display="none";
		 }
	  }
	  
	function recuperaUltimaAzione(username){
  		var ultimaAzione = localStorage.getItem(username);
  		if (ultimaAzione) {
    		ultimaAzione = JSON.parse(ultimaAzione);
    		var dataAttuale = new Date();
    		if (ultimaAzione.username === username) {
				//CONTROLLO CHE NON SIA SCADUTA
				var dataScadenza = new Date(ultimaAzione.data);
				if(dataScadenza>dataAttuale){
					return ultimaAzione.azione;
				}
    		}
  		}
  		return null;
	}
}