/*
 * File        : AboutWindow2.java
 * Date      : 1 avr. 2004
 * Author   : loyl
 * 
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details ( see the LICENSE file ).
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.analyse.core.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.analyse.core.gui.action.BasicAction;
import org.analyse.core.util.Constantes;
import org.analyse.core.util.GUIUtilities;
import org.analyse.core.util.MyPanelFactory;
import org.analyse.core.util.Utilities;
import org.analyse.main.Main;

/**
 * @author loyl
 */
public class AboutWindow extends JDialog implements ActionListener
{
    private BasicAction close;
    
    public AboutWindow(JFrame parent)
    {
        super(parent, Utilities.getRelease());

        initAction();
        
        JPanel p = new JPanel(new BorderLayout());

        JLabel img = new JLabel(GUIUtilities.getImageIcon(Constantes.FILE_PNG_LOGO ));
        img.setBorder(BorderFactory.createEtchedBorder());

        JPanel pImg = new JPanel(new BorderLayout());
        pImg.add(img, BorderLayout.CENTER);
        pImg.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JTabbedPane pane = new JTabbedPane();
        pane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        pane.add(Utilities.getLangueMessage(Constantes.MESSAGE_APROPOS) , pImg);
        pane.add(Utilities.getLangueMessage(Constantes.MESSAGE_AUTEUR), buildAuthorPane());
        //pane.add(Utilities.getLangueMessage(Constantes.MESSAGE_QUOI_DE_NEUF), buildWhatsnewPane());
        pane.add(Utilities.getLangueMessage(Constantes.MESSAGE_LICENCE), buildLicensePane());

        p.add(BorderLayout.CENTER, pane);
        p.add(BorderLayout.SOUTH, MyPanelFactory.createBottomWhitePanel(new JButton(close)));

        this.getContentPane().add(p);
        this.setSize(new Dimension(540, 550));
        this.setResizable(false);
        GUIUtilities.centerComponent(this);
    }

    private JPanel buildWhatsnewPane()
    {
        JPanel panel = new JPanel(new BorderLayout());
        
        try {

            JEditorPane editor = new JEditorPane( Main.class.getResource("help/whatsnew.html") );
            editor.setEditable(false);
            panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            panel.add(new JScrollPane(editor), BorderLayout.CENTER);
        } catch (IOException e) {
        }

        return panel;
    }

    private JPanel buildAuthorPane()
    {
        JPanel panel = new JPanel(new BorderLayout());
        
        try {

            JEditorPane editor = new JEditorPane( Main.class.getResource("help/author.html") );
            editor.setEditable(false);
            panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            panel.add(new JScrollPane(editor), BorderLayout.CENTER);
        } catch (IOException e) {
        }

        return panel;
    }

    private JPanel buildLicensePane()
    {
        JPanel panel = new JPanel(new BorderLayout());
        URL url = Main.class.getResource("help/GPL.html");
        try {
            JEditorPane editor = new JEditorPane(url);
            editor.setEditable(false);

            panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            panel.add(new JScrollPane(editor), BorderLayout.CENTER);
        } catch (IOException e) {
        }

        return panel;
    }
    
    private void initAction() {
        close = new BasicAction(
        			Utilities.getLangueMessage(Constantes.MESSAGE_FERMER), 
        			Utilities.getLangueMessage(Constantes.MESSAGE_FERMER_CETTE_FENETRE), 
		        	Constantes.CLOSE, null,
		            0, null);
        close.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(Constantes.CLOSE)) {
            this.setVisible(false);
        }
    }
}