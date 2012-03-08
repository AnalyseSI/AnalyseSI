/*
 * 06/25/2003 - 14:09:30
 * 
 * MeriseListModel.java - Copyright (C) 2003 Dreux Loic dreuxl@free.fr
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

package org.analyse.merise.gui.list;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractListModel;

public class MeriseListModel extends AbstractListModel
{
    private Hashtable<String,String> listLabel;

    private List<String> listKey;

    public static final boolean DOWN = true;

    public static final boolean UP = false;

    public MeriseListModel()
    {
        super();

        listLabel = new Hashtable<String,String>();
        listKey = new ArrayList<String>();
    }

    public void addElement(String key, String label)
    {
        if (!listLabel.containsKey(key)) {
            listLabel.put(key, label);
            listKey.add(key);
        }
        fireIntervalAdded(this, 0, listKey.size());
    }

    public void removeElement(String key)
    {
        listLabel.remove(key);
        listKey.remove(key);
        fireIntervalRemoved(this, 0, listKey.size());
    }

    public boolean moveLines(int ligneDebut, int ligneFin, boolean sens)
    {
        String o;
        if (ligneDebut == 0 && sens == UP || ligneFin >= listKey.size() - 1
                && sens == DOWN || ligneDebut > ligneFin)
            return false;

        if (sens == UP) {
            for (int i = ligneDebut - 1; i < ligneFin; i++) {
                o = listKey.get(i);
                listKey.set(i, listKey.get(i + 1));
                listKey.set(i + 1, o);
            }
        } else {
            for (int i = ligneFin; i > ligneDebut - 1; i--) {
                o = listKey.get(i);
                listKey.set(i, listKey.get(i + 1));
                listKey.set(i + 1, o);
            }
        }
        fireIntervalRemoved(this, 0, listKey.size());
        return true;
    }

    public void clear()
    {
        listLabel.clear();
        listKey.clear();
    }

    public Object getElementAt(int index)
    {
        return listLabel.get(listKey.get(index));
    }

    public String getKey(int index)
    {
        return (String) listKey.get(index);
    }

    public Hashtable<String,String>  getListLabel() {
        return listLabel ;
    }

    public int getSize()
    {
        return listKey.size();
    }
}