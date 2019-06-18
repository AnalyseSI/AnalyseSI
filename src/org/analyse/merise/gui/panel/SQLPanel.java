/*
 * 03/01/2004 - 11:45:38
 * 
 * SQLPanel.java - Copyright (C) 2004 Dreux Loic dreuxl@free.fr
 * 
 *  *  Modifications 
 *  -------------
 *  @date : 2009 janvier 24
 *  @auteur : Bruno Dabo <bruno.dabo@lywoonsoftware.com>
 *  
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.analyse.merise.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.analyse.core.gui.action.BasicAction;
import org.analyse.core.gui.menu.ClipboardPopupMenu;
import org.analyse.core.modules.AnalysePanel;
import org.analyse.core.modules.ClipboardInterface;
import org.analyse.core.util.Constantes;
import org.analyse.core.util.GUIUtilities;
import org.analyse.core.util.SwingWorker;
import org.analyse.core.util.Utilities;
import org.analyse.core.util.save.AnalyseFilter;
import org.analyse.core.util.save.AnalyseSave;
import org.analyse.core.util.save.FileChooserFilter;
import org.analyse.main.Main;
import org.analyse.merise.gui.dialog.ConnectionDialog;
import org.analyse.merise.sql.SQLCommand;

public class SQLPanel extends AnalysePanel
	implements Observer, ClipboardInterface {
	private ActionHandler actionHandler;

	private BasicAction connexion, deconnexion, exec, save;

	private ConnectionDialog connDialog;

	private JPopupMenu popup;

	private SQLCommand sqlCommand;

	private JPanel panel, toolbar, statePanel;

	private JEditorPane editor;

	private JTextField state;

	private JFileChooser chooser;

        private JComboBox jrbSQLSyntax;

	private Map requestsSelected;

	private Map requestsColorized;

	public SQLPanel(SQLCommand sqlCommand) {
		super("SQL");

		this.actionHandler = new ActionHandler();
		this.sqlCommand = sqlCommand;
		this.connDialog = new ConnectionDialog(sqlCommand);

		requestsSelected = new HashMap();
		requestsColorized = new HashMap();

		sqlCommand.addObserver(this);

		initAction();
		initToolbar();
		initStatebar();

		editor = new JEditorPane("text/html", "");
		editor.setEditable(false);

		popup = new ClipboardPopupMenu(false, true, false);
		editor.addMouseListener(new MouseHandler());
		editor.setBackground(Constantes.COULEUR_FOND_SQL) ;
		
		chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileHidingEnabled(true);

		AnalyseFilter af;
		FileChooserFilter cf;
		chooser.resetChoosableFileFilters();
		cf = new FileChooserFilter("SQL");
		cf.setExtension("sql");
		cf.setDescription(Utilities.getLangueMessage("fichier_script_sql"));
		chooser.addChoosableFileFilter(cf);
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);

		this.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 3));
		this.setLayout(new BorderLayout());
		this.add(BorderLayout.NORTH, toolbar);
		this.add(BorderLayout.CENTER, new JScrollPane(editor));
		this.add(BorderLayout.SOUTH, statePanel);
                
                editor.setFocusable(true);
                editor.addKeyListener(new KeyHandler());
	}

	private void initToolbar() {
		toolbar = new JPanel();
		toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));

		toolbar.add(new JButton(connexion) {

			{
				addMouseListener(Main.statusbar.getHandler());
			}
		});

		toolbar.add(new JButton(deconnexion) {

			{
				setText("");
				addMouseListener(Main.statusbar.getHandler());
			}
		});

		toolbar.add(new JToolBar.Separator());

		toolbar.add(new JButton(exec) {
			{
				addMouseListener(Main.statusbar.getHandler());
			}
		});

		toolbar.add(new JButton(save) {
			{
				addMouseListener(Main.statusbar.getHandler());
			}
		});

        // SQL Syntax selection combobox.
        jrbSQLSyntax = new JComboBox(SQLCommand.SQLsyntax.values());
        jrbSQLSyntax.setSelectedIndex(0);
        toolbar.add(new JToolBar.Separator());
        toolbar.add(new JLabel(Utilities.getLangueMessage("sql_syntax")));
        toolbar.add(jrbSQLSyntax);
        this.jrbSQLSyntax.requestFocus(true);
	}

	private void initAction() {
		
		connexion = new BasicAction(null, Utilities.getLangueMessage("connexion_avec_sgbd"),
				"CONN", GUIUtilities.getImageIcon(Constantes.FILE_PNG_CONNEXION), 0, null);
		connexion.addActionListener(actionHandler);

		deconnexion = new BasicAction("Déconnexion", "Déconnexion",
				"DECONN", GUIUtilities.getImageIcon(Constantes.FILE_PNG_DECONNEXION), 0,
				null);
		deconnexion.addActionListener(actionHandler);

		exec = new BasicAction(null, Utilities.getLangueMessage(Constantes.MESSAGE_EXECUTER_REQUETE_SQL), "EXEC",
				GUIUtilities.getImageIcon("build.png"), 0, null);
		exec.addActionListener(actionHandler);

		save = new BasicAction(null,
				Utilities.getLangueMessage("sauvegarder_script_sql"), "SAVESQL",
				GUIUtilities.getImageIcon(Constantes.FILE_PNG_SAVE), 0, null);
		save.addActionListener(actionHandler);
	}

	private void initStatebar() {
		statePanel = new JPanel();
		statePanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 3));

		state = new JTextField("");
		state.setEditable(false);
		state.setBackground(Color.WHITE);
		statePanel.setLayout(new BorderLayout());
		statePanel.add(BorderLayout.CENTER, state);
	}

	/**
	 * A optimiser
	 */
	public void update(Observable o, Object arg) {
		String name, str, text, textFinal, requete;

		List<String> keywords = sqlCommand.getKeywords();
		List<String> types = sqlCommand.getTypes();

		//textFinal = "<html><body style=\"font-family:Geneva,Arial,Helvetica,sans-serif;font-size:11px;\">";
		textFinal = "<html><body><pre>";
		text = sqlCommand.getRequests() ;
		
		for (StringTokenizer st = new StringTokenizer(text, " (),<>;", true); st
					.hasMoreElements();) {
				str = st.nextToken() ;
					
					str = str.replace(",", ",<br/>") ;
					
					if (keywords.contains(str))
						textFinal += "<b style=\"color: blue;\">" + str + "</b>";
					else if (types.contains(str))
						textFinal += "<b style=\"color: red;\">" + str + "</b>";
					else if (str.equals("(") || str.equals(")"))
						textFinal += "<b>" + str + "</b>";
					else if (str.equals(";"))
						textFinal += ";<br/><br/>";
					else
						textFinal += str;
				
			}

		textFinal += "</pre></body></html>";
		textFinal = textFinal.replace("<br/><br/><b style=\"color: blue;\">CREATE", "<br/><b style=\"color: blue;\">CREATE") ;
		textFinal = textFinal.replace("<b>)</b>;<br/><br/>", "<b>)</b>;<br/>" ) ;
		
		editor.setText(textFinal);

		state.setText(sqlCommand.getLabelState());
	}

	public void cut() {
		//System.out.println("Cut");
	}

	public void copy() {
		editor.copy();
	}

	public void paste() {
		//System.out.println("Paste");
	}

	private String chooseFile() {
		if (chooser.showDialog(org.analyse.main.Main.analyseFrame, null) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile().getAbsolutePath();
		}

		return null;
	}

	public void execRequest() {
		if (!sqlCommand.execRequest()) {
			if (sqlCommand.getErrorCode() == 0) {
				if (GUIUtilities.question_YES_NO  (Constantes.MESSAGE_TABLE_EXISTE_DEJA) == JOptionPane.YES_OPTION ) {
					if (sqlCommand.execRequest(true))						
						GUIUtilities.messageHTML(Utilities.getLangueMessage(Constantes.MESSAGE_TABLE_CREATION_OK), true, 300, 250);
					else
						GUIUtilities.error(Utilities.getLangueMessage(Constantes.MESSAGE_IMPOSSIBLE_CREER_TABLE));
				}
			} else {
				GUIUtilities.error(Utilities.getLangueMessage(Constantes.MESSAGE_IMPOSSIBLE_CREER_TABLE));
			}
		} else {
			GUIUtilities.messageHTML(Utilities.getLangueMessage(Constantes.MESSAGE_TABLE_CREATION_OK), true, 300, 250);
		}
	}


	private class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			String str, textFinal, text, oldStr;
			boolean firstAlter = false ;

			if (action.equals("CONN")) {
				if (sqlCommand.getState() == SQLCommand.DECONNECTED)
					connDialog.setVisible(true);
				else
					  //"Veuillez-vous déconnecter avant de tenter une nouvelle connexion."
					GUIUtilities.error ( Utilities.getLangueMessage("deconnecter_new_connexion"));
			} else if (action.equals("DECONN")) {
				sqlCommand.deconnection();
			} else if (action.equals("EXEC")) {
				if (sqlCommand.getState() == SQLCommand.DECONNECTED) {
					GUIUtilities.error("Veuillez-vous connecter avant d'exécuter les requêtes.");
				} else {
					SwingWorker worker = new SwingWorker() {
						public Object construct() {
							execRequest();

							return null;
						}
					};
					worker.start();

				}
			} else if (action.equals("SAVESQL")) {
				oldStr = "";
				String fileName = chooseFile();
				if (fileName == null)
					return;

				try {
					PrintStream out = new PrintStream(new FileOutputStream(
							fileName));

					textFinal = "";
					text = sqlCommand.getRequests();
					firstAlter = false ;
					
					for (StringTokenizer st = new StringTokenizer(text, ";",
							true); st.hasMoreElements();) {
						str = st.nextToken();
						str = str.replace(",", "," + Utilities.newLine() ) ;

						if (str.equals(";"))
							if ( oldStr.startsWith("DROP") || oldStr.startsWith("ALTER") )
								
								if ( ( oldStr.startsWith("ALTER") ) && ( firstAlter == false ) ) {
									textFinal += ";" + Utilities.newLine() + Utilities.newLine();
									firstAlter = true ; 
								} else
									textFinal += ";" + Utilities.newLine() ;
							else
								textFinal += ";" + Utilities.newLine() + Utilities.newLine(); 
						else {
							textFinal += str;
							oldStr = str ; 
						}
					}

					out.print(textFinal);

					out.close();

				} catch (IOException err) {
					GUIUtilities.error("Impossible de sauvegarder le fichier "
							+ fileName);
				}
			}
		}
	}

	private class MouseHandler extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger())
				popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

    /**
     * Returns the selected SQL syntax.
     * @return SQL syntax: 'MySQL' or 'PostgreSQL'
     */
    public String getSQLSyntax(){
        return jrbSQLSyntax.getSelectedItem().toString();
    }
    
    private class KeyHandler extends KeyAdapter
        {
            int lasttyped;
            public void keyPressed(KeyEvent ke){
                if((int)ke.getKeyCode() == 17){
                    lasttyped = 17;
                }
                if((int)ke.getKeyCode() == 83 && lasttyped == 17){
                    AnalyseSave s = Main.analyseFrame.getAnalyseSave();
                    s.save();
                }
                if((int)ke.getKeyCode() != 17){
                    lasttyped = 0;
                }
            }
        }
}
