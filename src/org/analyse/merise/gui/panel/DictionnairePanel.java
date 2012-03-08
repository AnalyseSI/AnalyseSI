/*
 * 05/23/2003 - 16:36:28
 * 
 * DictionnairePanel.java - Copyright (C) 2003 Dreux Loic dreuxl@free.fr
 * 
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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.analyse.core.gui.action.BasicAction;
import org.analyse.core.modules.AnalysePanel;
import org.analyse.core.util.Constantes;
import org.analyse.core.util.GUIUtilities;
import org.analyse.core.util.Utilities;
import org.analyse.main.Main;
import org.analyse.merise.gui.table.DictionnaireTable;

public class DictionnairePanel extends AnalysePanel
{
    private JPanel toolbar;

    private JComboBox comboBox;

    private JPopupMenu popupTable;

    private BasicAction supprimer;

    private BasicAction monter, descendre;

    private ActionHandler actionHandler;

    private JTable table;

    private DictionnaireTable dictionnaireTable;

    public DictionnairePanel(DictionnaireTable dictionnaireTable)
    {
        super(Constantes.DICO);

        this.dictionnaireTable = dictionnaireTable;

        this.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 3));

        comboBox = new JComboBox(dictionnaireTable.getTypes().toArray());

        table = new JTable(dictionnaireTable);
        table.getColumn(Utilities.getLangueMessage (Constantes.MESSAGE_TYPE)).setCellEditor(new DefaultCellEditor(comboBox));

        table.getColumnModel().getColumn(3).setMaxWidth(100);
        table.getColumnModel().getColumn(4).setMaxWidth(50);
        table.setRowHeight(4, 1);
        table.setAutoCreateRowSorter(true)  ;   // Bug #456345

        actionHandler = new ActionHandler();

        initAction();
        initPopup();
        initToolbar();

        table.addMouseListener(new MouseHandler());

        this.setLayout(new BorderLayout());
        this.add(BorderLayout.CENTER, new JScrollPane(table));
        this.add(BorderLayout.SOUTH, toolbar);
    }

    private void initToolbar()
    {
        toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout(FlowLayout.CENTER));

        toolbar.add(new JButton(monter) {
            {
                setText("");
                addMouseListener(Main.statusbar.getHandler());
            }
        });
        toolbar.add(new JButton(descendre) {
            {
                setText("");
                addMouseListener(Main.statusbar.getHandler());
            }
        });
    }

    private void initAction()
    {
        supprimer = new BasicAction(Utilities.getLangueMessage (Constantes.MESSAGE_SUPPRIMER),
        		Utilities.getLangueMessage (Constantes.MESSAGE_SUPPRIMER_LIGNE), 
        		Constantes.DELETE, GUIUtilities.getImageIcon(Constantes.FILE_PNG_DELETE), 0, null);
        supprimer.addActionListener(actionHandler);

        monter = new BasicAction(Utilities.getLangueMessage (Constantes.MESSAGE_MONTER),
        		Utilities.getLangueMessage (Constantes.MESSAGE_INVERSER_INFORMATION_AVEC_PRECEDENT ),
                Constantes.UP, GUIUtilities.getImageIcon(Constantes.FILE_PNG_UP), 0, null);
        monter.addActionListener(actionHandler);

        descendre = new BasicAction(Utilities.getLangueMessage (Constantes.MESSAGE_DESCENDRE),
        		Utilities.getLangueMessage (Constantes.MESSAGE_INVERSER_INFORMATION_AVEC_SUIVANT ),
                Constantes.DOWN, GUIUtilities.getImageIcon(Constantes.FILE_PNG_DOWN), 0, null);
        descendre.addActionListener(actionHandler);
    }

    private void initPopup()
    {
        popupTable = new JPopupMenu();

        popupTable.add(new JMenuItem(monter) {
            {
                addMouseListener(Main.statusbar.getHandler());
            }
        });
        popupTable.add(new JMenuItem(descendre) {
            {
                addMouseListener(Main.statusbar.getHandler());
            }
        });
        popupTable.addSeparator();
        popupTable.add(new JMenuItem(supprimer) {
            {
                addMouseListener(Main.statusbar.getHandler());
            }
        });
    }

    private class MouseHandler extends MouseAdapter
    {
        public void mousePressed(MouseEvent e)
        {
            java.awt.Point point = e.getPoint();

            if (table.getSelectedRowCount() == 0
                    || !table.isRowSelected(table.rowAtPoint(point)))
                table.changeSelection(table.rowAtPoint(point), 0, false, false);
        }

        public void mouseReleased(MouseEvent e)
        {
            //if (e.isPopupTrigger()) Ne marche pas avec le JDK d'IBM
            if (e.getButton() == MouseEvent.BUTTON3)
                popupTable.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    private class ActionHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String action = e.getActionCommand();
            boolean test;

            if (action.equals(Constantes.DELETE)) {
                String mess;
                int[] indexSelected = table.getSelectedRows();
                
              //modif bellier.l  -   merci pour le code 

                for (int i = 0; i < indexSelected.length; i++) {
                	indexSelected[i] =
table.convertRowIndexToModel(indexSelected[i]);
                }
                //==> ca c'est nouveau
                
                Arrays.sort(indexSelected); //Tri croissant des index pour suppression
                
                int nbIndex = indexSelected.length;
                
                if (nbIndex == 1)                	
                    mess = Utilities.getLangueMessage (Constantes.MESSAGE_QUESTION_SUPPRIMER_ELEMENT) ;
                else {
                	
                	Object[] messageArguments = {nbIndex} ; 
                	mess = Utilities.getLangueMessageFormatter (Constantes.MESSAGE_QUESTION_SUPPRIMER_ELEMENTS_SELECTIONNES, 
                			messageArguments ) ; 
                    
                }
                              
                test = false;
                for (int i = 0; i < nbIndex; i++) {
                    if (dictionnaireTable.getUse(indexSelected[i]))
                        test = true;
                }

                if (test && nbIndex == 1)
                    mess += Utilities.getLangueMessage (Constantes.MESSAGE_INFO_DANS_MCD) ;                    	
                else if (test)
                    mess += Utilities.getLangueMessage (Constantes.MESSAGE_CERTAINES_INFOS_DANS_MCD) ;
                
                if (JOptionPane.showConfirmDialog(null, mess, Utilities.getLangueMessage (Constantes.MESSAGE_ANALYSESI),
                        JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
                    return;
                
                //suppression des lignes
                dictionnaireTable.delLines(indexSelected);
                
            } else if (action.equals(Constantes.UP)) {
            	dictionnaireTable.moveLines(table.getSelectedRows(), DictionnaireTable.UP );

            } else if (action.equals(Constantes.DOWN)) {
            	dictionnaireTable.moveLines(table.getSelectedRows(), DictionnaireTable.DOWN );

            }
        }
    }
}
