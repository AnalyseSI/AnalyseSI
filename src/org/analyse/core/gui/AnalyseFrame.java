/*
 * 05/15/2003 - 22:04:55
 *
 * AnalyseFrame.java - 
 * Copyright (C) 2003 Dreux Loic
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

package org.analyse.core.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.analyse.core.gui.action.BasicAction;
import org.analyse.core.gui.action.MainActionListener;
import org.analyse.core.gui.action.NavigationActionFactory;
import org.analyse.core.gui.menu.AnalyseMenu;
import org.analyse.core.gui.panel.HelpPanel;
import org.analyse.core.gui.panel.Navigator;
import org.analyse.core.gui.toolbar.AnalyseToolbar;
import org.analyse.core.modules.AnalysePanel;
import org.analyse.core.util.Constantes;
import org.analyse.core.util.GUIUtilities;
import org.analyse.core.util.Utilities;
import org.analyse.core.util.save.AnalyseSave;
import org.analyse.main.Main;

/**
 * Fenetre principale d'AnalyseSI
 */
public class AnalyseFrame extends JFrame {
	
	public static final String DEFAULT = Constantes.DEFAULT ; 
	public static final String HELP = Constantes.HELP;

	/* Menu + Toolbar */
	private AnalyseMenu menu;

	private AnalyseToolbar toolbar;

	private AnalyseBar analyseBar;

	/* Listener */
	private MainActionListener actionListener;

	/* Sauvegarde */
	private Properties props;

	private AnalyseSave analyseSave;

	/* Panel */
	private Navigator navigator;

	private JPanel center;

	private AnalysePanel panelCurrent;

	private AnalysePanel helpPanel;

	/* Factory */
	private NavigationActionFactory navigationActionFactory;

	/**
	 * Crée une nouvelle Fenetre AnalyseSI
	 */
	public AnalyseFrame() {
		/* Construction d'AnalyseFrame */
		super(Utilities.getRelease() + " - sans nom");
		this
				.setIconImage(GUIUtilities.getImageIcon(Constantes.FILE_PNG_ANALYSESI)
						.getImage());

		/* Gestion des évènements */
		this.addWindowListener(new WindowHandler());
		actionListener = new MainActionListener();
		navigationActionFactory = new NavigationActionFactory(this);

		/* Constuction des Menus */
		menu = new AnalyseMenu();
		menu.init();
		toolbar = new AnalyseToolbar();
		Main.splash.setProgress(30);

		/* Constuction du Panel d'aide */
		helpPanel = new HelpPanel();
		Main.splash.setProgress(40);

		/* Container */
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());

		/* Nord */
		c.add(BorderLayout.NORTH, new JPanel(new BorderLayout()) {
			{
				this.add(BorderLayout.CENTER, new JPanel(new BorderLayout()) {
					{
						this.add(BorderLayout.NORTH, menu.getMenuBar());
						this.add(BorderLayout.SOUTH, toolbar);
					}
				});
				//this.add(BorderLayout.EAST, new AnalyseBar(Constantes.RELEASE));
				this.setBorder(BorderFactory.createEtchedBorder());
			}
		});
		/* Centre */
		navigator = new Navigator(this);
		Main.splash.setProgress(50);

		center = new JPanel(new BorderLayout());
		center.add(BorderLayout.WEST, navigator);
		center.add(BorderLayout.CENTER, new JPanel());

		Dimension minimumSize = new Dimension(200, 50);
		navigator.setMinimumSize(minimumSize);
		c.add(BorderLayout.CENTER, center);
		/* Sud */
		c.add(BorderLayout.SOUTH, Main.statusbar);
		Main.splash.setProgress(60);

		/* Chargement du panel d'aide */
                setPanel( helpPanel ) ;
                

		/* Redimensionnement et mise au centre */
		this.setSize(640, 480);

		GUIUtilities.centerComponent(this);
		Main.splash.setProgress(70);

		/* Gestion des sauvegardes */
		analyseSave = new AnalyseSave(this);
		Main.splash.setProgress(80);

