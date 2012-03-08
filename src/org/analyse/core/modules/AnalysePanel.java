/*
 * 05/26/2003 - 09:53:06
 *
 * PanelModule.java - 
 * Copyright (C) 2003 Dreux Loic
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

package org.analyse.core.modules;

import javax.swing.JPanel;

/**
 * La classe panel Module est dérivé <code>JPanel</code> et peut implémenter
 * les interfaces <code>UndoInterface</code> et <code>ClipboardInterface</code>.
 */

public abstract class AnalysePanel extends JPanel
{
    private boolean undoEnabled;

    private boolean redoEnabled;

    private boolean copyEnabled;

    private boolean pasteEnabled;
    
    private String ID;

    public AnalysePanel(String ID) {
        this.ID = ID;
        undoEnabled = this instanceof UndoInterface;
        redoEnabled = this instanceof UndoInterface;
        copyEnabled = this instanceof ClipboardInterface;
        pasteEnabled = this instanceof ClipboardInterface;
    }

    /**
     * Récupère l'Identifiant
     */
    public String getID() {
        return ID;
    }
    
    /**
     * Indique si le bouton Undo doit etre actif.
     */
    public boolean getUndoEnabled()  {
        return this instanceof UndoInterface && undoEnabled;
    }

    /**
     * Indique si le bouton Redo doit etre actif.
     */
    public boolean getRedoEnabled()  {
        return this instanceof UndoInterface && redoEnabled;
    }

    /**
     * Indique si le bouton Copy doit etre actif.
     */
    public boolean getCopyEnabled() {
        return this instanceof ClipboardInterface && copyEnabled;
    }

    /**
     * Indique si le bouton Paste doit etre actif.
     */
    public boolean getPasteEnabled() {
        return this instanceof ClipboardInterface && pasteEnabled;
    }

    /**
     * Active le bouton Undo dans l'interface.
     */
    public void setUndoEnabled(boolean undoEnabled) {
        this.undoEnabled = undoEnabled;
    }

    /**
     * Active le bouton Redo dans l'interface.
     */
    public void setRedoEnabled(boolean redoEnabled) {
        this.redoEnabled = redoEnabled;
    }

    /**
     * Active le bouton Copy dans l'interface.
     */
    public void setCopyEnabled(boolean copyEnabled) {
        this.copyEnabled = copyEnabled;
    }

    /**
     * Active le bouton Paste dans l'interface.
     */
    public void setPasteEnabled(boolean pasteEnabled) {
        this.pasteEnabled = pasteEnabled;
    }
}