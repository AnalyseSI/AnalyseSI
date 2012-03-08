/*
 * 05/19/2003 - 10:40:44
 * 
 * MainActionListener.java - Copyright (C) 2003 Dreux Loic dreuxl@free.fr
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

package org.analyse.core.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.analyse.core.modules.AnalysePanel;
import org.analyse.core.modules.ClipboardInterface;
import org.analyse.core.modules.UndoInterface;
import org.analyse.core.util.Constantes;
import org.analyse.core.util.save.AnalyseSave;
import org.analyse.main.Main;

public class MainActionListener implements ActionListener
{
    public void actionPerformed(ActionEvent e)
    {
        String action = e.getActionCommand();
        AnalysePanel p = Main.analyseFrame.getCurrentPanel();

        if (action.equals(Constantes.QUIT)) {
            Main.analyseFrame.exit();
        } else if (action.equals(Constantes.ABOUT)) {
            Main.aboutWindow.setVisible(true);
        } else if (action.equals(Constantes.UNDO)) {
            UndoInterface u;

            try {
                u = (UndoInterface) p;
                u.undo();
            } catch (ClassCastException exp) {
                System.err.println(exp);
            }
        } else if (action.equals(Constantes.REDO)) {
            UndoInterface u;

            try {
                u = (UndoInterface) p;
                u.redo();
            } catch (ClassCastException exp) {
                System.err.println(exp);
            }
        } else if (action.equals(Constantes.CUT)) {
            ClipboardInterface c;

            try {
                c = (ClipboardInterface) p;
                c.cut();
            } catch (ClassCastException exp) {
                System.err.println(exp);
            }
        } else if (action.equals(Constantes.COPY)) {
            ClipboardInterface c;
            try {
                c = (ClipboardInterface) p;
                c.copy();
            } catch (ClassCastException exp) {
                System.err.println(exp);
            }
        } else if (action.equals(Constantes.PASTE)) {
            ClipboardInterface c;

            try {
                c = (ClipboardInterface) p;
                c.paste();
            } catch (ClassCastException exp) {
                System.err.println(exp);
            }
        } else if (action.equals(Constantes.NEW)) {
            AnalyseSave s = Main.analyseFrame.getAnalyseSave();
            s.setNewOption(true) ; 
            s.save();
            s.clear();
            
        } else if (action.equals(Constantes.OPEN)) {
            AnalyseSave s = Main.analyseFrame.getAnalyseSave();

            s.open();
        } else if (action.equals(Constantes.SAVE)) {
            AnalyseSave s = Main.analyseFrame.getAnalyseSave();

            s.save();
        } else if (action.equals(Constantes.SAVEAS)) {
            AnalyseSave s = Main.analyseFrame.getAnalyseSave();

            s.saveAs();
        } else if (action.equals(Constantes.SHOWHIDE_NAVIGATOR)) {
            Main.analyseFrame.showHideNavigator();          
        } else if (action.equals(Constantes.PARAMETRAGE)) {
        	Main.parametrageWindow.setVisible(true);       
        } 
    }
}