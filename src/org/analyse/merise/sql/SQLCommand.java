/*
 * 03/01/2004 - 12:27:08
 *
 * SQLCommand.java - 
 * Copyright (C) 2004 Dreux Loic
 * dreuxl@free.fr
 * 
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.analyse.merise.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

/**
 * Cette classe fait le lien avec une base de donnée. Elle contient permet
 * d'exécuter les requêtes avec la base de données.
 */
public class SQLCommand {
	public static final int DECONNECTED = 0;

	public static final int CONNECTED = 1;

	private int state;

	private String driver, url, user, password;

	private Connection con;

	private Statement stmt;

	private String error;

	private int errorCode;

	private ObservableSQL observableSQL;

	private List<String> requests;

	private List<String> keywords;

	private List<String> types;

	private List<String> typesWithoutSize;

    // Syntaxes SQL disponibles
    public enum SQLsyntax { MySQL, PostgreSQL, OracleDB}

    public SQLCommand() {
		observableSQL = new ObservableSQL();

		state = DECONNECTED;

		requests = new ArrayList<String>();
		initKeywords();
		initTypes();
	}

	/**
	 * Initialise le Vector contenant les différents mots clé.
	 */
	
	private void initKeywords() {
		keywords = new ArrayList<String>();
		keywords.add("CREATE");
		keywords.add("ALTER");
		keywords.add("SELECT");
		keywords.add("INSERT");

		keywords.add("TABLE");
		keywords.add("VIEW");

		keywords.add("ADD");
		keywords.add("NOT");
		keywords.add("IN");
		keywords.add("AS");
		keywords.add("NULL");
		keywords.add("PRIMARY");
		keywords.add("CONSTRAINT");
		keywords.add("REFERENCES");
		keywords.add("FOREIGN");
		keywords.add("KEY");
		/*
		 * Edité par B. Bouffet le 20/05/2016
		 * Mots-clés spécifiques Oracle Database
		 * Permettent de définir l'équivalent du type MySQL AUTO_INCREMENT
		 * ainsi que de la requête MySQL DROP TABLE IF EXISTS
		 * Génération de code compatible avec Oracle : à venir... 
		 */
		keywords.add("TRIGGER");
		keywords.add("SEQUENCE");
		keywords.add("BEFORE");
		keywords.add("ON");
		keywords.add("FOR");
		keywords.add("EACH");
		keywords.add("ROW");
		keywords.add("BEGIN");
		keywords.add("INTO");
		keywords.add("FROM");
		keywords.add("END");
		
		keywords.add("DECLARE");
		keywords.add("WHERE");
		keywords.add("IF");
		keywords.add("THEN");
		keywords.add("EXECUTE");
		keywords.add("IMMEDIATE");
		keywords.add("DROP");
	}

	/**
	 * Initialise le Vector contenant les différents types SQL.
	 */
	public void initTypes() {
		
		types = new ArrayList<String>();
		
	    types.add("TINYINT") ;
	    types.add("SMALLINT") ;
	    types.add("MEDIUMINT") ;
	    types.add("INT") ;
        types.add("INT_AUTO_INCREMENT") ;  // oups, légère entorse (pour faire plaisir)
        types.add("BIGINT_AUTO_INCREMENT") ;  // oups, légère entorse (pour faire plaisir)
	    types.add("INTEGER") ;
	    types.add("BIGINT") ;
	    types.add("TIMESTAMP") ;
	    types.add("CHAR") ;
	    types.add("VARCHAR") ;
	    types.add("TINYBLOB") ;

	    types.add("FLOAT") ;
	    types.add("DOUBLE PRECISION") ;
	    types.add("DOUBLE") ;
	    types.add("REAL") ;
	    types.add("DECIMAL") ;
	    types.add("NUMERIC") ;

	    types.add("DATE") ;
	    types.add("DATETIME") ;
	    types.add("TIME") ;
	    types.add("YEAR") ;
	    
	    types.add("BIT") ;
	    types.add("BOOL") ;
	    types.add("TINYTEXT") ;
	    types.add("BLOB") ;
	    types.add("TEXT") ;
	    types.add("MEDIUMBLOB") ;
	    types.add("MEDIUMTEXT") ;
	    types.add("LONGBLOB") ;
	    types.add("LONGTEXT") ;

	    types.add("ENUM") ;
	    types.add("SET") ;

        // PostgreSQL specific types.
        types.add("SERIAL");
        types.add("BIGSERIAL");
        types.add("TIMESTAMP");
		
		/*
		 * Edité par B. Bouffet le 12/05/2016
		 * Types spécifiques Oracle Database
		 */
		types.add("BOOLEAN");
		types.add("VARCHAR2");
		types.add("BINARY_FLOAT");
		types.add("BINARY_DOUBLE");
	    
	    // les types sans taille
		typesWithoutSize = new ArrayList<String>();

	    typesWithoutSize.add("BIT") ; 
	    typesWithoutSize.add("BOOL") ; 
	    typesWithoutSize.add("BOOLEAN") ; 
	    typesWithoutSize.add("BLOB") ; 
		typesWithoutSize.add("DATE") ; 
	    typesWithoutSize.add("DATETIME") ; 
	    typesWithoutSize.add("ENUM") ; 
	    typesWithoutSize.add("LONGBLOB") ; 
	    typesWithoutSize.add("LONGTEXT") ; 
	    typesWithoutSize.add("MEDIUMBLOB") ; 
	    typesWithoutSize.add("MEDIUMTEXT") ; 
	    typesWithoutSize.add("SET") ; 
		typesWithoutSize.add("TEXT") ; 
		typesWithoutSize.add("TIME") ; 
		typesWithoutSize.add("TINYTEXT") ; 
	    typesWithoutSize.add("YEAR") ; 
	    
		// tri des données pour affichage ultérieur
		
		Collections.sort(types);
		Collections.sort(typesWithoutSize);
		
	}

