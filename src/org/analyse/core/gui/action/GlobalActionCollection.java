/*
 * 05/21/2003 - 13:55:17
 *
 * GlobalActionCollection.java -
 * Copyright (C) 2003 Dreux Loic
 * dreuxl@free.fr
 *
 *Modifications : 
 *  Date : 2009 avril / bruno.dabo@lywoonsoftware.com  => multi-langage
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

package org.analyse.core.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.analyse.core.modules.AnalysePanel;
import org.analyse.core.util.Constantes;
import org.analyse.core.util.GUIUtilities;
import org.analyse.core.util.Utilities;
import org.analyse.main.Main;

/**
 * Cette classe regroupe toutes les actions globales.
 */
public class GlobalActionCollection implements Observer {
	private Map<Object, BasicAction> actionCollection;

	private MainActionListener mainActionListener;

	/**
	 * Créer les différentes actions.
	 */
	public GlobalActionCollection() {
		mainActionListener = new MainActionListener();
		actionCollection = new HashMap<Object, BasicAction>();

		BasicAction action;

		action = new BasicAction(
				Utilities.getLangueMessage(Constantes.MESSAGE_NOUVEAU), 
				Utilities.getLangueMessage(Constantes.MESSAGE_CREER_DOCUMENT) , Constantes.NEW,
				GUIUtilities.getImageIcon(Constantes.FILE_PNG_NEW), 'n', KeyStroke
						.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		action.addActionListener(mainActionListener);
		actionCollection
				.put(action.getValue(Action.ACTION_COMMAND_KEY), action);

		action = new BasicAction(
				Utilities.getLangueMessage(Constantes.MESSAGE_OUVRIR), 
				Utilities.getLangueMessage(Constantes.MESSAGE_OUVRIR_DOCUMENT) ,
				Constantes.OPEN, GUIUtilities.getImageIcon(Constantes.FILE_PNG_OPEN), 'o', KeyStroke
						.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		action.addActionListener(mainActionListener);
		actionCollection
				.put(action.getValue(Action.ACTION_COMMAND_KEY), action);

		action = new BasicAction(
				Utilities.getLangueMessage(Constantes.MESSAGE_SAUVEGARDER), 
				Utilities.getLangueMessage(Constantes.MESSAGE_SAUVEGARDER_DOCUMENT), 				
				Constantes.SAVE, GUIUtilities.getImageIcon(Constantes.FILE_PNG_SAVE ), 's', KeyStroke
						.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		action.addActionListener(mainActionListener);
		actionCollection
				.put(action.getValue(Action.ACTION_COMMAND_KEY), action);

		action = new BasicAction(
				Utilities.getLangueMessage(Constantes.MESSAGE_SAVEAS), 
				Utilities.getLangueMessage(Constantes.MESSAGE_SAVEAS_DOCUMENT), 
				Constantes.SAVEAS,
				GUIUtilities.getImageIcon(Constantes.FILE_PNG_SAVEAS ), 'a', KeyStroke
						.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		action.addActionListener(mainActionListener);
		actionCollection
				.put(action.getValue(Action.ACTION_COMMAND_KEY), action);		

		action = new BasicAction(
				Utilities.getLangueMessage(Constantes.MESSAGE_QUITTER), 
				Utilities.getLangueMessage(Constantes.MESSAGE_QUITTER_APPLICATION ),
				Constantes.QUIT,
				null, 'q', KeyStroke.getKeyStroke(KeyEvent.VK_Q,
						ActionEvent.CTRL_MASK));
		action.addActionListener(mainActionListener);
		actionCollection
				.put(action.getValue(Action.ACTION_COMMAND_KEY), action);

		action = new BasicAction(
				Utilities.getLangueMessage(Constantes.MESSAGE_ANNULER), 
				Utilities.getLangueMessage(Constantes.MESSAGE_ANNULER_DERNIERE_OPERATION),				
				Constantes.UNDO, GUIUtilities.getImageIcon(Constantes.FILE_PNG_UNDO ), 'a', KeyStroke
						.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		action.addActionListener(mainActionListener);
		actionCollection
				.put(action.getValue(Action.ACTION_COMMAND_KEY), action);

		action = new BasicAction(
				Utilities.getLangueMessage(Constantes.MESSAGE_RETABLIR), 
				Utilities.getLangueMessage(Constantes.MESSAGE_RETABLIR_DERNIERE_OPERATION),				
				Constantes.REDO, GUIUtilities.getImageIcon(Constantes.FILE_PNG_REDO ), 'r', KeyStroke
						.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		action.addActionListener(mainActionListener);
		actionCollection
				.put(action.getValue(Action.ACTION_COMMAND_KEY), action);

		action = new BasicAction(
				Utilities.getLangueMessage(Constantes.MESSAGE_COUPER), 
				Utilities.getLangueMessage(Constantes.MESSAGE_COUPER_COLLER),				
				Constantes.CUT, GUIUtilities.getImageIcon(Constantes.FILE_PNG_CUT ), 'c', KeyStroke
						.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		action.addActionListener(mainActionListener);
		actionCollection
				.put(action.getValue(Action.ACTION_COMMAND_KEY), action);

		action = new BasicAction(
				Utilities.getLangueMessage(Constantes.MESSAGE_COLLER), 
				Utilities.getLangueMessage(Constantes.MESSAGE_COLLER_ELEMENT),
				Constantes.PASTE, GUIUtilities.getImageIcon(Constantes.FILE_PNG_PASTE ), 'l', KeyStroke
						.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		action.addActionListener(mainActionListener);
		actionCollection
				.put(action.getValue(Action.ACTION_COMMAND_KEY), action);

		action = new BasicAction(
				Utilities.getLangueMessage(Constantes.MESSAGE_COPIER), 
				Utilities.getLangueMessage(Constantes.MESSAGE_COPIER_ELEMENT),
				Constantes.COPY, GUIUtilities.getImageIcon(Constantes.FILE_PNG_COPY), 'p', KeyStroke
						.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		action.addActionListener(mainActionListener);
		actionCollection
				.put(action.getValue(Action.ACTION_COMMAND_KEY), action);

		action = new BasicAction(
				Utilities.getLangueMessage(Constantes.MESSAGE_AFFICHE_NAVIGATEUR),
				Utilities.getLangueMessage(Constantes.MESSAGE_AFFICHE_NAVIGATEUR),
				Constantes.SHOWHIDE_NAVIGATOR, GUIUtilities
						.getImageIcon(Constantes.FILE_PNG_TREE), 0, null);
		action.addActionListener(mainActionListener);
		actionCollection
				.put(action.getValue(Action.ACTION_COMMAND_KEY), action);
		
		action = new BasicAction(
				Utilities.getLangueMessage(Constantes.MESSAGE_PARAMETRAGE),
				Utilities.getLangueMessage(Constantes.MESSAGE_PARAMETRAGE),
				Constantes.PARAMETRAGE, GUIUtilities
				.getImageIcon(Constantes.FILE_PNG_PARAMETRAGE), 0, null);
		action.addActionListener(mainActionListener);
		actionCollection.put(action.getValue(Action.ACTION_COMMAND_KEY), action);				
		
		action = new BasicAction(
				Utilities.getLangueMessage(Constantes.MESSAGE_APROPOS),
				Utilities.getLangueMessage(Constantes.MESSAGE_APROPOS),
				Constantes.ABOUT, GUIUtilities
				.getImageIcon(Constantes.FILE_PNG_ABOUT), 0, null);
		action.addActionListener(mainActionListener);
		actionCollection
				.put(action.getValue(Action.ACTION_COMMAND_KEY), action);
		
	}

	/**
	 * Retourne une BasicAction utilisable dans un composant graphique.
	 * 
	 * @param ID
	 *            identifiant de l'action
	 */
	public BasicAction getAction(String ID) {
		return actionCollection.get(ID);
	}

	/**
	 * Met à jour l'interface : active ou désactive les boutons Undo, Redo, Cut,
	 * Copy et Paste.
	 */
	public void update(Observable o, Object arg) {
		AnalysePanel p = Main.analyseFrame.getCurrentPanel();

		getAction(Constantes.REDO).setEnabled(p.getRedoEnabled());
		getAction(Constantes.UNDO).setEnabled(p.getUndoEnabled());

		getAction(Constantes.CUT).setEnabled(p.getCopyEnabled() && p.getPasteEnabled());
		getAction(Constantes.COPY).setEnabled(p.getCopyEnabled());
		getAction(Constantes.PASTE).setEnabled(p.getPasteEnabled());
	}
}