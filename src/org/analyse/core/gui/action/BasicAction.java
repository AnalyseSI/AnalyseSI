/*
 * 05/21/2003 - 11:59:05
 * 
 * BasicAction.java - Copyright (C) 2003 Dreux Loic dreuxl@free.fr
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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.event.EventListenerList;

public class BasicAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2516617444390483758L;
	private EventListenerList actionListeners;

	public BasicAction(String name, String longDescription,
			String actionCommand, ImageIcon small_icon, int mnemonic,
			KeyStroke keyStroke) {
		super();
		actionListeners = new EventListenerList();

		putValue(Action.NAME, name);
		putValue(Action.LONG_DESCRIPTION, longDescription);
		putValue(Action.SHORT_DESCRIPTION, longDescription);
		putValue(Action.ACTION_COMMAND_KEY, actionCommand);
		putValue(Action.ACCELERATOR_KEY, keyStroke);
		if (small_icon != null)
			putValue(Action.SMALL_ICON, small_icon);
		if (mnemonic != 0)
			putValue(Action.MNEMONIC_KEY, new Integer(mnemonic));
	}

	public void actionPerformed(ActionEvent evt) {
		if (actionListeners != null) {
			Object[] listenerList = actionListeners.getListenerList();

			// Recreate the ActionEvent and stuff the value of the
			// ACTION_COMMAND_KEY
			ActionEvent e = new ActionEvent(evt.getSource(), evt.getID(),
					(String) getValue(Action.ACTION_COMMAND_KEY));
			for (int i = 0; i <= listenerList.length - 2; i += 2) {
				((ActionListener) listenerList[i + 1]).actionPerformed(e);
			}
		}
	}

	public void addActionListener(ActionListener listener) {
		actionListeners.add(ActionListener.class, listener);
	}

	public void removeActionListener(ActionListener listener) {
		actionListeners.remove(ActionListener.class, listener);
	}
}