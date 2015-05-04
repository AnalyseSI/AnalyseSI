/*
 * 03/04/2004 - 15:13:43
 *
 * ConnectionDialog.java - 
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

package org.analyse.merise.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.analyse.core.gui.action.BasicAction;
import org.analyse.core.util.Constantes;
import org.analyse.core.util.GUIUtilities;
import org.analyse.core.util.MyBorderFactory;
import org.analyse.core.util.MyPanelFactory;
import org.analyse.core.util.Utilities;
import org.analyse.main.Main;
import org.analyse.merise.sql.SQLCommand;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Cette fenêtre permet d'établir une connection avec une base de donnée grâce à
 * la class SQLCommand.
 */
public class ConnectionDialog extends JDialog
{
    private BasicAction ok, cancel;

    private ActionHandler handler;

    private SQLCommand sqlCommand;

    private JComboBox<String> driver;

    private JTextField url, user, password;

    private String[] typeConnections;
    private String[] libelleConnections;
    private String[] urlStartConnections;

    public ConnectionDialog(SQLCommand sqlCommand)
    {
        super(Main.analyseFrame, Utilities.getLangueMessage(Constantes.MESSAGE_CONNEXION), true);

        this.handler = new ActionHandler();
        this.sqlCommand = sqlCommand;

        typeConnections = new String[3];
        typeConnections[0] = "org.gjt.mm.mysql.Driver";
        typeConnections[1] = "org.postgresql.Driver";
        typeConnections[2] = "sun.jdbc.odbc.JdbcOdbcDriver";

        libelleConnections = new String[3];
        libelleConnections[0] = "MySQL";
        libelleConnections[1] = "PostgreSQL";
        libelleConnections[2] = "ODBC";

        urlStartConnections = new String[3];
        urlStartConnections[0] = "jdbc:mysql://localhost/database";    // exemple d'url 
        urlStartConnections[1] = "jdbc:postgresql://localhost:port/database";
        urlStartConnections[2] = "jdbc:odbc:";

        initAction();

        Container c = this.getContentPane();
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());

        p.add(BorderLayout.NORTH, MyPanelFactory.createAntialiasingTitle(Utilities.getLangueMessage(Constantes.MESSAGE_CONNEXION)));
        p.add(BorderLayout.CENTER, createPanel());
        p.add(BorderLayout.SOUTH, MyPanelFactory.createBottomWhitePanel(new JButton(ok), new JButton(cancel)));
        c.add(p);

        this.pack();
        GUIUtilities.centerComponent(this);
    }

    private JPanel createPanel()
    {
        JPanel p, p2, p3;

        driver = new JComboBox(libelleConnections);
        driver.addItemListener(new ItemHandler());

        url = new JTextField();
        user = new JTextField("root");
        password = new JPasswordField();

        url.setText(urlStartConnections[driver.getSelectedIndex()]);

        FormLayout layout = new FormLayout("right:max(40mm;p), 4mm, 45mm", "p, 2mm, p, 2mm, p, 2mm, p");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();

        builder.addLabel(
        		Utilities.getLangueMessage(Constantes.MESSAGE_TYPE_CONNEXION) + " : ", cc.xy(1, 1));
        builder.add(driver, cc.xy(3, 1));
        builder.addLabel(
        		Utilities.getLangueMessage(Constantes.MESSAGE_ADRESSE_URL) + " : ", cc.xy(1, 3));
        builder.add(url, cc.xy(3, 3));
        builder.addLabel(
        		Utilities.getLangueMessage(Constantes.MESSAGE_LOGIN) + " : ", cc.xy(1, 5));
        builder.add(user, cc.xy(3, 5));
        builder.addLabel(
        		Utilities.getLangueMessage(Constantes.MESSAGE_PASSWORD) + " : ", cc.xy(1, 7));
        builder.add(password, cc.xy(3, 7));

        p = builder.getPanel();
        p.setBorder(MyBorderFactory.createEtchedBorder());

        return p;
    }

    /**
     * Ferme la fenetre.
     */
    private void close()
    {
        this.setVisible(false);
    }

    public void initAction()
    {
        ok = new BasicAction(Utilities.getLangueMessage(Constantes.MESSAGE_CONNECTER),
        		Utilities.getLangueMessage(Constantes.MESSAGE_CONNEXION_AVEC_BASE) ,
        		Constantes.CONN, null, 0, null);
        ok.addActionListener(handler);
        cancel = new BasicAction(Utilities.getLangueMessage(Constantes.MESSAGE_ANNULER) ,
        		Utilities.getLangueMessage(Constantes.MESSAGE_FERMER_CETTE_FENETRE), Constantes.CANCEL,
                null, 0, null);
        cancel.addActionListener(handler);
    }

    private class ItemHandler implements ItemListener
    {
        public void itemStateChanged(ItemEvent e)
        {
            url.setText(urlStartConnections[driver.getSelectedIndex()]);
        }
    }

    private class ActionHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String action = e.getActionCommand();

            if (action.equals(Constantes.CANCEL)) {
                close();
            } else if (action.equals(Constantes.CONN)) {
                if (!sqlCommand.connection(typeConnections[driver
                        .getSelectedIndex()], url.getText(), user.getText(),
                        password.getText())) {
                    GUIUtilities.error(sqlCommand.getError());
                } else {
                    close();
                }
            }
        }
    }
}