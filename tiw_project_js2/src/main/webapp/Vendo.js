/**
 * PAGINA VENDO
 */
	 //FUNZIONE CHE CARICA LA PAGINA VENDO
	 function loadVendo(){
		 makeCall("GET", "goToPageVendo", null,
		 function(x){
			document.getElementById("availableArticles").innerHTML="";
			document.getElementById("auctionClosed_body").innerHTML="";
			document.getElementById("auctionNotClosed_body").innerHTML="";
			//Displaying the tables
			document.getElementById("AsteChiuse").style.display="block";
			document.getElementById("AsteNonChiuse").style.display="block";
			if(x.readyState==XMLHttpRequest.DONE){
				var message = x.responseText;
				switch(x.status){
					case 200:
						//Parsing the json file
						var parsedJsonVendo = JSON.parse(x.responseText);
						var listaArticoliDisponibili = parsedJsonVendo.listaArticoli; //Lista articoli disponibili
						var asteNonChiuse = parsedJsonVendo.asteNonChiuse; //Lista
						var ListaGiorniMancantiANC = parsedJsonVendo.ListaGiorniMancantiANC; //Lista
						var ListaOreMancantiANC = parsedJsonVendo.ListaOreMancantiANC; //Lista
						var ListaArticoliA = parsedJsonVendo.ListaArticoliA; //Lista di liste
						var asteChiuse = parsedJsonVendo.asteChiuse; //Lista
						var ListaArticoliB = parsedJsonVendo.ListaArticoliB; //Lista di liste
						//Creazione tabella aste non chiuse
						var i=-1;
						var asteNonChiuse_body = document.getElementById("auctionNotClosed_body");
						if(asteNonChiuse!=null){
							asteNonChiuse.forEach(function(asta){
								i++;
								let trAsta = document.createElement("tr");
								let tdIdAsta = document.createElement("td");
								let anchor = document.createElement("a");
								anchor.textContent = asta.idAsta;
								anchor.href="#";
								tdIdAsta.appendChild(anchor);
								var idAsta = asta.idAsta;
								//Asta non chiusa
								anchor.addEventListener("click", (e)=>{
									//FUNZIONE CHE CARICA IL DETTAGLIO PER LE ASTE NON CHIUSE
									loadDettaglioAstaNotClosed(asta.idAsta);
								});
								trAsta.appendChild(tdIdAsta);
								let tdCurrentPrice = document.createElement("td");
								tdCurrentPrice.textContent = asta.currentPrice;
								trAsta.appendChild(tdCurrentPrice);
								let tdGiorniMancanti = document.createElement("td");
								tdGiorniMancanti.textContent = ListaGiorniMancantiANC[i];
								trAsta.appendChild(tdGiorniMancanti);
								let tdOreMancanti = document.createElement("td");
								tdOreMancanti.textContent = ListaOreMancantiANC[i];
								trAsta.appendChild(tdOreMancanti);
								//Articoli in asta
								let tdArticoliInAsta = document.createElement("td");
								let tableArticoli = document.createElement("table");
								tableArticoli.border = 1;
								//Head articoli in asta
								let tableArticoli_head = document.createElement("thead");
								let tableArticoli_trHead= document.createElement("tr");
								let thCodiceArticolo = document.createElement("th");
								thCodiceArticolo.textContent = "Codice";
								tableArticoli_trHead.appendChild(thCodiceArticolo);
								let thNomeArticolo = document.createElement("th");
								thNomeArticolo.textContent = "Nome";
								tableArticoli_trHead.appendChild(thNomeArticolo);
								tableArticoli_head.appendChild(tableArticoli_trHead);
								tableArticoli.appendChild(tableArticoli_head);
								//Body tabella
								var tableArticoliBody = document.createElement("tbody");
								ListaArticoliA[i].forEach(function(articolo){
									let trArticle = document.createElement("tr");
									let tdIdArticolo = document.createElement("td");
									tdIdArticolo.textContent = articolo.idArticolo;
									trArticle.appendChild(tdIdArticolo);
									let tdNomeArticolo = document.createElement("td");
									tdNomeArticolo.textContent = articolo.name;
									trArticle.appendChild(tdNomeArticolo);
									tableArticoliBody.appendChild(trArticle);
									tableArticoli.appendChild(tableArticoliBody);
								});
								tdArticoliInAsta.appendChild(tableArticoli);
								trAsta.appendChild(tdArticoliInAsta);
								asteNonChiuse_body.appendChild(trAsta);
							});
						}else{
							document.getElementById("AsteNonChiuse").style.display="none";
						}
						//Creazione tabella aste chiuse
						var j=-1;
						var asteChiuse_body = document.getElementById("auctionClosed_body");
						if(asteChiuse!=null){
							asteChiuse.forEach(function(asta){
								j++;
								let trAsta = document.createElement("tr");
								let tdIdAsta = document.createElement("td");
								let anchor = document.createElement("a");
								anchor.textContent=asta.idAsta;
								anchor.href="#";
								//Asta chiusa
								anchor.addEventListener('click', (e)=>{
									//FUNZIONE CHE CARICA IL DETTAGLIO PER LE ASTE CHIUSE
									loadDettaglioAstaClosed(asta.idAsta);
								});
								tdIdAsta.appendChild(anchor);
								trAsta.appendChild(tdIdAsta);
								let tdStart = document.createElement("td");
								tdStart.textContent = asta.dateStart;
								trAsta.appendChild(tdStart);
								let tdEnd = document.createElement("td");
								tdEnd.textContent = asta.dateExpiration;
								trAsta.appendChild(tdEnd);
								let tdFinalPrice = document.createElement("td");
								tdFinalPrice.textContent = asta.currentPrice;
								trAsta.appendChild(tdFinalPrice);
								//Articoli in asta
								let tdArticoliInAsta = document.createElement("td");
								let tableArticoli = document.createElement("table");
								tableArticoli.border = 1;
								//Head articoli in asta
								let tableArticoli_head = document.createElement("thead");
								let tableArticoli_trHead= document.createElement("tr");
								let thCodiceArticolo = document.createElement("th");
								thCodiceArticolo.textContent = "Codice";
								tableArticoli_trHead.appendChild(thCodiceArticolo);
								let thNomeArticolo = document.createElement("th");
								thNomeArticolo.textContent = "Nome";
								tableArticoli_trHead.appendChild(thNomeArticolo);
								tableArticoli_head.appendChild(tableArticoli_trHead);
								tableArticoli.appendChild(tableArticoli_head);
								//Body tabella
								var tableArticoliBody = document.createElement("tbody");
								ListaArticoliB[j].forEach(function(articolo){
									let trArticle = document.createElement("tr");
									let tdIdArticolo = document.createElement("td");
									tdIdArticolo.textContent = articolo.idArticolo;
									trArticle.appendChild(tdIdArticolo);
									let tdNomeArticolo = document.createElement("td");
									tdNomeArticolo.textContent = articolo.name;
									trArticle.appendChild(tdNomeArticolo);
									tableArticoliBody.appendChild(trArticle);
									tableArticoli.appendChild(tableArticoliBody);
								});
								tdArticoliInAsta.appendChild(tableArticoli);
								trAsta.appendChild(tdArticoliInAsta);
								asteChiuse_body.appendChild(trAsta);
							});
						}else{
							document.getElementById("AsteChiuse").style.display="none";
						}
						//Articoli disponibili
						if(listaArticoliDisponibili!=null){
							var availableArticles = document.getElementById("availableArticles");
							listaArticoliDisponibili.forEach(function(articolo){
								let articleName = document.createElement("input");
								articleName.type = "checkbox";
								articleName.value = articolo.idArticolo;
								articleName.name = "values";
								let text = document.createElement("span");
								text.textContent = articolo.name;
								availableArticles.appendChild(articleName);
								availableArticles.appendChild(text);
								let br = document.createElement("br");
								availableArticles.appendChild(br);
							});
							
							
						}else{
							//Nessun articolo disponibile in asta
							var availableArticles = document.getElementById("availableArticles");
							let text = document.createElement("span");
							text.textContent = "Nessun articolo disponibile";
							availableArticles.appendChild(text);
							let br = document.createElement("br");
							availableArticles.appendChild(br);
						}
						
						//FUNZIONE PER LA CREAZIONE DI UN NUOVO ARTICOLO
						document.getElementById("createArticleButton").addEventListener("click", (e)=>{
							
							document.getElementById("error").textContent = "";
							var form = e.target.closest("form");
							if(form.checkValidity()){
							makeCall("POST", "createNewArticolo", e.target.closest("form"),
							function(x){
								if(x.readyState == XMLHttpRequest.DONE){
									switch(x.status){
										case 200:
											//Articolo creato, refresh della pagina vendo
											saveLastAction(sessionStorage.getItem("username"), "Creazione articolo", null);
											loadVendo();
											break;
										case 400:
											//Articolo non creato
											document.getElementById("error").textContent = "Errore parametri";
											break;
										case 500:
											//Errore del server
											document.getElementById("error").textContent = "Errore server";
											break;
									}
								}
							});
							}else{
								form.reportValidity();
							}
	 						});
	 						
	 						//FUNZIONE PER LA CREAZIONE DI UN'ASTA
	 						document.getElementById("createAuctionButton").addEventListener("click", (e)=>{
								document.getElementById("error").textContent ="";
								var form = e.target.closest("form");
								if(form.checkValidity()){
									makeCall("POST", "createAsta", e.target.closest("form"),
									function(y){
										if(y.readyState == XMLHttpRequest.DONE){
											switch(y.status){
												case 200:
													//ASTA CREATA, REFRESH DELLA PAGINA VENDO
													saveLastAction(sessionStorage.getItem("username"), "Creazione asta", null);
													loadVendo();
													break;
												case 400:
													//Asta non creata
													document.getElementById("error").textContent = "Errore parametri";
													break;
												case 401: //unauthorized
													document.getElementById("error").textContent="Non autorizzato";
													break;
												case 403: //Forbidden
													document.getElementById("error").textContent="Data non permessa";
													break;
												case 500:
													//Errore del server
													document.getElementById("error").textContent = "Errore server";
													break;
											}
										}
									});
								}else{
									form.reportValidity();
								}
	 						});
						
						break;
					case 500:
						document.getElementById("error").textContent="Errore lato server";
						break;
				}
			} 
		 });
	 };
	 
	 function loadDettaglioAstaClosed(idAsta){
		 document.getElementById("error").textContent = "";
		 makeCall('GET', "goToDettaglioAsta?id="+idAsta,null,
			 function(x){
				 //Funzione creazione pagina dettaglio asta
				 if(x.readyState == XMLHttpRequest.DONE){
					 var message = x.responseText;
					 switch(x.status){
						 case 200:
							var offertaDiv=document.getElementById("offerta");
							var acquistoDiv = document.getElementById("acquisto");
							var vendoDiv = document.getElementById("vendo");
							//Resetting the page acquisto
							document.getElementById("auctionWon_body").innerHTML="";
							document.getElementById("auctionList").innerHTML="";
							acquistoDiv.style.display = "none";
							//Resetting the page offerta
							document.getElementById("auctionItem_body").innerHTML="";
							document.getElementById("auctionOffers_body").innerHTML="";
							offertaDiv.style.display = "none";
							//Resetting the page vendo
							document.getElementById("auctionClosed_body").innerHTML="";
							document.getElementById("auctionNotClosed_body").innerHTML="";
							 var parsedJsonDettaglioAsta = JSON.parse(message);
							 var nomeAggiudicatario = parsedJsonDettaglioAsta.nomeAggiudicatario;
							 var indirizzoSpedizione = parsedJsonDettaglioAsta.indirizzoSpedizione;
							 var asta = parsedJsonDettaglioAsta.asta; //Asta
							 var prezzo = parsedJsonDettaglioAsta.prezzo;
							 var nomeCreatore = parsedJsonDettaglioAsta.nomeCreatore;
							 
							//Creazione tabella
							let divDettaglio = document.getElementById("dettaglioAsta");
							let table = document.createElement("table");
							table.border = 1;
							let table_head = document.createElement("thead");
							let th_row = document.createElement("tr");
							let thUtenteCreatore = document.createElement("th");
							thUtenteCreatore.textContent = "Nome creatore";
							th_row.appendChild(thUtenteCreatore);
							let thStart = document.createElement("th");
							thStart.textContent = "Data inizio asta";
							th_row.appendChild(thStart);
							let thEnd = document.createElement("th");
							thEnd.textContent = "Data fine asta";
							th_row.appendChild(thEnd);
							let thPrice = document.createElement("th");
							thPrice.textContent="Prezzo finale";
							th_row.appendChild(thPrice);
							let thAumento = document.createElement("th");
							thAumento.textContent = "Minimo aumento";
							th_row.appendChild(thAumento);
							let thNomeAgg = document.createElement("th");
							thNomeAgg.textContent="Nome aggiudicatario";
							th_row.appendChild(thNomeAgg);
							let thSpedizione = document.createElement("th");
							thSpedizione.textContent = "Indirizzo spedizione";
							th_row.appendChild(thSpedizione);
							table_head.appendChild(th_row);
							table.appendChild(table_head);
							//Body tabella
							let tableBody = document.createElement("tbody");
							let tr = document.createElement("tr");
							let tdName = document.createElement("td");
							tdName.textContent = nomeCreatore;
							tr.appendChild(tdName);
							let tdInizio = document.createElement("td");
							tdInizio.textContent = asta.dateStart;
							tr.appendChild(tdInizio);
							let tdFine = document.createElement("td");
							tdFine.textContent = asta.dateExpiration;
							tr.appendChild(tdFine);
							let tdPrice = document.createElement("td");
							tdPrice.textContent = prezzo;
							tr.appendChild(tdPrice);
							let tdAumento = document.createElement("td");
							tdAumento.textContent = asta.minimumIncrease;
							tr.appendChild(tdAumento);
							let tdNomeAgg = document.createElement("td");
							tdNomeAgg.textContent = nomeAggiudicatario;
							tr.appendChild(tdNomeAgg);
							let tdIndirizzo = document.createElement("td");
							tdIndirizzo.textContent = indirizzoSpedizione;
							tr.appendChild(tdIndirizzo);
							tableBody.appendChild(tr);
							table.appendChild(tableBody);
							divDettaglio.appendChild(table);
							divDettaglio.style.display="block";
							document.getElementById("acquisto").style.display="none";
							document.getElementById("vendo").style.display="none";
							document.getElementById("offerta").style.display="none";
							 break;
						case 400:
							document.getElementById("error").textContent="Errore parametri";
							break;
						case 500:
							document.getElementById("error").textContent="Errore lato server";
							break;
					 }
				 }
			 });
	 }
	 
	function loadDettaglioAstaNotClosed(idAsta){
		
		document.getElementById("error").textContent = "";
		makeCall('GET', "goToDettaglioAsta?id=" + idAsta, null,
		function(x){
		//Funzione creazione pagina dettaglio asta
			if(x.readyState == XMLHttpRequest.DONE){
				var message = x.responseText;
				switch(x.status){
					case 200:
						var offertaDiv=document.getElementById("offerta");
						var acquistoDiv = document.getElementById("acquisto");
						var vendoDiv = document.getElementById("vendo");
						//Resetting the page acquisto
						document.getElementById("auctionWon_body").innerHTML="";
						document.getElementById("auctionList").innerHTML="";
						acquistoDiv.style.display = "none";
						//Resetting the page offerta
						document.getElementById("auctionItem_body").innerHTML="";
						document.getElementById("auctionOffers_body").innerHTML="";
						offertaDiv.style.display = "none";
						//Resetting the page vendo
						document.getElementById("auctionClosed_body").innerHTML="";
						document.getElementById("auctionNotClosed_body").innerHTML="";
						var parsedJsonDettaglioAsta = JSON.parse(message);
						var asta = parsedJsonDettaglioAsta.asta; //Asta
						var listOfferte = parsedJsonDettaglioAsta.listOfferte; //Lista
						var nomeUtente = parsedJsonDettaglioAsta.nomeUtente; //Lista utenti offerta
						var nomeCreatore = parsedJsonDettaglioAsta.nomeCreatore; //Stringa
														
						//Costruzione del div del dettaglio asta
						let br = document.createElement("br");
						let divDettaglio = document.getElementById("dettaglioAsta");
						let h2 = document.createElement("h2");
						h2.textContent="Dati asta";
						divDettaglio.appendChild(h2);
						let table = document.createElement("table");
						table.border = 1;
						//Creazione head tabella
						let table_head = document.createElement("thead");
						let trHead = document.createElement("tr");
						let thUtente_head = document.createElement("th");
						thUtente_head.textContent = "Utente creatore";
						trHead.appendChild(thUtente_head);
						let thDataInizio_head = document.createElement("th");
						thDataInizio_head.textContent = "Data inizio asta";
						trHead.appendChild(thDataInizio_head);
						let thDataFine_head = document.createElement("th");
						thDataFine_head.textContent = "Data fine asta";
						trHead.appendChild(thDataFine_head);
						let thPrezzo = document.createElement("th");
						thPrezzo.textContent="Prezzo attuale";
						trHead.appendChild(thPrezzo);
						let thAumento = document.createElement("th");
						thAumento.textContent="Minimo aumento";
						trHead.appendChild(thAumento);
						table_head.appendChild(trHead);
						table.appendChild(table_head);
						//Creazione body tabella
						let table_body = document.createElement("tbody");
						let trBody=document.createElement("tr");
						let tdNomeCreatore = document.createElement("td");
						tdNomeCreatore.textContent = nomeCreatore;
						trBody.appendChild(tdNomeCreatore);
						let tdDataInizio = document.createElement("td");
						tdDataInizio.textContent = asta.dateStart;
						trBody.appendChild(tdDataInizio);
						let tdDataFine = document.createElement("td");
						tdDataFine.textContent = asta.dateExpiration;
						trBody.appendChild(tdDataFine);
						let tdCurrentPrice = document.createElement("td");
						tdCurrentPrice.textContent = asta.currentPrice;
						trBody.appendChild(tdCurrentPrice);
						let tdAumento = document.createElement("td");
						tdAumento.textContent = asta.minimumIncrease;
						trBody.appendChild(tdAumento);
						table_body.appendChild(trBody);
														
						table.appendChild(table_body);
						divDettaglio.appendChild(table);
						divDettaglio.appendChild(br);
						divDettaglio.appendChild(br);
						if(listOfferte!=null){
							//Tabella offerte
							let table_offerte = document.createElement("table");
							table_offerte.border = 1;
							//Creazione head tabella
							let head = document.createElement("thead");
							let trHead_offerte = document.createElement("tr");
							let thNomeUtente_offerte = document.createElement("th");
							thNomeUtente_offerte.textContent = "Nome utente";
							trHead_offerte.appendChild(thNomeUtente_offerte);
							let thPrezzo_offerte = document.createElement("th");
							thPrezzo_offerte.textContent = "Prezzo offerto";
							trHead_offerte.appendChild(thPrezzo_offerte);
							let thData_offerte = document.createElement("th");
							thData_offerte.textContent = "Data e ora";
							trHead_offerte.appendChild(thData_offerte);
							head.appendChild(trHead_offerte);
							table_offerte.appendChild(head);
							//Creazione body tabella
							let tableBody_offerte = document.createElement("tbody");
							for(var i=0; i<listOfferte.length; i++){
								let trBody_offerte = document.createElement("tr");
								let tdNomeUtente_offerte = document.createElement("td");
								tdNomeUtente_offerte.textContent = nomeUtente[i].username;
								trBody_offerte.appendChild(tdNomeUtente_offerte);
								let tdPrezzo_offerte = document.createElement("td");
								tdPrezzo_offerte.textContent = listOfferte[i].offerPrice;
								trBody_offerte.appendChild(tdPrezzo_offerte);
								let tdData_offerte = document.createElement("td");
								tdData_offerte.textContent = listOfferte[i].dateHour;
								trBody_offerte.appendChild(tdData_offerte);
								tableBody_offerte.appendChild(trBody_offerte);
							}
							table_offerte.appendChild(tableBody_offerte);
							divDettaglio.appendChild(table_offerte);
							divDettaglio.appendChild(br);
							divDettaglio.appendChild(br);
						}						
						//Creazione form di chiusura asta
						let h2Chiusura = document.createElement("h2");
						h2Chiusura.textContent="Chiudi asta";
						divDettaglio.appendChild(h2Chiusura);
						let formChiusura = document.createElement("form");
						formChiusura.action = "#";
						let buttonChiusura = document.createElement("button");
						buttonChiusura.type = "button";
						buttonChiusura.textContent="Chiudi asta";
						buttonChiusura.addEventListener("click", (e)=>{
							chiudiAsta(idAsta);
						});
						formChiusura.appendChild(buttonChiusura);
						divDettaglio.appendChild(formChiusura);
						divDettaglio.style.display="block";
						//Setting the other divs not visible
						document.getElementById("acquisto").style.display="none";
						document.getElementById("vendo").style.display="none";
						document.getElementById("offerta").style.display="none";
						break;
					case 400:
						document.getElementById("error").textContent="Errore parametri";
						break;
					case 403: //forbidden
						document.getElementById("error").textContent = "Operazione non permessa";
						break;
					case 500:
						document.getElementById("error").textContent="Errore lato server"
						break;
					}
				}
			});
		}
	 
	 //Funzione per chiudere l'asta
	 function chiudiAsta(idAsta){
		 document.getElementById("error").textContent = "";
		 makeCall('POST', "chiudiAsta?idAsta="+idAsta, null,
		 function(x){
			 
			 if(x.readyState==XMLHttpRequest.DONE){
				 switch(x.status){
					 case 200:
						 //Asta chiusa
						 //Ricarico la pagina dettaglio asta chiusa
						 saveLastAction(sessionStorage.getItem("username"), "Chiusura asta", null);
						 document.getElementById("dettaglioAsta").innerHTML="";
						 loadDettaglioAstaClosed(idAsta);
						 break;
					case 400:
						document.getElementById("error").textContent="Parametri errati";
						break;
					case 403:
						document.getElementById("error").textContent="Non è possibile chiudere l'asta";
						break;
					case 500:
						document.getElementById("error").textContent = "Errore lato server";
						break;
				 }
			 }
		 });
	 }
	 
	 
	  //Funzione che gestisce tutti gli eventi nella pagina
	 function handleEventVendo(event){
		 
			 
			 if(event.type == "click"){
				var offertaDiv=document.getElementById("offerta");
		 		var acquistoDiv = document.getElementById("acquisto");
		 		var vendoDiv = document.getElementById("vendo");
				//Resetting the page acquisto
				document.getElementById("auctionWon_body").innerHTML="";
				document.getElementById("auctionList").innerHTML="";
				acquistoDiv.style.display = "none";
				//Resetting the page offerta
		 		document.getElementById("auctionItem_body").innerHTML="";
				document.getElementById("auctionOffers_body").innerHTML="";
				offertaDiv.style.display = "none";
				//Resetting the page vendo
				document.getElementById("auctionClosed_body").innerHTML="";
				document.getElementById("auctionNotClosed_body").innerHTML="";
				vendoDiv.style.display ="block";
				//Resetting the page dettaglio asta
				document.getElementById("error").innerHTML="";
				document.getElementById("dettaglioAsta").style.display="none";
				document.getElementById("dettaglioAsta").innerHTML="";
		 		//Reloading the page vendo
		 		loadVendo();
			 }
		 } 
	 
		 document.getElementById("VendoButton").addEventListener("click",(e)=>{
			 handleEventVendo(e);
		 });
		 
		 //Funzione per salvare l'ultima azione
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