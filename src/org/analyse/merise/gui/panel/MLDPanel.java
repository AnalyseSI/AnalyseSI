/*
 * 02 sep 2010  - 11:45:38
 * 
 * MLDPanel - Copyright (C) 2010 Bruno Dabo
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
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.analyse.core.gui.action.BasicAction;
import org.analyse.core.gui.menu.ClipboardPopupMenu;
import org.analyse.core.modules.AnalysePanel;
import org.analyse.core.modules.ClipboardInterface;
import org.analyse.core.util.Constantes;
import org.analyse.core.util.GUIUtilities;
import org.analyse.core.util.Utilities;
import org.analyse.core.util.save.AnalyseSave;
import org.analyse.core.util.save.FileChooserFilter;
import org.analyse.main.Main;
import org.analyse.merise.gui.dialog.ConnectionDialog;
import org.analyse.merise.mcd.composant.MLDCommand;

public class MLDPanel  extends AnalysePanel implements Observer, ClipboardInterface{
	private ActionHandler actionHandler;

	private BasicAction connexion, deconnexion, exec, save;

	private ConnectionDialog connDialog;

	private JPopupMenu popup;

	private JPanel toolbar, statePanel;

	private JEditorPane editor;

	private JTextField state;

	private JFileChooser chooser;

	private Map requestsSelected;
	private MLDCommand mldCommand  ;  
	
	private Map requestsColorized;

	public void cut() {
		//System.out.println("Cut");
	}

	public MLDPanel(MLDCommand mldCommand) {
		super("MLD");

		this.actionHandler = new ActionHandler();
		this.mldCommand = mldCommand;

		requestsSelected = new HashMap();
		requestsColorized = new HashMap();

		mldCommand.addObserver(this);

		initAction();
		initToolbar();
		initStatebar();

		editor = new JEditorPane("text/html", "");
		editor.setEditable(false);

		popup = new ClipboardPopupMenu(false, true, false);
		editor.addMouseListener(new MouseHandler());
		editor.setBackground(Constantes.COULEUR_FOND_MLD) ;
		chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileHidingEnabled(true);

		FileChooserFilter cf;
		chooser.resetChoosableFileFilters();
		cf = new FileChooserFilter("MLD");
		cf.setExtension("mld");
		cf.setDescription("Fichier script MLD");
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
		toolbar.add(new JButton(save) {
			{
				addMouseListener(Main.statusbar.getHandler());
			}
		});
	}

	private void initAction() {

		save = new BasicAction(
				null,
				Utilities.getLangueMessage(Constantes.MESSAGE_SAUVEGARDER_SCRIPT_MLD),
				"SAVEMLD", GUIUtilities.getImageIcon(Constantes.FILE_PNG_SAVE), 0, null);
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
	
	public void update(Observable o, Object arg) {
		String name, str, text, textFinal, requete;

		//textFinal = "<html><body style=\"font-family:Geneva,Arial,Helvetica,sans-serif;font-size:11px;\">";
		textFinal = "<html><body><PRE>";
		 
		text = mldCommand.getRequests() ;
		
		for (StringTokenizer st = new StringTokenizer(text, " (),<>;", true); st
					.hasMoreElements();) {
				str = st.nextToken() ;
					
					str = str.replace(";", "<br/>") ;
					
					/*if (keywords.contains(str))
						textFinal += "<b style=\"color: blue;\">" + str + "</b>";
					else if (types.contains(str))
						textFinal += "<b style=\"color: red;\">" + str + "</b>";
					else if (str.equals("(") || str.equals(")"))
						textFinal += "<b>" + str + "</b>";
					else if (str.equals(";"))
						textFinal += ";<br/><br/>";
					else    */
						textFinal += str;
				
			}

		textFinal += "</PRE></body></html>";
		
		editor.setText(textFinal);
		 
	}
	
	public void copy() {
		editor.copy();
	}

	public void paste() {
		// System.out.println("Paste");
	}

	private String chooseFile() {
		if (chooser.showDialog(org.analyse.main.Main.analyseFrame, null) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile().getAbsolutePath();
		}

		return null;
	}

	private class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			String str, textFinal, text;

			if (action.equals("SAVEMLD")) {
				String fileName = chooseFile();
				if (fileName == null)
					return;

				try {
					PrintStream out = new PrintStream(new FileOutputStream(
							fileName));

					textFinal = "";
					text = mldCommand.getRequests();

					for (StringTokenizer st = new StringTokenizer(text, ";",
							true); st.hasMoreElements();) {
						str = st.nextToken();

						str = str.replaceAll("<u>", ""); 
						str = str.replaceAll("</u>", "");
						
						if (str.equals(";"))
							textFinal += Utilities.newLine() ;
						else
							textFinal += str;
					}

					out.print(textFinal);

					out.close();

				} catch (IOException err) {
		        	Object[] messageArguments = { fileName } ; 		        	
					GUIUtilities.error(Utilities.getLangueMessageFormatter (Constantes.MESSAGE_IMPOSSIBLE_SAVE_FICHIER, messageArguments) )  ;
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
