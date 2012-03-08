/*
 * 05/19/2003 - 10:59:58
 * 
 * AnalyseStatusbar.java - Copyright (C) 2003 Dreux Loic dreuxl@free.fr
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

package org.analyse.core.gui.statusbar;

import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.event.MouseInputAdapter;

import org.analyse.core.util.Constantes;
import org.analyse.core.util.Utilities;

public class AnalyseStatusbar extends JLabel
{
    private MouseInputAdapter mouseHandler;

    private boolean enabledChange;

    public AnalyseStatusbar()
    {
        setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 2));

        enabledChange = true;

        mouseHandler = new MouseHandler(this);
        this.setText( Utilities.getRelease() );
    }

    public MouseInputAdapter getHandler()
    {
        return mouseHandler;
    }

    private class MouseHandler extends MouseInputAdapter
    {
        public JLabel label;

        public MouseHandler(JLabel label)
        {
            this.label = label;
        }

        public void mouseEntered(MouseEvent evt)
        {
            if (evt.getSource() instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) evt.getSource();
                Action action = button.getAction();
                if (action != null && enabledChange) {
                    String message = (String) action
                            .getValue(Action.LONG_DESCRIPTION);
                    label.setText(message);
                }
            }
        }
    }
}