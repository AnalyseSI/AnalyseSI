/*
 * 07/02/2003 - 10:44:43
 * 
 * ArgObserverTable.java - Copyright (C) 2003 Dreux Loic dreuxl@free.fr
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

package org.analyse.merise.gui.table;

public class ArgObserverTable
{
    public static final boolean RENAME = true;

    public static final boolean DELETE = false;

    private boolean type;

    private String name;

    private String oldName, newName;

    public ArgObserverTable(boolean type, String name)
    {
        this.type = type;
        this.name = name;
    }

    public ArgObserverTable(boolean type, String oldName, String newName)
    {
        this.type = type;
        this.oldName = oldName;
        this.newName = newName;
    }

    public boolean getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }

    public String getOldName()
    {
        return oldName;
    }

    public String getNewName()
    {
        return newName;
    }

    public String toString()
    {
        return type + " - " + (type == RENAME ? oldName + "," + newName : name);
    }
}