	/**
	 * Ajoute un observer qui permet d'avertir les autres classes d'un ajout ou
	 * d'une suppression d'une requete.
	 */
	public void addObserver(Observer obs) {
		observableSQL.addObserver(obs);
	}

	/**
	 * Supprime toutes les requetes.
	 */
	public void clear() {
		requests.clear();
		observableSQL.notifyObservers();
	}

	public String getRequests() {
		String res = "";
		for (Iterator<String> e = requests.iterator(); e.hasNext();) {
			res += e.next();
		}
		return res;
	}

	public void addRequest(String request) {
		requests.add(request);
		observableSQL.notifyObservers();
	}

	public boolean execRequest() {
		return execRequest(false);
	}

	/**
	 * Exécute les différentes requêtes.
	 */
	public boolean execRequest(boolean deleteTable) {
		String line;
		boolean returnTemp = true;
		error = null;

		if (stmt == null) {
			error = "Non connecté à la base ...";
			return false;
		}

		if (!deleteTable) {
			try {
				for (Iterator<String> e = requests.iterator(); e.hasNext();) {
					stmt.executeUpdate(e.next());
				}
			} catch (SQLException e) {
				//e.printStackTrace();

				error = e.getMessage();
				errorCode = e.getErrorCode();

				return false;
			}
		} else {
			Iterator<String> e = requests.iterator();
			line = e.next();

			while (e.hasNext()) {
				try {
					stmt.executeUpdate(line);

					line = e.next();
				} catch (SQLException e2) {
					//e2.printStackTrace();

					if (e2.getErrorCode() == 0 && !deleteTable(e2.getMessage())) {
						return false;

					}
				}
			}
		}

		return true;
	}

	public boolean deleteTable(String errorMessage) {
		String table;

		StringTokenizer tk = new StringTokenizer(errorMessage, "\"");
		tk.nextToken();
		table = tk.nextToken();

		try {
			stmt.executeUpdate("DROP TABLE " + table + " CASCADE;");
		} catch (SQLException e) {
			//e.printStackTrace();

			error = e.getMessage();
			errorCode = e.getErrorCode();

			return false;
		}

		return true;
	}

	/**
	 * Effectue la connection avec la base de donnée.
	 * 
	 * @return Indique si la connection a réussi
	 */
	public boolean connection(String driver, String url, String user,
			String password) {
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.password = password;

		error = null;

		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			error = "Impossible de charger le driver : "
					+ driver
					+ ", vérifiez que le driver JDBC se trouve bien dans le classpath";
			//e.printStackTrace();
            error += e.getMessage() ;
			return false;
		}

		try {
			con = DriverManager.getConnection(url, user, password);
			stmt = con.createStatement();
		} catch (SQLException e) {
			error = "Impossible de se connecter à la base de donnée, vérifier l'URL, le login et le password\net que votre database existe !";
            error += e.getMessage() ;
			//e.printStackTrace();
			return false;
		}

		state = CONNECTED;
		observableSQL.notifyObservers();

		return true;
	}

	/**
	 * Deconnecte.
	 */
	public void deconnection() {
		this.driver = null;
		this.url = null;
		this.user = null;
		this.password = null;

		state = DECONNECTED;
		observableSQL.notifyObservers();
	}

	/**
	 * Permet de récupérer l'erreur lors de la connection ou de l'éxécution des requetes SQL.
	 */
	
	public String getError() {
		return error;
	}

	/**
	 * Permet de récupérer le code d'erreur.
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * Retourne l'état de la connection sous forme de chaine de caractères.
	 */
	
	public String getLabelState() {
		if (state == DECONNECTED)
			return "Déconnecté";
		return "Connecté";
	}

	/**
	 * Retourne l'état de la connection.
	 */
	
	public int getState() {
		return state;
	}

	/**
	 * Retourne les différents mots clés SQL.
	 */
	public List<String> getKeywords() {
		return keywords;
	}

	/**
	 * Retourne les différents types SQL.
	 */
	public List<String> getTypes() {
		return types;
	}

	/**
	 * Retourne les types SQL ne nécessitant pas de tailles
	 * 
	 * @author dreuxl
	 *  
	 */
	public List<String> getTypesWithoutSize() {
		return typesWithoutSize;
	}

	private class ObservableSQL extends Observable {
		public void notifyObservers() {
			setChanged();
			super.notifyObservers();
		}
	}
}
