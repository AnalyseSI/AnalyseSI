/*
 * 05/19/2003 - 14:10:04
 * 
 * AnalyseToolbar.java - Copyright (C) 2003 Dreux Loic dreuxl@free.fr
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

package org.analyse.core.gui.toolbar;

import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.MouseInputAdapter;

import org.analyse.core.gui.action.MainActionListener;
import org.analyse.core.util.Constantes;
import org.analyse.main.Main;

public class AnalyseToolbar extends JPanel
{
    private MouseInputAdapter handler;

    private JToggleButton tree;

    public AnalyseToolbar()
    {
        handler = Main.statusbar.getHandler();

        setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton bi;

        MainActionListener actionListener = new MainActionListener();

        bi = new JButton(Main.globalActionCollection.getAction(Constantes.NEW ));
        bi.addMouseListener(handler);
        bi.setText("");
        this.add(bi);

        bi = new JButton(Main.globalActionCollection.getAction(Constantes.OPEN));
        bi.addMouseListener(handler);
        bi.setText("");
        this.add(bi);

        bi = new JButton(Main.globalActionCollection.getAction(Constantes.SAVE));
        bi.addMouseListener(handler);
        bi.setText("");
        this.add(bi);

        bi = new JButton(Main.globalActionCollection.getAction(Constantes.SAVEAS));
        bi.addMouseListener(handler);
        bi.setText("");
        this.add(bi);

        this.add(new JToolBar.Separator());

        tree = new JToggleButton(Main.globalActionCollection.getAction(Constantes.SHOWHIDE_NAVIGATOR));
        tree.addMouseListener(handler);
        tree.setText("");
        this.add(tree);

        this.add(new JToolBar.Separator());
        
        bi = new JButton(Main.globalActionCollection.getAction(Constantes.ABOUT));
        bi.addMouseListener(handler);
        bi.setText("");
        this.add(bi);
/*
 * suite sur la 0.76
        this.add(new JToolBar.Separator());
        
        bi = new JButton(Main.globalActionCollection.getAction(Constantes.PARAMETRAGE));
        bi.addMouseListener(handler);
        bi.setText("");
        this.add(bi);
  */      
    }

    public void addButton(JButton button)
    {
        this.add(button, this.getComponentCount() - 2);
    }

    public boolean getTreeSelected()
    {
        return tree.getSelectedObjects() != null;
    }

    public void setTreeSelected(boolean value)
    {
        tree.setSelected(value);
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        int h = this.getSize().height;
        int w = this.getSize().width;

        g2d.setPaint(new GradientPaint(0, 0, super.getBackground(), w, 0, super
                .getBackground().brighter()));
        g2d.fillRect(0, 0, w, h);
    }
}