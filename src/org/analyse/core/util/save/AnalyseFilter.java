/*
 * 02/03/2004 - 14:57:28
 *
 * AnalyseFilter.java - 
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

package org.analyse.core.util.save;

/**
 * La class <code>AnalyseFilter</code> représente un filtre qui contient le
 * code pour sauvegarder et/ou charger des données depuis un fichier externe de
 * type binaire ou texte selon les filtres.
 */
public abstract class AnalyseFilter
{
    protected String ID;

    protected String extention;

    /**
     * Créer un nouveau <code>AnalyseFilter</code>.
     * 
     * @param name
     *            identifiant du Filtre.
     * @param extention
     *            extention associée au filtre.
     * @param load
     *            indique si le filtre prend en charge les chargements.
     * @param save
     *            indique si le filtre prend en charge les sauvegarde.
     */
    public AnalyseFilter(String ID, String extention)
    {
        this.ID = ID;
        this.extention = extention;
    }

    /**
     * Retourne l'extention associée au filtre.
     */
    public String getExtension()
    {
        return extention;
    }

    /**
     * Retourne une description du filtre.
     */
    public abstract String getDescription();

    /**
     * Retourne l'identifiant du filtre.
     */
    public String getID()
    {
        return ID;
    }

    public boolean canSave()
    {
        return this instanceof Save;
    }

    public boolean canOpen()
    {
        return this instanceof Open;
    }
}