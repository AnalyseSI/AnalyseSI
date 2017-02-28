/*
 * 05/16/2003 - 09:36:15
 *
 * HtmlWindow.java -
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
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.JWindow;

import org.analyse.core.gui.action.BasicAction;
import org.analyse.core.util.Constantes;
import org.analyse.core.util.GUIUtilities;
import org.analyse.core.util.MyPanelFactory;
import org.analyse.core.util.Utilities;
import org.analyse.main.Main;

/**
 * Cette Fenetre sert à afficher un document Html, elle peut servir pour
 * afficher de l'aide par exemple.
 */
public class HtmlWindow extends JWindow implements ActionListener
{
    private JEditorPane editor;
    private BasicAction close;
    
    public HtmlWindow(String text, int width,
            int height, boolean visible, boolean closeAfter2sec)
    {
    	super(Main.analyseFrame);
    	
        editor = new JEditorPane("text/html", text) {
            public void paintComponent(Graphics g)
            {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                super.paintComponent(g2d);
            }
        };
        editor.setEditable(false);
        editor.setBackground(Constantes.COULEUR_FOND_POPUP) ;
        initAction();
        
        Container c = this.getContentPane();
        c.setLayout(new BorderLayout());
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.setBackground(Constantes.COULEUR_FOND_POPUP) ;
        p.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        p.add(BorderLayout.CENTER, new JPanel(new BorderLayout()) {
            {
                this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
                this.add(BorderLayout.CENTER, new JScrollPane(editor) {
                    {
                        JViewport vp = this.getViewport();
                        vp.add(editor);
                    }
                });
            }
        });
        p.add(BorderLayout.SOUTH, MyPanelFactory.createBottomWhitePanel(new JButton(close)));
        c.add(p);

        //this.setTitle(title);
        this.setSize(width, height);
        
        if(closeAfter2sec)
        	this.setLocation(Main.analyseFrame.getX() + Main.analyseFrame.getWidth() - width, Main.analyseFrame.getY() + Main.analyseFrame.getHeight() - height);
        else
            GUIUtilities.centerComponent(this);
        
        this.setVisible(visible);
        
        if(closeAfter2sec)
            closeAfter2sec();
    }

    private void initAction() {
        close = new BasicAction(
        		Utilities.getLangueMessage(Constantes.MESSAGE_FERMER), 
        		Utilities.getLangueMessage(Constantes.MESSAGE_FERMER_CETTE_FENETRE),
        		Constantes.CLOSE, null,
                0, null);
        
        close.addActionListener(this);
    }
    
    public void actionPerformed(ActionEvent e)
    {
        if(e.getActionCommand().equals(Constantes.CLOSE))
        {
            this.setVisible(false);
        }
    }
    
    /**
     * Cette méthode ferme automatiquement le popup au bout
     * de 3 secondes
     */
    public void closeAfter2sec()
    {
        Thread t = new Thread(new Runnable() {
            
                public void run() {
                    try {
                        Thread.sleep(3000);
                        
                        setVisible(false);
                    } catch(InterruptedException e) {
                        setVisible(false);
                    }
                }
        });
        t.start();
    }
}