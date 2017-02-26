/*
 * 06/11/2003 - 14:55:13
 *
 * EntiteDialog.java -
 * Copyright (C) 2003 Dreux Loic
 * dreuxl@free.fr
 *
 * Modifications :
 * Mehdi CHAABANI - 04/02/2017
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

package org.analyse.merise.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.analyse.core.gui.action.BasicAction;
import org.analyse.core.util.Constantes;
import org.analyse.core.util.GUIUtilities;
import org.analyse.core.util.MyPanelFactory;
import org.analyse.core.util.Utilities;
import org.analyse.main.Main;
import org.analyse.merise.gui.list.MeriseListModel;
import org.analyse.merise.gui.table.DictionnaireTable;
import org.analyse.merise.mcd.composant.MCDAssociation;
import org.analyse.merise.mcd.composant.MCDEntite;
import org.analyse.merise.mcd.composant.MCDObjet;

/**
 * Ce <code>JDialog</code> permet d'ajouter ou de supprimer les attributs
 * d'une entité.
 */
public class EntiteDialog extends JDialog
{
    private BasicAction ok;

    private BasicAction cancel;

    private BasicAction ajout;

    private BasicAction right, left;

    private BasicAction monter, descendre;

    private BasicAction validerNom;

    private JPanel panel;

    private JLabel title;

    private JTextField nomInfo, tailleInfo;

    private JComboBox typesInfo;

    private JTextField nom;

    private JComboBox identifiant;

    private MCDObjet mcdobject;

    private DictionnaireTable data;

    private JList droite, gauche;

    private ActionHandler handler;
    
    private MouseHandler mouseHandler;

    protected MeriseListModel listGauche, listDroite;

    private String oldName;

    public EntiteDialog(DictionnaireTable dictionnaireTable)
    {
        super(Main.analyseFrame, Utilities.getLangueMessage (Constantes.MESSAGE_ENTITE), true);
        this.data = dictionnaireTable;

        initAction();

        listGauche = new MeriseListModel();
        listDroite = new MeriseListModel();

        //Bug #347422
        listGauche.clear() ;
        listDroite.clear() ;
       
        Container c = this.getContentPane();
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());

        p.add(BorderLayout.NORTH, title = MyPanelFactory.createAntialiasingTitle( Utilities.getLangueMessage (Constantes.MESSAGE_ENTITE) ));
        p.add(BorderLayout.CENTER, buildPanel());
        p.add(BorderLayout.SOUTH, MyPanelFactory.createBottomWhitePanel(new JButton(ok), new JButton(cancel)));

        gauche.setModel(listGauche);
        droite.setModel(listDroite);

        gauche.addMouseListener(mouseHandler);
        droite.addMouseListener(mouseHandler);
        
        c.add(p);

