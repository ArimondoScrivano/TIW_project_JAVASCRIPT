/**
 * PAGINA ACQUISTO
 */

//Listener per eventi


	//GESTORE DEL CLICK DEL BOTTONE DI ACQUISTO
	  document.getElementById("AcquistoButton").addEventListener("click",(e)=>{
		 handleEvent(e);
	 });

//FUNZIONE PER CARICARE LA PAGINA DI ACQUISTO
 function LoadAcquisto(){
	 document.getElementById("offerteAggiudicate").style.display="block";

		 makeCall("GET", "goToPageAcquisto", null,
		 function(x){
			 if (x.readyState == XMLHttpRequest.DONE) {
            		var message = x.responseText;
            			switch (x.status) {
              			case 200:
			 			var parsedJsonAcquisto = JSON.parse(x.responseText);
						var AsteVinte = parsedJsonAcquisto.AsteVinte;
						var ListaCreatori = parsedJsonAcquisto.ListaCreatori;
						var ListaArticoli = parsedJsonAcquisto.ListaArticoli;
						var auctionWon_body= document.getElementById("auctionWon_body");
						var i=-1;
						if(AsteVinte!=null){
							AsteVinte.forEach(function(element){
							i++;
							var tr= document.createElement("tr");
							var tdIdAsta= document.createElement("td");
							tdIdAsta.textContent= element.idAsta;
							tr.appendChild(tdIdAsta);
							var tdPrice= document.createElement("td");
							tdPrice.textContent = element.currentPrice;
							tr.appendChild(tdPrice);
							var tdCreatoreAsta= document.createElement("td");
							tdCreatoreAsta.textContent= ListaCreatori[i];
							tr.appendChild(tdCreatoreAsta);
							
							var tdArticoli= document.createElement("td");
							var tableArticoli = document.createElement("table");
							tableArticoli.border =1;
							//HEAD TABELLA
							var tableArticoli_head = document.createElement("thead");
							var tableArticoli_trH= document.createElement("tr");
							var thNameArticolo = document.createElement("th");
							thNameArticolo.textContent = "Nome articolo";
							tableArticoli_trH.appendChild(thNameArticolo);
							var thDescrizioneArticolo = document.createElement("th");
							thDescrizioneArticolo.textContent = "Descrizione";
							tableArticoli_trH.appendChild(thDescrizioneArticolo);
							var thPrezzoArticolo = document.createElement("th");
							thPrezzoArticolo.textContent = "Prezzo";
							tableArticoli_trH.appendChild(thPrezzoArticolo);
							var thImage = document.createElement("th");
							thImage.textContent = "Immagine";
							tableArticoli_trH.appendChild(thImage);
							tableArticoli_head.appendChild(tableArticoli_trH);
							tableArticoli.appendChild(tableArticoli_head);
							//BODY TABELLA
							var tableArticoloBody=document.createElement("tbody");
							ListaArticoli[i].forEach(function(article){
								var trArticle = document.createElement("tr");
								var tdNameArticle = document.createElement("td");
								tdNameArticle.textContent = article.name;
								trArticle.appendChild(tdNameArticle);
								var tdDescriptionArticle = document.createElement("td");
								tdDescriptionArticle.textContent = article.description;
								trArticle.appendChild(tdDescriptionArticle);
								var tdPriceArticle = document.createElement("td");
								tdPriceArticle.textContent = article.price;
								trArticle.appendChild(tdPriceArticle);
								var imageArticle = document.createElement("td");
								var imgElementArticle = document.createElement("img");
								imgElementArticle.src = 'data:image/jpeg/jpg/png;base64,'+article.imageLink;
								imgElementArticle.width = 250;
								imgElementArticle.height = 250;
								imgElementArticle.alt = "Image";
								imageArticle.appendChild(imgElementArticle);
								trArticle.appendChild(imageArticle);
								tableArticoloBody.appendChild(trArticle);
								tableArticoli.appendChild(tableArticoloBody);
								
							});
							tdArticoli.appendChild(tableArticoli);
							tr.appendChild(tdArticoli);
							auctionWon_body.appendChild(tr);
						});
						}
						if(i==-1){
							document.getElementById("offerteAggiudicate").style.display="none";
						}			
							break;
						case 500:
							document.getElementById("error").textContent = "Errore lato server";
							break;
						};						
			 }
		 });

	 	//RICERCA PER PAROLE CHIAVE
		 document.getElementById("keywordSearchButton").addEventListener('click', (e)=>{
			 document.getElementById("error").textContent = "";
			 var form = e.target.closest("form");
			 var divElement = document.getElementById("auctionList");
			 divElement.innerHTML = "";
    			if (form.checkValidity()) {
      				makeCall("POST", "goToPageAcquisto", e.target.closest("form"),
      				//FUNZIONE PER LA RICERCA PAROLE CHIAVE 
        			function(x) {
          				if (x.readyState == XMLHttpRequest.DONE) {
            				var message = x.responseText;
            			switch (x.status) {
              			case 200:
							saveLastAction(sessionStorage.getItem("username"), "Ricerca parola chiave", null);
            				var auctionList = JSON.parse(x.responseText);
            				var ul = document.getElementById("auctionList");
            				auctionList.forEach(function(element) {
								document.getElementById("idAstaOffer").value= element;
  								var li = document.createElement("li");
  								var anchor = document.createElement("a");
  								anchor.href = "#";
  								anchor.textContent = element;
  								li.appendChild(anchor);
  								ul.appendChild(li);
  								anchor.addEventListener('click', (e)=>{
									 document.getElementById("error").textContent = "";
									 var anchor = e.target.closest("anchor");
									 
									 makeCall('GET', "goToPageOfferta?id=" + element, anchor,
									 
									 //FUNZIONE REDIRECT PAG OFFERTA
										function OffertaFun(offerta){
											document.getElementById("auctionItem_body").innerHTML="";
											document.getElementById("auctionOffers_body").innerHTML="";
											if(offerta.readyState == XMLHttpRequest.DONE){
												var messageOfferta = offerta.responseText;
												switch(offerta.status){
													case 200:
														
														saveLastAction(sessionStorage.getItem("username"), "Asta visitata", element);
														var parsedJson = JSON.parse(offerta.responseText);
														var articoliSelAsta = parsedJson.articoliSelAsta;
														var offertaSelAsta = parsedJson.offertaSelAsta;
														
														document.getElementById("offerteAsta").style.display="block";
														var offertaDiv=document.getElementById("offerta");
														var acquistoDiv = document.getElementById("acquisto");
														var tableBody = document.getElementById("auctionItem_body");
														articoliSelAsta.forEach(function(articolo){
															var tr = document.createElement("tr");
															var name = document.createElement("td");
															name.textContent = articolo.name;
															tr.appendChild(name);
															var description = document.createElement("td");
															description.textContent = articolo.description;
															tr.appendChild(description);
															var price = document.createElement("td");
															price.textContent = articolo.price;
															tr.appendChild(price);
															var image = document.createElement("td");
															var imgElement = document.createElement("img");
															imgElement.src = 'data:image/jpeg/jpg/png;base64,'+articolo.imageLink;
															imgElement.width = 250;
															imgElement.height = 250;
															imgElement.alt = "Image";
															image.appendChild(imgElement);
															tr.appendChild(image);
															tableBody.appendChild(tr);
														});
														
														//Creazione tabella offerte
														if(offertaSelAsta!=null){
															tableBody = document.getElementById("auctionOffers_body");
															offertaSelAsta.forEach(function(offer){
																var tr = document.createElement("tr");
																var idUtente = document.createElement("td");
																idUtente.textContent = offer.idUtente;
																tr.appendChild(idUtente);
																var priceOffered = document.createElement("td");
																priceOffered.textContent = offer.offerPrice;
																tr.appendChild(priceOffered);
																var orarioOfferta = document.createElement("td");
																orarioOfferta.textContent = offer.dateHour;
																tr.appendChild(orarioOfferta);
																tableBody.appendChild(tr);
															});
														}else{
															document.getElementById("offerteAsta").style.display="none";
														}
														
														offertaDiv.style.display = "block";
														acquistoDiv.style.display = "none";
														
														
														//GESTIONE FORM PER L'INSERIMENTO OFFERTA
														document.getElementById("createOfferButton").addEventListener('click', (e) => {
   														 var formOfferta = e.target.closest("form");
     															 makeCall("POST", "checkOfferta", e.target.closest("form"),
     															 function(prezzo) {
          															if (prezzo.readyState == XMLHttpRequest.DONE) {
            															var message = prezzo.responseText;
           																switch (prezzo.status) {
																		 	case 200:
																				saveLastAction(sessionStorage.getItem("username"), "Creazione offerta", null);
																				//L'OFFERTA VIENE CREATA, QUINDI VA REFRESHATA LA PAGINA
																				refreshOfferta(element);
                															break;
              																case 400: // bad request
               																	document.getElementById("error").textContent = "Parametri errati!";
                															break;
                															case 401: // unauthorized
                																document.getElementById("error").textContent = "Utente non autorizzato!";
																				break;
              																case 406: //not acceptable
                  																document.getElementById("error").textContent = "Offerta troppo bassa!";
                  															break;
             																case 500: // server error
            																	document.getElementById("error").textContent = "Errore server!";
                															break;
            															 }
          															}
        														 }
      														  );
    													
  														});

														
													break;
													case 400: //bad request
														document.getElementById("error").textContent = "Errore inserimento parametri";
													break;
													case 401: //unauthorized
														document.getElementById("error").textContent="Non autorizzato";
														break;
													case 500: //server error
														document.getElementById("error").textContent = "Errore lato server";
													break;
												}
											}
										});
								  });
							});
            				
            				
                		break;
              			case 400: // bad request
                			document.getElementById("error").textContent = "Parametri errati";
                		break;
              			case 401: // unauthorized
                  			document.getElementById("error").textContent = "Utente non autorizzato";
                  		break;
              			case 500: // server error
            				var ul = document.getElementById("auctionList");
							ul.appendChild(document.createTextNode("Nessuna asta disponibile"));
                		break;
            	}
          	}
        }
      );
    } else {
    	 form.reportValidity();
    }
		 });
		 
		 //FUNZIONE CHE VIENE CHIAMATA NEL MOMENTO IN CUI VIENE CREATA L'OFFERTA
		 function refreshOfferta(idAsta){
			 
			 makeCall('GET', "goToPageOfferta?id="+ idAsta, null,
			 function(x){
				 if(x.readyState == XMLHttpRequest.DONE){
					 document.getElementById("auctionItem_body").textContent="";
					 document.getElementById("auctionOffers_body").textContent="";
												var messageOfferta = x.responseText;
												switch(x.status){
													case 200:
														var parsedJson = JSON.parse(x.responseText);
														var articoliSelAsta = parsedJson.articoliSelAsta;
														var offertaSelAsta = parsedJson.offertaSelAsta;
														
														document.getElementById("offerteAsta").style.display="block";
														var offertaDiv=document.getElementById("offerta");
														var acquistoDiv = document.getElementById("acquisto");
														var tableBody = document.getElementById("auctionItem_body");
														articoliSelAsta.forEach(function(articolo){
															var tr = document.createElement("tr");
															var name = document.createElement("td");
															name.textContent = articolo.name;
															tr.appendChild(name);
															var description = document.createElement("td");
															description.textContent = articolo.description;
															tr.appendChild(description);
															var price = document.createElement("td");
															price.textContent = articolo.price;
															tr.appendChild(price);
															var image = document.createElement("td");
															var imgElement = document.createElement("img");
															imgElement.src = 'data:image/jpeg/jpg/png;base64,'+articolo.imageLink;
															imgElement.width = 250;
															imgElement.height = 250;
															imgElement.alt = "Image";
															image.appendChild(imgElement);
															tr.appendChild(image);
															tableBody.appendChild(tr);
														});
														
														if(offertaSelAsta!=null){
															tableBody = document.getElementById("auctionOffers_body");
															offertaSelAsta.forEach(function(offer){
																var tr = document.createElement("tr");
																var idUtente = document.createElement("td");
																idUtente.textContent = offer.idUtente;
																tr.appendChild(idUtente);
																var priceOffered = document.createElement("td");
																priceOffered.textContent = offer.offerPrice;
																tr.appendChild(priceOffered);
																var orarioOfferta = document.createElement("td");
																orarioOfferta.textContent = offer.dateHour;
																tr.appendChild(orarioOfferta);
																tableBody.appendChild(tr);
															});
														}else{
															document.getElementById("offerteAsta").style.display="none";
														}
														
														offertaDiv.style.visibility = "visible";
														acquistoDiv.style.visibility = "hidden";
														break;
													case 400:
														document.getElementById("Errore inserimento parametri");
														break;
													case 401: //unauthorized
														document.getElementById("error").textContent="Non autorizzato";
														break;
													case 500:
														document.getElementById("error").textContent="Errore lato server";
														break;
				 								}
				 							 
				 }
				 });
			 
		 };

	 };
	 //FUNZIONE CHE RECUPERA LE ASTE APERTE IN PRECEDENZA 
	 function loadAsteAcquisto(asteOld){
		 var form = document.createElement("form");
		 var i =-1;
		 asteOld.forEach(function(){
			 i++;
			 let id = document.createElement("input");
			 id.value = asteOld[i];
			 id.name = "values";
			 form.appendChild(id);
		 });
		 if(i!=-1){
			 makeCall("POST", "asteOld", form,
		 function(x){
			 if(x.readyState == XMLHttpRequest.DONE){
				 switch(x.status){
					 case 200:
						 var parsedJson = JSON.parse(x.responseText);
						 var nomiArticoli = parsedJson.nomiArticoli; //Lista di liste di String
						 if(nomiArticoli!=null && nomiArticoli!=undefined){
						 //Costruzione pagina con articoli
						 var divOld = document.getElementById("asteOld");
						 divOld.innerHTML="";
						 var h2 = document.createElement("h2");
						 h2.textContent="Aste visitate in precedenza";
						 divOld.appendChild(h2);
						 //Tabella
						 let table = document.createElement("table");
						 table.border = 1;
						 //Head tabella
						 let table_head = document.createElement("thead");
						 let table_trHead= document.createElement("tr");
						 let thId = document.createElement("th");
						 thId.textContent = "Id asta";
						 table_trHead.appendChild(thId);
						 let thNomeArticolo = document.createElement("th");
						 thNomeArticolo.textContent = "Nome articolo";
						 table_trHead.appendChild(thNomeArticolo);
						 table_head.appendChild(table_trHead);
						 table.appendChild(table_head);
						 var i=0;
						 //Body tabella
						 var table_body = document.createElement("tbody");
						 nomiArticoli.forEach(function(articolo){
							 if(articolo!=null && articolo!=undefined && articolo!=""){
							 	let trArticolo = document.createElement("tr");
							 	let tdIdAsta =  document.createElement("td");
							 	tdIdAsta.textContent=asteOld[i];
							 	trArticolo.appendChild(tdIdAsta);
							 	//Creazione td con nome articolo
							 	let tdNomeArticolo = document.createElement("td");
								tdNomeArticolo.textContent=articolo;
								trArticolo.appendChild(tdNomeArticolo);
							 	table_body.appendChild(trArticolo);	 
							 }
							 i++;
						 });
						 table.appendChild(table_body);
						 divOld.appendChild(table);
						 }
						 break;
					 case 400:
						 document.getElementById("error").textContent = "Errore parametri";
						 break;
					 case 500:
						 //Errore del server
						 document.getElementById("error").textContent = "Errore server";
						 break;
				 }
			 }
		 });
		 }
	 }
	 
	 //Funzione che gestisce tutti gli eventi nella pagina
	 function handleEvent(event){
			 if(event.type == "click"){
				var offertaDiv=document.getElementById("offerta");
		 		var acquistoDiv = document.getElementById("acquisto");
		 		var auctionList= document.getElementById("auctionList");
		 		var vendoDiv = document.getElementById("vendo");
		 		//Resetting the page acquisto
		 		document.getElementById("auctionWon_body").innerHTML="";
				auctionList.innerHTML = "";
				acquistoDiv.style.display = "block";
				//Resetting the page offerta
		 		document.getElementById("auctionItem_body").innerHTML="";
				document.getElementById("auctionOffers_body").innerHTML="";
				offertaDiv.style.display = "none";
				//Resetting the page vendo
				document.getElementById("auctionClosed_body").innerHTML="";
				document.getElementById("auctionNotClosed_body").innerHTML="";
		 		vendoDiv.style.display = "none";
		 		//Resetting the page dettaglio asta
		 		document.getElementById("dettaglioAsta").style.display="none";
				document.getElementById("dettaglioAsta").innerHTML="";
		 		LoadAcquisto();
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
			 }
		 } 
	 	
		//FUNZIONE PER RECUPERARE L'ULTIMA AZIONE
		function saveLastAction(username, azione, id) {
			//Recupero il valore dal localStorage
			var ultimaAzione = localStorage.getItem(username);
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
				//Aggiorno aste vecchie
				var result;
				if(id!=null){
					if (!asteVisitateOld.includes(id)) {
  							asteVisitateOld.push(id);
					}
					
					result = asteVisitateOld;
				}else{
					result = asteVisitateOld;
				}
				var dateExp = new Date();
				dateExp.setDate(dateExp.getDate() + 30);
  				let lastAction = {
    				username: username,
    				azione: azione,
    				data : dateExp.getTime(),
    				aste : result
  				};
  				localStorage.setItem(username, JSON.stringify(lastAction));
			}else{
				var dateExp = new Date();
				dateExp.setDate(dateExp.getDate() + 30);
				if(id!=null){
					var lastAction = {
    					username: username,
    					azione: azione,
    					data : dateExp.getTime(),
    					aste : [id]
  					};
				}else{
					var lastAction = {
    					username: username,
    					azione: azione,
    					data : dateExp.getTime(),
    					aste : []
  					};
				}
  				
  				localStorage.setItem(username, JSON.stringify(lastAction));
			}
			
		}
		
		function recuperaUltimaAzione(username){
  			var ultimaAzione = localStorage.getItem(username);
  			if (ultimaAzione) {
    			ultimaAzione = JSON.parse(ultimaAzione);
    			var dataAttuale = new Date();
    			if (ultimaAzione.username === username) {
					//Controllo che non sia scaduto
					var dataScadenza = new Date(ultimaAzione.data);
					if(dataScadenza>dataAttuale){
						return ultimaAzione.azione;
					}
    			}
  			}
  			return null;
		}
			 	
