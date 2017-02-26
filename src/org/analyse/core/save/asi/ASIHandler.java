/*
 * 02/06/2004 - 15:21:29
 * 
 * ASIHandler - Copyright (C) 2004 Dreux Loic dreuxl@free.fr
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

package org.analyse.core.save.asi;

import java.util.Deque;
import java.util.LinkedList;

import org.analyse.core.modules.AnalyseModule;

import com.microstar.xml.HandlerBase;

public class ASIHandler extends HandlerBase
{
    private Deque<String> stack;

    private AnalyseModule currentModule;

    private ASIModuleHandler asiModuleHandler;

    private String id;

    public ASIHandler()
    {
    }

    public void startDocument()
    {
        stack = new LinkedList<String>();
        currentModule = null;
    }

    public void endDocument()
    {
    }

    public void attribute(String aname, String value, boolean isSpecified)
    {
        if (aname.equals("id") && currentModule == null)
            id = value;

        if (currentModule != null) {
            asiModuleHandler.attribute(aname, value, isSpecified);
        }

    }

    public void startElement(String name)
    {
        stack.addFirst(name);

        if (name.equals("module")) {
            currentModule = org.analyse.main.Main.getModule(id.toUpperCase());
            asiModuleHandler = ((FilterASIModule) (currentModule
                    .getFiltre("ASI"))).getASIHandler();

        } else if (currentModule != null) {
            asiModuleHandler.startElement(name);
        }
    }

    public void endElement(String name)
    {
        if (name == null)
            return;

        String lastStartTag = stack.peekFirst();

        if (name.equalsIgnoreCase(lastStartTag)) {
            if (name.equals("module")) {
                currentModule = null;
            } else if (currentModule != null) {
                asiModuleHandler.endElement(name);
            }

            stack.removeFirst();
        } else
            System.err.println("Unclosed tag: " + stack.peekFirst());
    }
}