		/* Chargement des propriétés */
		props = new Properties();
		loadProperties();
	}

	/**
	 * Méthode exécutée après l'initialisation des modules
	 */
	public void initGUI() {
		navigator.addButton(navigationActionFactory.buildNavigationAction(
				GUIUtilities.getImageIcon("About.png"), 
				Utilities.getLangueMessage(Constantes.MESSAGE_AIDE),
				Utilities.getLangueMessage(Constantes.MESSAGE_AIDE),
				helpPanel));
	}

	/**
	 * Sauvegarde les propriétés dans un fichier.
	 */
	public void saveProperties() {
		try {
			FileOutputStream fos = new FileOutputStream(new File(
					Main.USER_PROPS));

			props.setProperty("X", "" + this.getX());
			props.setProperty("Y", "" + this.getY());
			props.setProperty("width", "" + this.getWidth());
			props.setProperty("height", "" + this.getHeight());
			props.setProperty("tree", "" + toolbar.getTreeSelected());

			props.store(fos, "properties");
		} catch (IOException e) {

		}
	}

	/**
	 * Récupère les propriétés depuis un fichier.
	 */
	public void loadProperties() {
		try {
			FileInputStream fis = new FileInputStream(new File(Main.USER_PROPS));

			props.load(fis);

		} catch (IOException e) {
			initProperties();
		}

		if (!validProperties())
			initProperties();

		this.setBounds(new Integer(props.getProperty("X")).intValue(),
				new Integer(props.getProperty("Y")).intValue(), new Integer(
						props.getProperty("width")).intValue(), new Integer(
						props.getProperty("height")).intValue());
		toolbar.setTreeSelected(new Boolean(props.getProperty("tree"))
				.booleanValue());
		this.showHideNavigator();
	}

	/**
	 * Vérifie que toutes les propriétés sont correctes.
	 */
	public boolean validProperties() {
		return props.getProperty("X") != null && props.getProperty("Y") != null
				&& props.getProperty("height") != null
				&& props.getProperty("width") != null
				&& props.getProperty("tree") != null;
	}

	/**
	 * Initialise les propriétés par défaut.
	 */
	public void initProperties() {
		File dir = new File(Main.SETTINGS_DIRECTORY);
		File file = new File(Main.USER_PROPS);
		FileInputStream stream;

		if (!dir.exists())
			dir.mkdir();

		try {
			FileOutputStream fos = new FileOutputStream(new File(
					Main.USER_PROPS));

			props.setProperty("X", "0");
			props.setProperty("Y", "0");
			props.setProperty("width", "640");
			props.setProperty("height", "480");
			props.setProperty("tree", "true");

			props.store(fos, "properties");
		} catch (IOException e) {
		}
	}

	public void showHideNavigator() {
		if (!toolbar.getTreeSelected())
			center.remove(navigator);
		else
			center.add(BorderLayout.WEST, navigator);

		center.revalidate();
		center.repaint();

	}

	/**
	 * Modifie le panel courant.
	 * 
	 * @param c
	 *            nouveau panel
	 */
	public void setPanel(AnalysePanel panelCurrent) {
		if (this.panelCurrent != null)
			center.remove(this.panelCurrent);

		center.add(BorderLayout.CENTER, panelCurrent);
		center.revalidate();
		center.repaint();

		this.panelCurrent = panelCurrent;
	}

	/**
	 * Retourne le panel courant.
	 */
	public AnalysePanel getCurrentPanel() {
		return panelCurrent;
	}

	/**
	 * Retourne la bar de titre.
	 */
	public AnalyseBar getAnalyseBar() {
		return analyseBar;
	}

	/**
	 * Retourne la classe qui gère les sauvegardes.
	 */
	public AnalyseSave getAnalyseSave() {
		return analyseSave;
	}

	/**
	 * Retourne la fabrique d'action de navigation
	 *
	 */
	public NavigationActionFactory getNavigationActionFactory() {
		return navigationActionFactory;
	}

	/**
	 * Ajoute un menu avant le menu aide
	 * 
	 */
	public void addMenu(JMenu menu) {
		this.menu.addMenu(menu);
	}

	/**
	 * Ajoute un bouton dans la barre d'outil
	 * 
	 */
	public void addButton(JButton button) {
		this.toolbar.addButton(button);
	}

	/**
	 * Ajoute un bouton de navigation
	 * 
	 */
	public void addNavigateButton(BasicAction action) {
		this.navigator.addButton(action);
	}

	/**
	 * Ferme proprement en sauvegardant les paramètres.
	 */
	public boolean exit() {
		
		int  reponse  = analyseSave.closeProgram();

		if ( reponse == JOptionPane.YES_OPTION || reponse == JOptionPane.NO_OPTION  ) {		// Bug #348263	
			saveProperties();
			System.exit(0);
		}
		
		if ( reponse == JOptionPane.CANCEL_OPTION )   // c'est fait exprès ( chemin tortueux à modifier )
			return false ;
		else 
			return true  ;  // Bug #348263

	}

	private class WindowHandler extends WindowAdapter {
		public void windowClosing(WindowEvent evt) {
			boolean sortir = exit();
			
			// Bug #348263
			if ( ! sortir )
				setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			else 
				System.exit(0);			
		}
	}
}