        this.pack();
        this.setResizable(false);
        GUIUtilities.centerComponent(this);
    }

    /**
     * Constuit le Panel.
     */
    private JPanel buildPanel()
    {
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        panel.add(BorderLayout.NORTH, new JPanel() {
            {
                setLayout(new BorderLayout());
                add(new JLabel(Utilities.getLangueMessage (Constantes.MESSAGE_NOM) + " : " , SwingConstants.LEFT),
                        BorderLayout.WEST);
                add(nom = new JTextField(), BorderLayout.CENTER);
                nom.setAction(validerNom);
                nom.addKeyListener(new KeyHandler());
            }
        });
        panel.add(BorderLayout.CENTER, new JPanel() {
            {
                this.setLayout(new BorderLayout());
                this.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
                this.add(BorderLayout.WEST, new JScrollPane(
                        gauche = new JList()) {
                    public Dimension getMinimumSize()
                    {
                        return new Dimension(200, 200);
                    }

                    public Dimension getPreferredSize()
                    {
                        return getMinimumSize();
                    }
                });
                this.add(BorderLayout.EAST, new JPanel() {
                    {
                        this.setLayout(new BorderLayout());
                        this.add(BorderLayout.CENTER, new JScrollPane(
                                droite = new JList()));
                        JPanel toolbar = new JPanel();
                        toolbar.setLayout(new FlowLayout(FlowLayout.CENTER));
                        toolbar.add(new JButton(monter));
                        toolbar.add(new JButton(descendre));
                        this.add(BorderLayout.SOUTH, toolbar);
                    }

                    public Dimension getMinimumSize()
                    {
                        return new Dimension(200, 200);
                    }

                    public Dimension getPreferredSize()
                    {
                        return getMinimumSize();
                    }

                });
                this.add(BorderLayout.CENTER, new JPanel() {
                    {
                        this.setLayout(new FlowLayout(FlowLayout.CENTER));
                        this.add(BorderLayout.CENTER, new JPanel() {
                            {
                                this.setLayout(new BorderLayout());
                                this
                                        .add(BorderLayout.NORTH, new JButton(
                                                right));
                                this.add(BorderLayout.CENTER,
                                        new JToolBar.Separator());
                                this.add(BorderLayout.SOUTH, new JButton(left));
                            }
                        });
                    }
                });
            }
        });

        nomInfo = new JTextField();
        typesInfo = new JComboBox(data.getTypes().toArray());
        tailleInfo = new JTextField();

        return new JPanel(new BorderLayout()) {
            {
                this.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
                this.add(BorderLayout.CENTER, new JPanel(new BorderLayout()) {
                    {
                        this.setBorder(BorderFactory.createEtchedBorder());
                        this.add(BorderLayout.CENTER, panel);
                    }
                });
                this.add(BorderLayout.SOUTH, new JPanel() {
                    {
                        this.setBorder(BorderFactory.createEmptyBorder(4, 0, 0,
                                0));
                        this.setLayout(new BorderLayout());

                        this.add(BorderLayout.CENTER, new JPanel() {
                            {
                                this.setBorder(BorderFactory
                                        .createEtchedBorder());
                                this.setLayout(new BorderLayout());
                                this.add(BorderLayout.CENTER, new JPanel() {
                                    {
                                        this.setLayout(new FlowLayout());
                                        add(new JLabel(Utilities.getLangueMessage (Constantes.MESSAGE_NOM) + " : "));
                                        nomInfo.setPreferredSize(new Dimension(
                                                100, 20));
                                        add(nomInfo);
                                        add(new JLabel(Utilities.getLangueMessage (Constantes.MESSAGE_TYPE) + " : "));
                                        typesInfo
                                                .setPreferredSize(new Dimension(
                                                        200, 20));  // B
                                        add(typesInfo);
                                        add(new JLabel(Utilities.getLangueMessage (Constantes.MESSAGE_TAILLE) + " : "));
                                        tailleInfo
                                                .setPreferredSize(new Dimension(
                                                        50, 20));
                                        add(tailleInfo);
                                        add(new JButton(ajout));
                                    }
                                });
                            }
                        });
                    }
                });

            }
        };
        
    
    }

    /**
     * Initialise les actions.
     */
    private void initAction()
    {
        handler = new ActionHandler();
        mouseHandler = new MouseHandler();

        ok = new BasicAction( Utilities.getLangueMessage ("ok"),
        		Utilities.getLangueMessage (Constantes.MESSAGE_ENREGISTRER_CHANGEMENT), Constantes.OK, null,
                0, null);
        ok.addActionListener(handler);
        cancel = new BasicAction(Utilities.getLangueMessage (Constantes.MESSAGE_ANNULER),
        		Utilities.getLangueMessage (Constantes.MESSAGE_ANNULER_CHANGEMENT),
                Constantes.CANCEL, null, 0, null);
        cancel.addActionListener(handler);

        right = new BasicAction("", Utilities.getLangueMessage (Constantes.MESSAGE_AJOUTER_INFORMATION),
                Constantes.RIGHT, GUIUtilities.getImageIcon(Constantes.FILE_PNG_RIGHT), 0, null);
        right.addActionListener(handler);

        left = new BasicAction("",
        		Utilities.getLangueMessage (Constantes.MESSAGE_RETIRER_INFORMATION),
        		Constantes.LEFT,
                GUIUtilities.getImageIcon(Constantes.FILE_PNG_LEFT), 0, null);
        left.addActionListener(handler);

        monter = new BasicAction(
                "",
                Utilities.getLangueMessage (Constantes.MESSAGE_INVERSER_INFORMATION_AVEC_PRECEDENT),
                Constantes.UP ,
                GUIUtilities.getImageIcon(Constantes.FILE_PNG_UP), 0, null);
        monter.addActionListener(handler);

        descendre = new BasicAction("",
                Utilities.getLangueMessage (Constantes.MESSAGE_INVERSER_INFORMATION_AVEC_SUIVANT),
                Constantes.DOWN ,
                GUIUtilities.getImageIcon(Constantes.FILE_PNG_DOWN), 0, null);
        descendre.addActionListener(handler);

        validerNom = new BasicAction("",
        		Utilities.getLangueMessage (Constantes.MESSAGE_MODIFIER_NOM_ENTITE), Constantes.STR_NAME,
                null, 0, null);
        validerNom.addActionListener(handler);

        ajout = new BasicAction(Utilities.getLangueMessage (Constantes.MESSAGE_AJOUT) ,
        		Utilities.getLangueMessage (Constantes.MESSAGE_AJOUT_RAPIDE_INFORMATION) ,
                Constantes.AJOUT, null, 0, null);
        ajout.addActionListener(handler);
    }

    /**
     * Chargement d'un MCDObjet. <br>
     * Initialise le titre et affiche la fenetre.
     */
    public void load(MCDObjet mcdobject)
    {
        this.mcdobject = mcdobject;

        if (mcdobject instanceof MCDEntite)
            setTitle( Utilities.getLangueMessage (Constantes.MESSAGE_ENTITE) + "...");
        else if (mcdobject instanceof MCDAssociation)
            setTitle( Utilities.getLangueMessage (Constantes.MESSAGE_ASSOCIATION) + "...");

        title.setText("<html><font size=+1><i>" + mcdobject.getName()
                + "</i></font></html>");
        nom.setText(mcdobject.getName());

        oldName = mcdobject.getName();
        listDroite.clear() ; // bug : 347422

        update();
        
        this.setVisible(true);
    }

    /**
     * Met à jour les listes.
     */
    private void update()
    {

       /* bug : 347422
       listGauche.clear();
       listDroite.clear();
    	*/

        String code = null ;

        for (int i = 0; i < data.getRowCount() - 1; i++) {
            code = data.getID(i);

            if (!data.getUse(i)) {
            	// bug : 347422
            	if ( ! listDroite.getListLabel().containsKey( code ) )   {
            		listGauche.addElement(code, (String) data.getValue(code,
                		DictionnaireTable.NAME));
            	}
            }
        }
        
        for (Iterator<String> e = mcdobject.elementsInformations(); e
                .hasNext();) {
            code = e.next();
            listDroite.addElement(code, (String) data.getValue(code,
                    DictionnaireTable.NAME));
        }
    }

    /**
     * Ajoute une donnée dans l'entité.
     */
    private void add()
    {
        String key;
        List<String> keys = new ArrayList<String>();
        for (int i = 0; i < listGauche.getSize(); i++) {
            if (gauche.isSelectedIndex(i)) {
                keys.add(listGauche.getKey(i));
                data.setUse(data.getID(i), true ) ;  // bug : 347422
            }
            
        }
        for (Iterator<String> e = keys.iterator(); e.hasNext();) {
            key = e.next();
            listDroite.addElement(key, (String) data.getValue(key,
                    DictionnaireTable.NAME));
            listGauche.removeElement(key);
        }
    }

    /**
     * Supprime une donnée de l'entité.
     */
    private void remove()
    {
        String key;
        List<String> keys = new ArrayList<String>();
        for (int i = 0; i < listDroite.getSize(); i++) {
            if (droite.isSelectedIndex(i))
                keys.add(listDroite.getKey(i));
        }
        for (Iterator<String> e = keys.iterator(); e.hasNext();) {
            key = e.next();
            listGauche.addElement(key, (String) data.getValue(key,
                    DictionnaireTable.NAME));
            listDroite.removeElement(key);
        }
    }

    /**
     * Déplace une donnée vers le haut de la liste.
     */
    private void up()
    {
        boolean sel = false;
        int debut = 0, fin = 0;

        for (int i = 0; i < listDroite.getSize() + 1; i++) {
            if (droite.isSelectedIndex(i) && !sel) {
                debut = i;
                sel = true;
            } else if (!droite.isSelectedIndex(i) && sel) {
                fin = i - 1;
                break;
            }
        }
        if (listDroite.moveLines(debut, fin, MeriseListModel.UP)) {
            int[] indices = new int[fin - debut + 1];
            int j = 0;
            for (int i = debut - 1; i < fin; i++) {
                indices[j] = i;
                j++;
            }

            droite.setSelectedIndices(indices);
        }
    }

    /**
     * Déplace une donnée vers le bas de la liste.
     */
    private void down()
    {
        boolean sel = false;
        int debut = 0, fin = 0;

        for (int i = 0; i < listDroite.getSize() + 1; i++) {
            if (droite.isSelectedIndex(i) && !sel) {
                debut = i;
                sel = true;
            } else if (!droite.isSelectedIndex(i) && sel) {
                fin = i - 1;
                break;
            }
        }
        if (listDroite.moveLines(debut, fin, MeriseListModel.DOWN)) {
            int[] indices = new int[fin - debut + 1];
            int j = 0;
            for (int i = debut + 1; i < fin + 2; i++) {
                indices[j] = i;
                j++;
            }

            droite.setSelectedIndices(indices);

        }
    }

    /**
     * Valide les modifications.
     */
    private void ok()
    {
        mcdobject.clearInformations();
        for (int i = 0; i < listDroite.getSize(); i++) {
            data.setUse(listDroite.getKey(i), true);
            mcdobject.addInformation(listDroite.getKey(i));
        }

        // Vérifie que le nom n'est pas utilisé par quelqu'un d'autre
        if (mcdobject.getMCD().getElement(nom.getText()) == null
                || mcdobject.getMCD().getElement(nom.getText()) == mcdobject) {
            mcdobject.setName(nom.getText());
            close();
        } else {
        	Object[] messageArguments = { nom.getText() } ;
        	String messEntite = Utilities.getLangueMessageFormatter (Constantes.MESSAGE_ENTITE_EXISTE_CHANGER_NOM, messageArguments ) ; 
        	String messAssociation = Utilities.getLangueMessageFormatter (Constantes.MESSAGE_ASSOCIATION_EXISTE_CHANGER_NOM, messageArguments ) ;
        	
            GUIUtilities.error((mcdobject.getMCD().getElement(nom.getText())) instanceof MCDEntite ? messEntite : messAssociation );
        }
    }

    /**
     * Ferme la fenêtre.
     */
    private void close()
    {
        this.setVisible( false );
    }

    private class ActionHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String action = e.getActionCommand();

            if (action.equals( Constantes.OK )) {
                ok();
            } else if (action.equals(Constantes.CANCEL)) {
                close();
            } else if (action.equals(Constantes.RIGHT)) {
                add();
            } else if (action.equals(Constantes.LEFT)) {
                remove();
            } else if (action.equals(Constantes.UP)) {
                up();
            } else if (action.equals(Constantes.DOWN)) {
                down();
            } else if (action.equals(Constantes.STR_NAME)) {
                oldName = nom.getText();
                title.setText("<html><font size=+1><b>" + nom.getText()
                        + "</b></font></html>");
            } else if (action.equals("AJOUT")) {
            	
            	/* cette règle est remise en cause - voir bug #689257
            	* pour l'instant on laisse tel quel
            	*/
            	 if ( ! nomInfo.getText().isEmpty() )
                 	/*  Pouvoir utilisé le même nom d'attribut => entorse à Merise mais beaucoup utilisé dans la pratique

                 	if ( data.contains ( nomInfo.getText() ) ) {
                    	Object[] messageArguments = { nomInfo.getText() } ;
                 		GUIUtilities.error (Utilities.getLangueMessageFormatter (Constantes.MESSAGE_PROPRIETE_EXISTE, messageArguments ) );
         	    		return ;
                 	}
                 	*/

                data.addData(nomInfo.getText(), (String) typesInfo
                        .getSelectedItem(), tailleInfo.getText(), nom.getText());
                update();
                nomInfo.setText("");
                tailleInfo.setText("");
                typesInfo.transferFocusBackward();
            }
        }
    }

    private class MouseHandler extends MouseAdapter
    {
        public void mouseClicked(MouseEvent e)
        {
            if(e.getClickCount() == 2)
            {
                if(e.getSource().equals(gauche))
                {
                    add();
                } else {
                    remove();
                }
            }
        }
    }
    
    private class KeyHandler implements KeyListener
    {
        public void keyPressed(KeyEvent evt)
        {
        }

        public void keyReleased(KeyEvent evt)
        {
        }

        public void keyTyped(KeyEvent evt)
        {
            // Récupère l'ancien nom dans le cas où l'utilisateur appuie sur échape
            if ((int) (evt.getKeyChar()) == 27) {
                nom.setText(oldName);
                title.setText("<html><font size=+1><b>" + nom.getText()
                        + "</b></font></html>");
            }
        }
    }
}
