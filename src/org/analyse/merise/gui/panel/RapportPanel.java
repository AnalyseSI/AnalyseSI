/*
 * 9 févr. 2005 - 11:15:31
 * 
 * RapportPanel.java Copyright (C) 2004 Dreux Loic dreuxl@free.fr
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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.analyse.core.gui.action.BasicAction;
import org.analyse.core.modules.AnalysePanel;
import org.analyse.core.util.Constantes;
import org.analyse.core.util.GUIUtilities;
import org.analyse.core.util.save.AnalyseFilter;
import org.analyse.core.util.save.FileChooserFilter;
import org.analyse.main.Main;
import org.analyse.merise.rapport.MeriseRapport;


public class RapportPanel extends AnalysePanel
{
    private JPanel toolbar;

    private BasicAction generer;

    private ActionHandler actionHandler;

    private MeriseRapport meriseRapport;

    private JFileChooser chooser;
    
    private JEditorPane editor;

    public RapportPanel(MeriseRapport meriseRapport)
    {
        super("RapportPanel");

        this.meriseRapport = meriseRapport;

        initAction();
        initToolbar();

        chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileHidingEnabled(true);

        AnalyseFilter af;
        FileChooserFilter cf;
        chooser.resetChoosableFileFilters();
        cf = new FileChooserFilter("HTML");
        cf.setExtension("sql");
        cf.setDescription("Fichier script SQL");
        chooser.addChoosableFileFilter(cf);
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        
        editor = new JEditorPane() {
            public void paintComponent(Graphics g)
            {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                super.paintComponent(g2d);
            }
        };
        editor.setEditable(false);

        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 3));
        
        this.add(BorderLayout.CENTER, new JScrollPane(editor));
        this.add(BorderLayout.NORTH, toolbar);
    }

    private void initToolbar()
    {
        toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        toolbar.add(new JButton(generer) {
            {
                addMouseListener(Main.statusbar.getHandler());
            }
        });
    }

    private void initAction()
    {
        actionHandler = new ActionHandler();

        generer = new BasicAction("Générer", "Générer le rapport", "GEN",
                GUIUtilities.getImageIcon(Constantes.FILE_PNG_RAPPORT), 0, null);
        generer.addActionListener(actionHandler);
    }

    private class ActionHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String command = e.getActionCommand();

            if (command.equals("GEN"))
            {
                try {
                    editor.setPage(meriseRapport.createRapport());
                } catch(IOException exc) {
                    
                }
            }
        }
    }
}
