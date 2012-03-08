/*
* 9 fevr. 2005 - 10:44:03
*
* NavigationAction.java
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
package org.analyse.core.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import org.analyse.core.gui.AnalyseFrame;
import org.analyse.core.modules.AnalysePanel;

public class NavigationActionFactory implements ActionListener
{
    public AnalyseFrame analyseFrame;
    private Map<String, AnalysePanel> hashtable;
    
    public NavigationActionFactory(AnalyseFrame analyseFrame)
    {
        this.analyseFrame = analyseFrame;
        
        /* Initialise la hashtable contenant les Panels */
        hashtable = new HashMap<String, AnalysePanel>();
    }
    
    public BasicAction buildNavigationAction(ImageIcon icon, String name, String desc, AnalysePanel panel)
    {
        BasicAction action = new BasicAction(name, desc, panel.getID(), icon, 0, null);
        action.addActionListener(this);
        hashtable.put(panel.getID(), panel);
        
        return action;
    }
    
    public void actionPerformed(ActionEvent e)
    {
        analyseFrame.setPanel((AnalysePanel)hashtable.get(e.getActionCommand()));
    }
}
