/*
 * Created on 20.06.2005
 *
 */
package org.jdesktop.demo.swingx.common;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

/**
 * A static snapshot of a container hierarchy.
 * 
 * NOTE: does not listen to any property changes of
 * contained components - cell updates are arbitrary on
 * repaint only!
 * 
 * @author Jeanette Winzenburg
 */
public class ComponentTreeTableModel extends AbstractTreeTableModel {


    public ComponentTreeTableModel(Container root) {
        super(root);
        setRoot(root);
    }

    public void setRoot(Container root) {
        if (root == null) {
            root = new JXFrame();
        }
        this.root = root;
        fireTreeStructureChanged(this, new Object[]{ root }, null, null);
    }
    
    //  ------------------TreeModel
    
    
    public Object getChild(Object parent, int index) {
        return ((Container) parent).getComponent(index);
    }
    
    public int getChildCount(Object parent) {
        return parent instanceof Container ? ((Container) parent).getComponentCount() : 0;
    }
    
    public int getIndexOfChild(Object parent, Object child) {
        Component[] children = ((Container) parent).getComponents();
        for (int i = 0; i < children.length; i++) {
            if (children[i].equals(child)) {
                return i;
            }
        }
        return -1;
    }
    
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }
 
    public String convertValueToText(Object node) {
        String className = node.getClass().getName();
        int lastDot = className.lastIndexOf(".");
        String lastElement = className.substring(lastDot + 1);
        return lastElement;
    }
//------------------ TreeTableModel    

    public Class getColumnClass(int column) {
        switch (column) {
        case 0:
            return hierarchicalColumnClass;
        case 1:
            return Point.class;
        case 2:
            return Dimension.class;
        default:
            return Object.class;
        }
    }
    
    public int getColumnCount() {
        return 3;
    }
    public String getColumnName(int column) {
        switch (column) {
        case 0:
            return "Type";
        case 1:
            return "Location";
        case 2:
            return "Size";
        default:
            return "Column " + column;
        }
    }
    
    public Object getValueAt(Object node, int column) {
        Component comp = (Component) node;
        switch (column) {
        case 0:
            return comp;
        case 1:
            return comp.getLocation();
        case 2:
            return comp.getSize();
        default:
            return null;
        }
    }

    public void setValueAt(Object value, Object node, int column) {

    }

    
}
