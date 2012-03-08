/*
 * 7 févr. 2005 - 11:19:31
 * 
 * Navigator.java Copyright (C) 2004 Dreux Loic dreuxl@free.fr
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
package org.analyse.core.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.analyse.core.gui.AnalyseFrame;
import org.analyse.core.gui.action.BasicAction;
import org.analyse.core.util.Constantes;
import org.analyse.core.util.Utilities;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.lite.panel.SimpleInternalFrame;

public class Navigator extends JPanel
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5561301850688044080L;
	private SimpleInternalFrame iFrame;
    private Map<String, JPanel> hashtable;
    private AnalyseFrame analyseFrame;
    private JPanel centre;
    private FormLayout layout;
    private PanelBuilder builder;
    private CellConstraints cc;
    private int inc;
    
    public Navigator(AnalyseFrame analyseFrame)
    {
        /* Construction du Navigator */
        super(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(4,4,0,0));
        this.analyseFrame = analyseFrame;
        
        /* Initialise la hashtable contenant les Panels */
        hashtable = new HashMap<String, JPanel>();
        
        /* Construction de la Frame Interne*/
        iFrame = new SimpleInternalFrame(Utilities.getLangueMessage(Constantes.MESSAGE_NAVIGATEUR));
        layout = new FormLayout(
				"5px, 182px, 5px",
				"2mm, p, 2mm, p, 2mm, p, 2mm, p, 2mm, p, 2mm, p");
        builder = new PanelBuilder(layout);
		cc = new CellConstraints();
		inc = 2;
		
        this.add(iFrame);
    }

    /**
     * Ajoute un bouton dans la barre de Navigation
     * @param action Action liée au bouton
     */
    public void addButton(BasicAction action)
    {
        JButton button = new JButton(action) {
            /**
			 * 
			 */
			private static final long serialVersionUID = -8635984393664103035L;

			public void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D)g;
                
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                     RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                super.paintComponent(g2d);
            }
        };
        button.setOpaque(false);
        button.setBorder(null);
        button.setHorizontalAlignment(JButton.LEFT);
          
        button.setForeground(new Color(70,47,47));
        builder.add(button, cc.xy(2, inc));
        inc += 2;
        
        if(centre != null)
            iFrame.remove(centre);
        
        centre = builder.getPanel();
        centre.setBackground(Color.white);
        iFrame.add(BorderLayout.CENTER, centre);        
    }
}