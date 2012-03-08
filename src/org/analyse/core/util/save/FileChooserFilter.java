/*
 * 11/20/2003 - 23:01:58
 * 
 * FileChooserFilter.java - Copyright (C) 2003 Dreux Loic dreuxl@free.fr
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

package org.analyse.core.util.save;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class FileChooserFilter extends FileFilter
{
    private String extension;

    private String description;

    private String ID;

    public FileChooserFilter(String ID)
    {
        this.ID = ID;
    }

    public boolean accept(File f)
    {
        if (f == null)
            return false;

        if (f.isDirectory())
            return true;

        return extension.equals(getExtension(f));
    }

    public String getExtension(File f)
    {
        if (f == null)
            return null;

        String filename = f.getName();
        int i = filename.lastIndexOf('.');

        return (i > 0 && i < filename.length() - 1) ? filename.substring(i + 1)
                .toLowerCase() : null;
    }

    public void setExtension(String extension)
    {
        this.extension = extension.toLowerCase();
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        String desc = description + " (" + extension + ")";

        return desc;
    }

    public String getExtension()
    {
        return extension;
    }

    public String getID()
    {
        return ID;
    }
}

