/*
 * 05/19/2003 - 10:48:54
 * 
 * AnalyseMenu.java - Copyright (C) 2003 Dreux Loic dreuxl@free.fr
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

package org.analyse.core.gui.menu;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.MouseInputAdapter;

import org.analyse.core.gui.action.MainActionListener;
import org.analyse.main.Main;

public class AnalyseMenu
{
    private JMenuBar menuBar;

    private Map<String, JMenuItem> menuItemTable;
    
    private MouseInputAdapter handler;

    public AnalyseMenu()
    {
        menuBar = new JMenuBar() {
            public void paintComponent(Graphics g)
            {
                super.paintComponent(g);

                Graphics2D g2d = (Graphics2D) g;
                int h = this.getSize().height;
                int w = this.getSize().width;

                //g2d.setPaint(new GradientPaint(0,0,Color.WHITE, w, h, new
                // Color(175,175,255)));
                g2d.setPaint(new GradientPaint(0, 0, /*super.getBackground()*/ new Color(227,236,217), w,
                        0, super.getBackground().brighter()));
                g2d.fillRect(0, 0, w, h);
            }
        };
        menuBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        menuItemTable = new HashMap<String, JMenuItem>();
    }

    public void init()
    {
        handler = Main.statusbar.getHandler();
        MainActionListener actionListener = new MainActionListener();
/*
        JMenu menu, subMenu;
        JMenuItem menuItem;

        menu = new JMenu(Utilities.getLangueMessage(Constantes.MESSAGE_PROJET));
        menu.setMnemonic('p');

        menuItem = new JMenuItem(Main.globalActionCollection.getAction(Constantes.NEW));
        menuItem.addMouseListener(handler);
        menu.add(menuItem);

        menuItem = new JMenuItem(Main.globalActionCollection.getAction(Constantes.OPEN));
        menuItem.addMouseListener(handler);
        menu.add(menuItem);

        menuItem = new JMenuItem(Main.globalActionCollection.getAction(Constantes.SAVE));
        menuItem.addMouseListener(handler);
        menu.add(menuItem);

        menuItem = new JMenuItem(Main.globalActionCollection.getAction(Constantes.SAVEAS));
        menuItem.addMouseListener(handler);
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem(Main.globalActionCollection.getAction(Constantes.QUIT));
        menuItem.addMouseListener(handler);
        menu.add(menuItem);

        menuBar.add(menu);

        menu = new JMenu(Utilities.getLangueMessage(Constantes.MESSAGE_AIDE));
        menu.setMnemonic('a');

        menuItem = new JMenuItem(Main.globalActionCollection.getAction(Constantes.ABOUT));
        menuItem.addMouseListener(handler);
        menu.add(menuItem);

        menuBar.add(menu);
        
        */
        
    }

    public void addMenu(JMenu menu)
    {
        menuBar.add(menu, menuBar.getMenuCount() - 1);
    }

    public void updateMenu()
    {
    }

    public JMenuBar getMenuBar()
    {
        return menuBar;
    }
}