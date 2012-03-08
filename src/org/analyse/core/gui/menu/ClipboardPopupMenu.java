/*
 * 05/26/2003 - 11:11:14
 * 
 * ClipboardPopupMenu.java - Copyright (C) 2003 Dreux Loic dreuxl@free.fr
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

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.MouseInputAdapter;

import org.analyse.core.util.Constantes;
import org.analyse.main.Main;

public class ClipboardPopupMenu extends JPopupMenu
{
    private MouseInputAdapter handler;

    public ClipboardPopupMenu()
    {
        this(true, true, true);
    }

    public ClipboardPopupMenu(boolean cut, boolean copy, boolean paste)
    {
        super();

        JMenuItem menuItem;
        handler = Main.statusbar.getHandler();

        if (cut) {
            menuItem = new JMenuItem(Main.globalActionCollection
                    .getAction(Constantes.CUT));
            menuItem.addMouseListener(handler);
            this.add(menuItem);
        }

        if (copy) {
            menuItem = new JMenuItem(Main.globalActionCollection
                    .getAction(Constantes.COPY));
            menuItem.addMouseListener(handler);
            this.add(menuItem);
        }

        if (paste) {
            menuItem = new JMenuItem(Main.globalActionCollection
                    .getAction(Constantes.PASTE));
            menuItem.addMouseListener(handler);
            this.add(menuItem);
        }
    }
}