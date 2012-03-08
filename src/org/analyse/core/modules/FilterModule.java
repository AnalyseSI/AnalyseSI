/*
 * 02/04/2004 - 23:57:57
 * 
 * FilterModule.java - Copyright (C) 2004 Dreux Loic dreuxl@free.fr
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

public abstract class FilterModule
{
    private String ID;

    protected AnalyseModule module;

    public FilterModule(String ID, AnalyseModule module)
    {
        this.ID = ID;
        this.module = module;
    }

    public String getID()
    {
        return ID;
    }

    public boolean canSave()
    {
        return this instanceof SaveModule;
    }

    public boolean canOpen()
    {
        return this instanceof OpenModule;
    }
}