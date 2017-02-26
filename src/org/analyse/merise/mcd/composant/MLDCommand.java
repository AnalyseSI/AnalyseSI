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

package org.analyse.merise.mcd.composant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


public class MLDCommand {
	
	private int state;
	private String error;
	private int errorCode;
	private ObservableMDL observableMDL;
	private List<String> requests;

	public MLDCommand() {
		observableMDL = new ObservableMDL();
		requests = new ArrayList<String>();	
	}

	/**
	 * Ajoute un observer qui permet d'avertir les autres classes d'un ajout ou
	 * d'une suppression d'une requête.
	 */
	public void addObserver(Observer obs) {
		observableMDL.addObserver(obs);
	}

	private class ObservableMDL extends Observable {
		public void notifyObservers() {
			setChanged();
			super.notifyObservers();
		}
	}
	
	/**
	 * Supprime toutes les requetes.
	 */
	public void clear() {
		requests.clear();
		observableMDL.notifyObservers();
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
		observableMDL.notifyObservers();
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
	 * Retourne l'état de la connection.
	 */
	public int getState() {
		return state;
	}
 
}