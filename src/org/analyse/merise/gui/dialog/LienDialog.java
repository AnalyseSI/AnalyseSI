/*
 * 10/15/2003 - 22:10:05
 * 
 * LienDialog.java - Copyright (C) 2003 Dreux Loic dreuxl@free.fr
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

package org.analyse.merise.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.analyse.core.gui.action.BasicAction;
import org.analyse.core.util.Constantes;
import org.analyse.core.util.GUIUtilities;
import org.analyse.core.util.MyPanelFactory;
import org.analyse.core.util.Utilities;
import org.analyse.main.Main;
import org.analyse.merise.mcd.composant.MCDLien;

public class LienDialog extends JDialog
{
    private BasicAction ok;

    private BasicAction cancel;

    private JPanel panel;

    private JLabel title;

    private JComboBox<String> comboBox;

    private MCDLien mcdlien;

    private ActionHandler handler;

    private final static String[] card = { "0, 1", "0, N", "1, 1", "1, N" };

    public LienDialog()
    {
        super(Main.analyseFrame, Utilities.getLangueMessage(Constantes.MESSAGE_LIEN), true);

        initAction();

        Container c = this.getContentPane();
        JPanel p = new JPanel();
        //p.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        p.setLayout(new BorderLayout());

        p.add(BorderLayout.NORTH, title = MyPanelFactory.createAntialiasingTitle( Utilities.getLangueMessage(Constantes.MESSAGE_LIEN) ));
        p.add(BorderLayout.CENTER, buildPanel());
        p.add(BorderLayout.SOUTH, MyPanelFactory.createBottomWhitePanel(new JButton(ok), new JButton(cancel)));

        c.add(p);

        this.setSize(250, 200);
        this.setResizable(false);
        GUIUtilities.centerComponent(this);
    }

    private JPanel buildPanel()
    {
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        panel.add(BorderLayout.CENTER, new JPanel() {
            {
                setLayout(new FlowLayout());
                add(new JLabel( Utilities.getLangueMessage(Constantes.MESSAGE_CARDINALITE) + " : ", SwingConstants.LEFT),
                        BorderLayout.WEST);
                add(comboBox = new JComboBox<String>(card), BorderLayout.CENTER);
            }
        });

        return new JPanel(new BorderLayout()) {
            {
                this.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
                this.add(BorderLayout.CENTER, new JPanel(new BorderLayout()) {
                    {
                        this.setBorder(BorderFactory.createEtchedBorder());
                        this.add(BorderLayout.CENTER, panel);
                    }
                });
            }
        };
    }

    public void load(MCDLien mcdlien)
    {
        this.mcdlien = mcdlien;

        String cardStr = mcdlien.getCardMin() + ", " + mcdlien.getCardMax();

        for (int i = 0; i < card.length; i++) {
            if (cardStr.equals(card[i])) {
                comboBox.setSelectedIndex(i);
                break;
            }
        }

        this.setVisible(true);
    }

    private void initAction()
    {
        handler = new ActionHandler();

        ok = new BasicAction(Utilities.getLangueMessage("ok"), 
        		Utilities.getLangueMessage(Constantes.MESSAGE_ENREGISTRER_CHANGEMENT) , Constantes.OK, null,
                0, null);
        ok.addActionListener(handler);
        cancel = new BasicAction(Utilities.getLangueMessage(Constantes.MESSAGE_ANNULER),
        		Utilities.getLangueMessage(Constantes.MESSAGE_ANNULER_CHANGEMENT),
                Constantes.CANCEL, null, 0, null);
        cancel.addActionListener(handler);
    }

    public void ok()
    {
        if (comboBox.getSelectedIndex() < 2)
            mcdlien.setCardMin("0");
        else
            mcdlien.setCardMin("1");

        if (comboBox.getSelectedIndex() == 0
                || comboBox.getSelectedIndex() == 2)
            mcdlien.setCardMax("1");
        else
            mcdlien.setCardMax("N");

        close();
    }

    public void close()
    {
        this.setVisible(false);
    }

    private class ActionHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String action = e.getActionCommand();

            if (action.equals(Constantes.OK)) {
                ok();
            } else if (action.equals(Constantes.CANCEL)) {
                close();
            }
        }
    }
}