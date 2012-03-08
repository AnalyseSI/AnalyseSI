/*
* 28 févr. 2005 - 10:48:36
*
* MyPanelFactory.java
* Copyright (C) 2004 Dreux Loic
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
package org.analyse.core.util;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MyPanelFactory
{
    
    public static JPanel createBottomWhitePanel(JButton button1)
    {
        return createBottomWhitePanel(button1, null, null);
    }
    
    public static JPanel createBottomWhitePanel(JButton button1, JButton button2)
    {
        return createBottomWhitePanel(button1, button2, null);
    }
    
    /**
     * Créer un Panel Blanc contenant 3 boutons.
     * 
     * Ce panel est destiné à être utilisé dans des dialogues.
     */
    public static JPanel createBottomWhitePanel(JButton button1, JButton button2, JButton button3)
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT)) {
            public void paintComponent(Graphics g)
            {
                super.paintComponent(g);

                g.setColor(Color.BLACK);
                g.drawLine(0, 0, this.getWidth(), 0);
            }
        };
        
        panel.setBackground(Color.WHITE);
        panel.add(button1);
        if(button2 != null)
            panel.add(button2);
        if(button3 != null)
            panel.add(button3);
        
        return panel;
    }
    
    /**
     * Créer un titre antialiasé.
     * @param text
     * @return
     */
    public static JLabel createAntialiasingTitle(String text)
    {
        return new JLabel("<html><font size=+1><i>" + text + "</i></font></html>") {
            {
                setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            }

            // Activation de l'antialiasing
            public void paintComponent(Graphics g)
            {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                super.paintComponent(g2d);
            }
        };
    }
}
