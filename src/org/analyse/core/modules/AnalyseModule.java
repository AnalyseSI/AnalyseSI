/*
 * 05/16/2003 - 12:00:17
 * 
 * AnalyseModule.java - Copyright (C) 2003 Dreux Loic dreuxl@free.fr
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

package org.analyse.core.modules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.DefaultTreeCellRenderer;

import org.analyse.core.gui.AnalyseFrame;

public abstract class AnalyseModule
{
    protected DefaultTreeCellRenderer treeCellRendererModule;

    protected List<FilterModule> filtres;

    public AnalyseModule()
    {
        filtres = new ArrayList<FilterModule>();
    }

    /** Initialise le module après la création de AnalyseFrame */
    public abstract void initGUI(AnalyseFrame analyseFrame);

    /**
     * Retourne le nom du module
     */
    public abstract String getName();

    /**
     * Retourne l'autheur du module
     */
    public abstract String getAuthor();

    /**
     * Retourne l'ID du module
     */
    public abstract String getID();

    public abstract void clear();

    public DefaultTreeCellRenderer getTreeCellRenderer()
    {
        return treeCellRendererModule;
    }

    public FilterModule getFiltre(String ID)
    {
        FilterModule fm;
        for (Iterator<FilterModule> e = filtres.iterator(); e.hasNext();) {
            fm = e.next();
            if (fm.getID().equals(ID))
                return fm;
        }
        return null;
    }
}