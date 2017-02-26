/*
 * 06/04/2003 - 13:56:06
 * 
 * MCDPanel.java - Copyright (C) 2003 Dreux Loic dreuxl@free.fr
 * 
 *  * Modifications : 
 * ---------------
 *   Auteur : Bruno Dabo <bruno.dabo@lywoonsoftware.com>
 *   Date   : 2009 jan 22
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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.analyse.core.util.Constantes ;

import javax.imageio.stream.FileImageOutputStream;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.analyse.core.gui.action.BasicAction;
import org.analyse.core.modules.AnalysePanel;
import org.analyse.core.util.GUIUtilities;
import org.analyse.core.util.Utilities;
import org.analyse.core.util.save.AnalyseFilter;
import org.analyse.core.util.save.FileChooserFilter;
import org.analyse.main.Main;
import org.analyse.merise.gui.dialog.EntiteDialog;
import org.analyse.merise.gui.dialog.LienDialog;
import org.analyse.merise.gui.table.DictionnaireTable;
import org.analyse.merise.mcd.composant.MCDAssociation;
import org.analyse.merise.mcd.composant.MCDComponent;
import org.analyse.merise.mcd.composant.MCDEntite;
import org.analyse.merise.mcd.composant.MCDLien;
import org.analyse.merise.mcd.composant.MCDObjet;
import org.analyse.merise.mcd.composant.MLDComponent;
import org.analyse.merise.mcd.composant.MPDComponent;
import org.analyse.merise.sql.SQLCommand;
import org.analyse.merise.mcd.composant.MLDCommand; 

import com.sun.imageio.plugins.png.PNGImageWriter;

public class MCDPanel extends AnalysePanel
{
	private static final long serialVersionUID = -1318663913915137489L;

	private ActionHandler actionHandler;

    private BasicAction addEntite, addAssociation, addLien, changeCurseur;
    private BasicAction modParametrage ;
    private BasicAction delLien, delObjet;
    private BasicAction modEntite, modAssociation, modLien;
    private BasicAction verif, buildMPD, buildMLD ;
    private BasicAction saveGraphic;

    private MCDComponent mcdComponent;
    private MLDComponent mldComponent;
    private MPDComponent mpdComponent;

    private SQLCommand sqlCommand ;
    private MLDCommand mldCommand ;
    
    private JPanel toolbar;

    private JPopupMenu popupLien, popupEntite, popupAssociation,
            popupSaveGraphic, popupCurseur;

    private JFileChooser chooser;

    private JToggleButton btnLien ,btnCurseur ;

    private EntiteDialog entiteDialog = null ;

    private LienDialog lienDialog;

    private MCDObjet objet;

    private MCDLien lien;
    
    private String typeAction = Constantes.ADD_ENT;

    public MCDPanel(MCDComponent mcdComponent, MPDComponent mpdComponent,
            SQLCommand sqlCommand,  MLDComponent mldComponent, MLDCommand mldCommand )
    {
        super(Constantes.MCD);
        
        this.mcdComponent = mcdComponent;
        this.mpdComponent = mpdComponent;        
        this.mldComponent = mldComponent;
        this.sqlCommand = sqlCommand;
        this.mldCommand = mldCommand;
        this.actionHandler = new ActionHandler();
        
        initAction();
        initToolbar();
        initPopup();
        initDialog();

        chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileHidingEnabled(true);

        AnalyseFilter af;
        FileChooserFilter cf;
        chooser.resetChoosableFileFilters();
        cf = new FileChooserFilter(Constantes.PNG);
        cf.setExtension(Constantes.PNG_MINUSCULE);
        cf.setDescription(Constantes.STR_IMAGE_PNG);
        chooser.addChoosableFileFilter(cf);
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);

        this.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 3));

        this.setLayout(new BorderLayout());
        mcdComponent.setBackground(Constantes.COULEUR_FOND_MCD) ;
        JScrollPane jsp = new JScrollPane(mcdComponent) ;
         
        this.add(BorderLayout.CENTER, jsp );
        this.add(BorderLayout.NORTH, toolbar);

        mcdComponent.addMouseListener(new MouseHandler());
        
		// Utilisation de la touche SUPPR ou BACK_SPACE pour supprimer la sélection
		mcdComponent.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "suppr");
		mcdComponent.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "suppr");
		mcdComponent.getActionMap().put("suppr",  deleteObjectsAction);
    }
    
    private Action deleteObjectsAction = new AbstractAction() {

		private static final long serialVersionUID = 4024872425170460547L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (mcdComponent.sizeSelection() > 0)
				deleteObjects();
		}
	};

    private void initToolbar()
    {
        toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));

        toolbar.add(new JToggleButton(addEntite) {

            {
                setText("");
                addMouseListener(Main.statusbar.getHandler());
            }
        });

        toolbar.add(new JToggleButton(addAssociation) {

            {
                setText("");
                addMouseListener(Main.statusbar.getHandler());
            }
        });

        toolbar.add(btnLien = new JToggleButton(addLien) {

            {
                setText("");                
                addMouseListener(Main.statusbar.getHandler());
            }
        });
        
        toolbar.add(btnCurseur = new JToggleButton(changeCurseur) {

            {
                setText("");
                setSelected(true);
                doClick() ;   // #505822
                addMouseListener(Main.statusbar.getHandler());
            }
        });
        
        toolbar.add(new JToolBar.Separator());

        toolbar.add(new JButton(saveGraphic) {

            {
                addMouseListener(Main.statusbar.getHandler());
            }
        });
    }

    private void initPopup()
    {
        popupLien = new JPopupMenu();

        popupLien.add(new JMenuItem(modLien) {

            {            	
                addMouseListener(Main.statusbar.getHandler());
            }
        });

        popupLien.addSeparator();

        popupLien.add(new JMenuItem(delLien) {

            {
                addMouseListener(Main.statusbar.getHandler());
            }
        });
        
        popupCurseur = new JPopupMenu();

        popupCurseur.add(new JMenuItem(changeCurseur) {

            {
                addMouseListener(Main.statusbar.getHandler());
            }
        });


        popupEntite = new JPopupMenu();

        popupEntite.add(new JMenuItem(modEntite) {

            {
                addMouseListener(Main.statusbar.getHandler());
            }
        });

        popupEntite.addSeparator();

        popupEntite.add(new JMenuItem(delObjet) {

            {
                addMouseListener(Main.statusbar.getHandler());
            }
        });

        popupAssociation = new JPopupMenu();

        popupAssociation.add(new JMenuItem(modAssociation) {

            {
                addMouseListener(Main.statusbar.getHandler());
            }
        });

        popupAssociation.addSeparator();

        popupAssociation.add(new JMenuItem(delObjet) {

            {
                addMouseListener(Main.statusbar.getHandler());
            }
        });

        popupSaveGraphic = new JPopupMenu();
        popupSaveGraphic.add(new JMenuItem(saveGraphic) {
            {
                addMouseListener(Main.statusbar.getHandler());
            }
        });
        
        
    }

    private void initAction()
    {        
        addEntite = new BasicAction(
        		Utilities.getLangueMessage (Constantes.MESSAGE_ENTITE),
        		Utilities.getLangueMessage (Constantes.MESSAGE_AJOUTER_ENTITE),
        		Constantes.ADD_ENT,
                GUIUtilities.getImageIcon(Constantes.FILE_PNG_ENTITE), 0, null);
        addEntite.addActionListener(actionHandler);
        
        changeCurseur = new BasicAction(Constantes.MESSAGE_CURSEUR, Constantes.MESSAGE_CHANGE_CURSEUR, Constantes.CHANGE_CURSEUR,
                GUIUtilities.getImageIcon(Constantes.FILE_PNG_CURSEUR ), 0, null);
        changeCurseur.addActionListener(actionHandler);
        
        addAssociation = new BasicAction(Utilities.getLangueMessage (Constantes.MESSAGE_ASSOCIATION),
        		Utilities.getLangueMessage (Constantes.MESSAGE_AJOUTER_ASSOCIATION),
                Constantes.ADD_ASS, 
                GUIUtilities.getImageIcon(Constantes.FILE_PNG_ASSOCIATION), 0, null) ;
        
        addAssociation.addActionListener(actionHandler);

        addLien = new BasicAction(
        		Utilities.getLangueMessage (Constantes.MESSAGE_LIEN),
        		Utilities.getLangueMessage (Constantes.MESSAGE_AJOUTER_LIEN),
        		Constantes.ADD_LIEN,
                GUIUtilities.getImageIcon(Constantes.FILE_PNG_LIEN), 0, null);
        addLien.addActionListener(actionHandler);

        delLien = new BasicAction("Supprimer", "Supprimer le lien", Constantes.DEL_LIEN,
                GUIUtilities.getImageIcon(Constantes.FILE_PNG_DELETE ), 0, null);
        delLien.addActionListener(actionHandler);

        delObjet = new BasicAction("Supprimer", "Supprimer l'objet",
                Constantes.DEL_OBJET, GUIUtilities.getImageIcon(Constantes.FILE_PNG_DELETE), 0, null);
        delObjet.addActionListener(actionHandler);

        modEntite = new BasicAction("Modifier", "Modifier l'entité",
                Constantes.MOD_OBJET, GUIUtilities.getImageIcon(Constantes.FILE_PNG_EDIT), 0, null);
        modEntite.addActionListener(actionHandler);

        modAssociation = new BasicAction("Modifier", "Modifier l'association",
                Constantes.MOD_OBJET, GUIUtilities.getImageIcon(Constantes.FILE_PNG_EDIT), 0, null);
        modAssociation.addActionListener(actionHandler);

        modLien = new BasicAction("Modifier", "Modifier le lien", Constantes.MOD_LIEN,
                GUIUtilities.getImageIcon(Constantes.FILE_PNG_EDIT), 0, null);
        modLien.addActionListener(actionHandler);

        // test multi-langage
        verif = new BasicAction("", Utilities.getLangueMessage ("verification"),
                Constantes.VERIF_MCD, GUIUtilities.getImageIcon(Constantes.FILE_PNG_OK), 0, null);
        verif.addActionListener(actionHandler);

        buildMPD = new BasicAction("",
        		Utilities.getLangueMessage ("generation_mpd"), Constantes.BUILD_MPD,
                GUIUtilities.getImageIcon(Constantes.FILE_PNG_BUILD_MPD), 0, KeyStroke
                        .getKeyStroke(KeyEvent.VK_F5, 0));
        buildMPD.addActionListener(actionHandler);      
        
        modParametrage = new BasicAction("",
        		Utilities.getLangueMessage ("parametrage"), Constantes.PARAMETRAGE,
                GUIUtilities.getImageIcon(Constantes.FILE_PNG_PARAMETRAGE), 0, KeyStroke
                        .getKeyStroke(KeyEvent.VK_F5, 0));
        modParametrage.addActionListener(actionHandler);

        saveGraphic = new BasicAction(Utilities.getLangueMessage ("sauvegarde_png"),
                Utilities.getLangueMessage("help_sauvegarde_png"), Constantes.SAVE_GRAPH,
                GUIUtilities.getImageIcon(Constantes.FILE_PNG_SAVE), 0, null);
        saveGraphic.addActionListener(actionHandler);
    }

    private String chooseFile()
    {
        if (chooser.showDialog(org.analyse.main.Main.analyseFrame, null) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }

        return null;
    }

    public BasicAction getVerif()
    {
        return verif;
    }

    public BasicAction getBuildLMD()
    {
        return buildMPD;
    }


    public BasicAction getBuildLMLD()
    {
        return buildMLD;
    }

    private void initDialog()
    {
        entiteDialog = new EntiteDialog(mcdComponent.getData());         
        lienDialog = new LienDialog();
    }
    
    //Désélectionne tous les JToggleButton de la toolbar et séléctionne sauf
    //Il y a toujours un bouton pressé (à la manière de radio boutons)
    private void deselectToolbarButton(JToggleButton sauf){
    	for(Component comp: toolbar.getComponents()){
    		if(comp instanceof JToggleButton && comp!=sauf){
    			((JToggleButton) comp).setSelected(false);
    		}
    	}
    	sauf.setSelected(true);
    	
    }
    
	private void deleteObjects() {
		DictionnaireTable data = mcdComponent.getData();
		String mess = Utilities
				.getLangueMessage("supprimer_objet_selection");
		if (mcdComponent.sizeSelection() > 1)
			mess = "Voulez-vous vraiment supprimer les "
					+ mcdComponent.sizeSelection()
					+ " objets sélectionnés ?";
		if (JOptionPane.showConfirmDialog(null, mess,
				Utilities.getLangueMessage("analysesi"),
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
		for (MCDObjet mcdObjet : mcdComponent.removeObjets())
			data.deleteObserver(mcdObjet);
		}
	}

    private class ActionHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String action = e.getActionCommand();
            
            if (action.equals(Constantes.ADD_ENT)) {
            	typeAction = Constantes.ADD_ENT;
            	mcdComponent.setEnabled(false);
            	deselectToolbarButton((JToggleButton)e.getSource());
            } 
            if (action.equals(Constantes.ADD_ASS)) {
            	typeAction = Constantes.ADD_ASS;
            	mcdComponent.setEnabled(false);
            	deselectToolbarButton((JToggleButton)e.getSource());
            } 
            
            if (action.equals(Constantes.ADD_LIEN)) {
            	typeAction = Constantes.ADD_LIEN;
            	mcdComponent.setEnabled(true);
            	deselectToolbarButton((JToggleButton)e.getSource());
            	if (btnLien.getSelectedObjects() != null)
                    mcdComponent.addLien();
            } 
            
            if(action.equals(Constantes.CHANGE_CURSEUR)) {
            	typeAction = Constantes.CHANGE_CURSEUR;
            	mcdComponent.annulerCreerLien();
            	mcdComponent.setEnabled(true);
                deselectToolbarButton((JToggleButton)e.getSource());  	
            } 
            if (action.equals(Constantes.DEL_LIEN)) {
                String mess = Utilities.getLangueMessage ("supprimer_lien_selection") ;
                if (JOptionPane.showConfirmDialog(null, mess, Utilities.getLangueMessage ("analysesi"),
                        JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
                    return;
                mcdComponent.removeLien();
            } 
            
            if (action.equals(Constantes.DEL_OBJET)) {
            	deleteObjects();
            } 
            if (action.equals(Constantes.MOD_OBJET)) {
                entiteDialog.load(objet);
            } 
            if (action.equals(Constantes.MOD_LIEN)) {
                lienDialog.load(lien);
            } 
            if (action.equals(Constantes.VERIF_MCD)) {
                mcdComponent.isCorrect(Constantes.SHOW_ALL);
            } 
            if (action.equals(Constantes.BUILD_MPD)) {
                if ( mcdComponent.buildMPD(mpdComponent,  Constantes.CREATE_MCD) ) {                	
                	mpdComponent.buildSQL(mcdComponent.getData(), sqlCommand);
                	mldComponent.buildMLD(mpdComponent, mldCommand);                                         
                }
            } 
            /*
            if (action.equals(Constantes.BUILD_MLD)) {
                    if ( mcdComponent.buildMPD(mpdComponent,  Constantes.CREATE_MCD) ) {
                        mpdComponent.buildSQL(mcdComponent.getData(), sqlCommand);
                        mldCommand = sqlCommand ; 
                        mldComponent.buildMLD(mldCommand);                    
                    }
            }
              */  
                
            if (action.equals(Constantes.SAVE_GRAPH)) {
                String fileName = chooseFile();
                if (fileName == null)
                    return;

                if (!Utilities.getExtension(fileName).equals(Constantes.PNG_MINUSCULE)
                        && !Utilities.getExtension(fileName).equals(Constantes.PNG))
                    fileName = fileName + "." + Constantes.PNG_MINUSCULE ;

                mcdComponent.enleverFocus();

                try {
                    File imageFile;
                    FileImageOutputStream outputStream;
                    BufferedImage img;
                    Graphics g;
                    Graphics2D g2d;
                    Rectangle2D r;

                    imageFile = new File(fileName);
                    img = new BufferedImage(
                            (int) (mcdComponent.getPreferredSize().getWidth()),
                            (int) (mcdComponent.getPreferredSize().getHeight()),
                            BufferedImage.TYPE_INT_RGB);

                    g = img.getGraphics();
                    g2d = (Graphics2D) g;

                    g2d.setColor(new Color(255, 255, 255));
                    r = new Rectangle2D.Double(0, 0, img.getWidth(), img
                            .getHeight());
                    g2d.fill(r);

                    mcdComponent.paintComponent(g2d);

                    PNGImageWriter writer = new PNGImageWriter(null);

                    writer.setOutput(outputStream = new FileImageOutputStream(
                            imageFile));
                    writer.write(img);

                    outputStream.close();

                    writer.dispose();

                } catch (IOException err) {
                    GUIUtilities.error("Impossible de sauvegarder le fichier " + fileName);
                }
            }
        }
    }

    private class MouseHandler extends MouseAdapter
    {
    	public void mousePressed(MouseEvent me){

    		if (me.getButton() == MouseEvent.BUTTON1) {		
	            if (typeAction.equals(Constantes.ADD_ENT)) {
	                DictionnaireTable data = mcdComponent.getData();          
	                data.addObserver(mcdComponent.addEntite(me.getX(), me.getY()));
	                
	            } else if (typeAction.equals(Constantes.ADD_ASS)) {
	                DictionnaireTable data = mcdComponent.getData();
	                data.addObserver(mcdComponent.addAssociation(me.getX(), me.getY()));
	            } 
    		}
    		else{
    			/* on repasse en mode curseur*/
            	typeAction = Constantes.CHANGE_CURSEUR;
                deselectToolbarButton(btnCurseur);
    		}
    	}
    	
        public void mouseReleased(MouseEvent e)
        {
            objet = null;
            lien = null;
            //if (e.isPopupTrigger()) Ne marche pas avec le JDK d'IBM
            if (e.getButton() == MouseEvent.BUTTON3) {
                Object o = mcdComponent.getObjectFromLocation(e.getX(), e
                        .getY());
                if (o instanceof MCDLien) {
                    popupLien.show(e.getComponent(), e.getX(), e.getY());
                    lien = (MCDLien) o;
                } else if (o instanceof MCDAssociation) {
                    popupAssociation.show(e.getComponent(), e.getX(), e.getY());
                    objet = (MCDObjet) o;
                } else if (o instanceof MCDEntite) {
                    popupEntite.show(e.getComponent(), e.getX(), e.getY());
                    objet = (MCDObjet) o;
                } else {
                    popupSaveGraphic.show(e.getComponent(), e.getX(), e.getY());
                }

            }
            else if (e.getButton() == MouseEvent.BUTTON1 && typeAction.equals(Constantes.ADD_LIEN)) {
            	mcdComponent.addLien();
            }
        }

        public void mouseClicked(MouseEvent e)
        {
            if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 
            		&& typeAction.equals(Constantes.CHANGE_CURSEUR)) {
                Object o = mcdComponent.getObjectFromLocation(e.getX(), e
                        .getY());
                if (o instanceof MCDLien) {
                    lienDialog.load((MCDLien) o);
                } else if (o instanceof MCDAssociation) {
                	entiteDialog = new EntiteDialog(mcdComponent.getData());
                    entiteDialog.load((MCDAssociation) o);
                } else if (o instanceof MCDEntite) {
                	entiteDialog = new EntiteDialog(mcdComponent.getData());
                    entiteDialog.load((MCDObjet) o);
                }
            }
        }
    }
}
