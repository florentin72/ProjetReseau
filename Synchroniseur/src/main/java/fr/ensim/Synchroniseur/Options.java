package fr.ensim.Synchroniseur;

public enum Options {

	defaut, w, e, s, we, ws, es, wes;

	public Options ajouter(Options o) {
		if(o.equals(Options.defaut)) { //Si on demande à ajouter l'option par défaut
			return this;
		}
			
		switch(this) {
		case defaut: //Si on avait pas d'options
			return o; // On prend l'option demandée
			
		case w:
			switch(o) {
			case w: return this;
			case e: return we;
			case ws: return ws;
			case s: return ws;
			default : return wes;
			}
			
		case e:
			switch(o) {
			case e: return e;
			case s:	return es;
			case es: return es;
			case w:	return we;
			case we: return we;
			default : return wes;
			}
			
		case s:
			switch(o) {
			case s: return s;
			case e: return es;
			case es: return es;
			case w: return ws;
			case ws: return ws;	
			default : return wes;
			}
			
		case we:
			switch(o) {
			case e: return we;
			case w: return we;
			case we: return we;
			default : return wes;
			}
			
		case ws:
			switch(o) {
			case s: return ws;
			case w: return ws;
			case ws: return ws;
			default : return wes;
			}
			
		case es:
			switch(o) {
			case e:	return es;
			case es: return es;
			case s:	return es;
			default : return wes;
			}
			
		case wes:
			return this;
		}
		
		return this;
	} 
}