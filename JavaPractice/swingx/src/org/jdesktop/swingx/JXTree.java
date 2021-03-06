/*
 * $Id: JXTree.java,v 1.15 2005/10/12 11:26:57 kleopatra Exp $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.AbstractSearchable.SearchResult;
import org.jdesktop.swingx.JXList.ListSearchable;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.HighlighterPipeline;


/**
 * JXTree.
 *
 * PENDING: support filtering/sorting.
 * 
 * @author Ramesh Gupta
 * @author Jeanette Winzenburg
 */
public class JXTree extends JTree {
    private Method conversionMethod = null;
    private final static Class[] methodSignature = new Class[] {Object.class};
    private final static Object[] methodArgs = new Object[] {null};

    protected FilterPipeline filters;
    protected HighlighterPipeline highlighters;
    private ChangeListener highlighterChangeListener;

    private DelegatingRenderer delegatingRenderer;

    /**
     * Mouse/Motion/Listener keeping track of mouse moved in
     * cell coordinates.
     */
    private RolloverProducer rolloverProducer;

    /**
     * RolloverController: listens to cell over events and
     * repaints entered/exited rows.
     */
    private LinkController linkController;
    private boolean overwriteIcons;
    private Searchable searchable;
    
    
    
    /**
     * Constructs a <code>JXTree</code> with a sample model. The default model
     * used by this tree defines a leaf node as any node without children.
     */
    public JXTree() {
	initActions();
    }

    /**
     * Constructs a <code>JXTree</code> with each element of the specified array
     * as the child of a new root node which is not displayed. By default, this
     * tree defines a leaf node as any node without children.
     *
     * This version of the constructor simply invokes the super class version
     * with the same arguments.
     *
     * @param value an array of objects that are children of the root.
     */
    public JXTree(Object[] value) {
        super(value);
	initActions();
    }

    /**
     * Constructs a <code>JXTree</code> with each element of the specified
     * Vector as the child of a new root node which is not displayed.
     * By default, this tree defines a leaf node as any node without children.
     *
     * This version of the constructor simply invokes the super class version
     * with the same arguments.
     *
     * @param value an Vector of objects that are children of the root.
     */
    public JXTree(Vector value) {
        super(value);
	initActions();
    }

    /**
     * Constructs a <code>JXTree</code> created from a Hashtable which does not
     * display with root. Each value-half of the key/value pairs in the HashTable
     * becomes a child of the new root node. By default, the tree defines a leaf
     * node as any node without children.
     *
     * This version of the constructor simply invokes the super class version
     * with the same arguments.
     *
     * @param value a Hashtable containing objects that are children of the root.
     */
    public JXTree(Hashtable value) {
        super(value);
	initActions();
    }

    /**
     * Constructs a <code>JXTree</code> with the specified TreeNode as its root,
     * which displays the root node. By default, the tree defines a leaf node as
     * any node without children.
     *
     * This version of the constructor simply invokes the super class version
     * with the same arguments.
     *
     * @param root root node of this tree
     */
    public JXTree(TreeNode root) {
        super(root, false);
    }

    /**
     * Constructs a <code>JXTree</code> with the specified TreeNode as its root,
     * which displays the root node and which decides whether a node is a leaf
     * node in the specified manner.
     *
     * This version of the constructor simply invokes the super class version
     * with the same arguments.
     *
     * @param root root node of this tree
     * @param asksAllowsChildren if true, only nodes that do not allow children
     * are leaf nodes; otherwise, any node without children is a leaf node;
     * @see javax.swing.tree.DefaultTreeModel#asksAllowsChildren
     */
    public JXTree(TreeNode root, boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
		initActions();
    }

    /**
     * Constructs an instance of <code>JXTree</code> which displays the root
     * node -- the tree is created using the specified data model.
     * 
     * This version of the constructor simply invokes the super class version
     * with the same arguments.
     * 
     * @param newModel
     *            the <code>TreeModel</code> to use as the data model
     */
    public JXTree(TreeModel newModel) {
        super(newModel);
        initActions();
        // To support delegation of convertValueToText() to the model...
        conversionMethod = getValueConversionMethod(newModel);
    }

    public void setModel(TreeModel newModel) {
        super.setModel(newModel);
        // To support delegation of convertValueToText() to the model...
        conversionMethod = getValueConversionMethod(newModel);
    }

    private Method getValueConversionMethod(TreeModel model) {
        try {
            return model == null ? null : model.getClass().getMethod(
                    "convertValueToText", methodSignature);
        } catch (NoSuchMethodException ex) {
            // not an error
        }
        return null;
    }

    public String convertValueToText(Object value, boolean selected,
            boolean expanded, boolean leaf, int row, boolean hasFocus) {
        // Delegate to model, if possible. Otherwise fall back to superclass...
        if (value != null) {
            if (conversionMethod == null) {
                return value.toString();
            } else {
                try {
                    methodArgs[0] = value;
                    return (String) conversionMethod.invoke(getModel(),
                            methodArgs);
                } catch (Exception ex) {
                    // fall through
                }
            }
        }
        return "";
    }

    private void initActions() {
        // Register the actions that this class can handle.
        ActionMap map = getActionMap();
        map.put("expand-all", new Actions("expand-all"));
        map.put("collapse-all", new Actions("collapse-all"));
        map.put("find", createFindAction());
        // JW: this should be handled by the LF!
        KeyStroke findStroke = KeyStroke.getKeyStroke("control F");
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(findStroke, "find");

    }

    /**
     * A small class which dispatches actions.
     * TODO: Is there a way that we can make this static?
     */
    private class Actions extends UIAction {
        Actions(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent evt) {
            if ("expand-all".equals(getName())) {
		expandAll();
            }
            else if ("collapse-all".equals(getName())) {
                collapseAll();
            }
        }
    }


//-------------------- search support
    
    private Action createFindAction() {
        Action findAction = new UIAction("find") {

            public void actionPerformed(ActionEvent e) {
                doFind();
                
            }
            
        };
        return findAction;
    }

    protected void doFind() {
        SearchFactory.getInstance().showFindInput(this, getSearchable());
    }

    /**
     * 
     * @returns a not-null Searchable for this editor.  
     */
    public Searchable getSearchable() {
        if (searchable == null) {
            searchable = new TreeSearchable();
        }
        return searchable;
    }

    /**
     * sets the Searchable for this editor. If null, a default 
     * searchable will be used.
     * 
     * @param searchable
     */
    public void setSearchable(Searchable searchable) {
        this.searchable = searchable;
    }
    
 
    /**
     * A searchable targetting the visible rows of a JXTree.
     * 
     * PENDING: value to string conversion should behave as nextMatch (?) which
     * uses the convertValueToString().
     * 
     */
    public class TreeSearchable extends AbstractSearchable {

        @Override
        protected void findMatchAndUpdateState(Pattern pattern, int startRow,
                boolean backwards) {
            SearchResult searchResult = null;
            if (backwards) {
                for (int index = startRow; index >= 0 && searchResult == null; index--) {
                    searchResult = findMatchAt(pattern, index);
                }
            } else {
                for (int index = startRow; index < getSize()
                        && searchResult == null; index++) {
                    searchResult = findMatchAt(pattern, index);
                }
            }
            updateState(searchResult);

        }

        @Override
        protected SearchResult findExtendedMatch(Pattern pattern, int row) {
            return findMatchAt(pattern, row);
        }

        /**
         * Matches the cell content at row/col against the given Pattern.
         * Returns an appropriate SearchResult if matching or null if no
         * matching
         * 
         * @param pattern
         * @param row
         *            a valid row index in view coordinates
         * @param column
         *            a valid column index in view coordinates
         * @return
         */
        protected SearchResult findMatchAt(Pattern pattern, int row) {
            TreePath path = getPathForRow(row);
            Object value = null;
            if (path != null) {
                value = path.getLastPathComponent();
            }
            if (value != null) {
                Matcher matcher = pattern.matcher(value.toString());
                if (matcher.find()) {
                    return createSearchResult(matcher, row, -1);
                }
            }
            return null;
        }

        @Override
        protected int getSize() {
            return getRowCount();
        }

        @Override
        protected void moveMatchMarker() {
            int row = lastSearchResult.foundRow;
            setSelectionRow(row);
            if (row >= 0) {
                scrollRowToVisible(row);
            }

        }

    }
    
    /**
     * Collapses all nodes in the tree table.
     */
    public void collapseAll() {
        for (int i = getRowCount() - 1; i >= 0 ; i--) {
            collapseRow(i);
        }
    }

    /**
     * Expands all nodes in the tree table.
     */
    public void expandAll() {
        for (int i = 0; i < getRowCount(); i++) {
            expandRow(i);
        }
    }


    public HighlighterPipeline getHighlighters() {
        return highlighters;
    }

    /** Assigns a HighlighterPipeline to the table. */
    public void setHighlighters(HighlighterPipeline pipeline) {
        HighlighterPipeline old = getHighlighters();
        if (old != null) {
            old.removeChangeListener(getHighlighterChangeListener());
        }
        highlighters = pipeline;
        if (highlighters != null) {
            highlighters.addChangeListener(getHighlighterChangeListener());
        }
        firePropertyChange("highlighters", old, getHighlighters());
    }

    private ChangeListener getHighlighterChangeListener() {
        if (highlighterChangeListener == null) {
            highlighterChangeListener = new ChangeListener() {

                public void stateChanged(ChangeEvent e) {
                    repaint();
                    
                }
                
            };
        }
        return highlighterChangeListener;
    }


    /**
     * Property to enable/disable rollover support. This can be enabled
     * to show "live" rollover behaviour, f.i. the cursor over LinkModel cells. 
     * Default is disabled.
     * @param rolloverEnabled
     */
    public void setRolloverEnabled(boolean rolloverEnabled) {
        boolean old = isRolloverEnabled();
        if (rolloverEnabled == old) return;
        if (rolloverEnabled) {
            rolloverProducer = createRolloverProducer();
            addMouseListener(rolloverProducer);
            addMouseMotionListener(rolloverProducer);
            linkController = new LinkController();
            addPropertyChangeListener(linkController);
        } else {
            removeMouseListener(rolloverProducer);
            removeMouseMotionListener(rolloverProducer);
            rolloverProducer = null;
            removePropertyChangeListener(linkController);
            linkController = null;
        }
        firePropertyChange("rolloverEnabled", old, isRolloverEnabled());
    }

    /**
     * creates and returns the RolloverProducer to use with this tree.
     * A "hit" for rollover is covering the total width of the tree.
     * Additionally, a pressed to the right (but outside of the label bounds)
     * is re-dispatched as a pressed just inside the label bounds. This 
     * is a first go for #166-swingx.
     * 
     * @return
     */
    protected RolloverProducer createRolloverProducer() {
        RolloverProducer r = new RolloverProducer() {
            
            @Override
            public void mousePressed(MouseEvent e) {
                JXTree tree = (JXTree) e.getComponent();
                Point mousePoint = e.getPoint();
              int labelRow = tree.getRowForLocation(mousePoint.x, mousePoint.y);
              // default selection
              if (labelRow >= 0) return;
              int row = tree.getClosestRowForLocation(mousePoint.x, mousePoint.y);
              Rectangle bounds = tree.getRowBounds(row);
              if (bounds == null) {
                  row = -1;
              } else {
                  if ((bounds.y + bounds.height < mousePoint.y) || 
                       bounds.x > mousePoint.x)   {
                      row = -1;
                  }
              }
              // no hit
              if (row < 0) return;
              tree.dispatchEvent(new MouseEvent(tree, e.getID(), e.getWhen(), 
                      e.getModifiers(), bounds.x + bounds.width - 2, mousePoint.y,
                      e.getClickCount(), e.isPopupTrigger(), e.getButton()));
            }

            protected void updateRolloverPoint(JComponent component,
                    Point mousePoint) {
                JXTree tree = (JXTree) component;
                int row = tree.getClosestRowForLocation(mousePoint.x, mousePoint.y);
                Rectangle bounds = tree.getRowBounds(row);
                if (bounds == null) {
                    row = -1;
                } else {
                    if ((bounds.y + bounds.height < mousePoint.y) || 
                            bounds.x > mousePoint.x)   {
                           row = -1;
                       }
                }
                int col = row < 0 ? -1 : 0;
                rollover.x = col;
                rollover.y = row;
            }

        };
        return r;
    }

  
   
    /**
     * returns the rolloverEnabled property.
     * @return
     */
    public boolean isRolloverEnabled() {
        return rolloverProducer != null;
    }


    /**
     * listens to rollover properties. 
     * Repaints effected component regions.
     * Updates link cursor.
     * 
     * @author Jeanette Winzenburg
     */
    public  class LinkController implements PropertyChangeListener {


        private Cursor oldCursor;
        public void propertyChange(PropertyChangeEvent evt) {
            if (RolloverProducer.ROLLOVER_KEY.equals(evt.getPropertyName())) {
                    rollover((JXTree) evt.getSource(), (Point) evt.getOldValue(),
                            (Point) evt.getOldValue());
            } 
        }
        
        
//    -------------------------------------JTree rollover
        
        private void rollover(JXTree tree, Point oldLocation, Point newLocation) {
            //setLinkCursor(list, newLocation);
            // JW: conditional repaint not working?
            tree.repaint();
//            if (oldLocation != null) {
//                Rectangle r = tree.getRowBounds(oldLocation.y);
////                r.x = 0;
////                r.width = table.getWidth();
//                if (r != null)
//                tree.repaint(r);
//            }
//            if (newLocation != null) {
//                Rectangle r = tree.getRowBounds(newLocation.y);
////                r.x = 0;
////                r.width = table.getWidth();
//                if (r != null)
//                tree.repaint(r);
//            }
        }

    }


    
    private DelegatingRenderer getDelegatingRenderer() {
        if (delegatingRenderer == null) {
            // only called once... to get hold of the default?
            delegatingRenderer = new DelegatingRenderer();
            delegatingRenderer.setDelegateRenderer(super.getCellRenderer());
        }
        return delegatingRenderer;
    }

    public TreeCellRenderer getCellRenderer() {
        return getDelegatingRenderer();
    }

    public void setCellRenderer(TreeCellRenderer renderer) {
        // PENDING: do something against recursive setting
        // == multiple delegation...
        getDelegatingRenderer().setDelegateRenderer(renderer);
        super.setCellRenderer(delegatingRenderer);
    }

    /**
     * sets the icon for the handle of an expanded node.
     * 
     * Note: this will only succeed if the current ui delegate is
     * a BasicTreeUI otherwise it will do nothing.
     * 
     * @param expanded
     */
    public void setExpandedIcon(Icon expanded) {
        if (getUI() instanceof BasicTreeUI) {
            ((BasicTreeUI) getUI()).setExpandedIcon(expanded);
        }
    }
    
    /**
     * sets the icon for the handel of a collapsed node.
     * 
     * Note: this will only succeed if the current ui delegate is
     * a BasicTreeUI otherwise it will do nothing.
     *  
     * @param collapsed
     */
    public void setCollapsedIcon(Icon collapsed) {
        if (getUI() instanceof BasicTreeUI) {
            ((BasicTreeUI) getUI()).setCollapsedIcon(collapsed);
        }
    }
    
    /**
     * set the icon for a leaf node.
     * 
     * Note: this will only succeed if current renderer is a 
     * DefaultTreeCellRenderer.
     * 
     * @param leafIcon
     */
    public void setLeafIcon(Icon leafIcon) {
        getDelegatingRenderer().setLeafIcon(leafIcon);
        
    }
    
    /**
     * set the icon for a open non-leaf node.
     * 
     * Note: this will only succeed if current renderer is a 
     * DefaultTreeCellRenderer.
     * 
     * @param openIcon
     */
    public void setOpenIcon(Icon openIcon) {
        getDelegatingRenderer().setOpenIcon(openIcon);
    }
    
    /**
     * set the icon for a closed non-leaf node.
     * 
     * Note: this will only succeed if current renderer is a 
     * DefaultTreeCellRenderer.
     * 
     * @param closedIcon
     */
    public void setClosedIcon(Icon closedIcon) {
        getDelegatingRenderer().setClosedIcon(closedIcon);
    }
    
    /**
     * Property to control whether per-tree icons should be 
     * copied to the renderer on setCellRenderer.
     * 
     * the default is false for backward compatibility.
     * 
     * PENDING: should update the current renderer's icons when 
     * setting to true?
     * 
     * @param overwrite
     */
    public void setOverwriteRendererIcons(boolean overwrite) {
        if (overwriteIcons == overwrite) return;
        boolean old = overwriteIcons;
        this.overwriteIcons = overwrite;
        firePropertyChange("overwriteRendererIcons", old, overwrite);
    }

    public boolean isOverwriteRendererIcons() {
        return overwriteIcons;
    }
    
    public class DelegatingRenderer implements TreeCellRenderer {
        private Icon    closedIcon = null;
        private Icon    openIcon = null;
        private Icon    leafIcon = null;
       
        private TreeCellRenderer delegate;
        
        public DelegatingRenderer() {
            initIcons(new DefaultTreeCellRenderer());
        }

        /**
         * initially sets the icons to the defaults as given
         * by a DefaultTreeCellRenderer.
         * 
         * @param renderer
         */
        private void initIcons(DefaultTreeCellRenderer renderer) {
            closedIcon = renderer.getDefaultClosedIcon();
            openIcon = renderer.getDefaultOpenIcon();
            leafIcon = renderer.getDefaultLeafIcon();
        }

        /**
         * Set the delegate renderer. 
         * Updates the folder/leaf icons. 
         * 
         * THINK: how to update? always override with this.icons, only
         * if renderer's icons are null, update this icons if they are not,
         * update all if only one is != null.... ??
         * 
         * @param delegate
         */
        public void setDelegateRenderer(TreeCellRenderer delegate) {
            if (delegate == null) {
                delegate = new DefaultTreeCellRenderer();
            }
            this.delegate = delegate;
            updateIcons();
        }
        
        /**
         * tries to set the renderers icons. Can succeed only if the
         * delegate is a DefaultTreeCellRenderer.
         * THINK: how to update? always override with this.icons, only
         * if renderer's icons are null, update this icons if they are not,
         * update all if only one is != null.... ??
         * 
         */
        private void updateIcons() {
            if (!isOverwriteRendererIcons()) return;
            setClosedIcon(closedIcon);
            setOpenIcon(openIcon);
            setLeafIcon(leafIcon);
        }

        public void setClosedIcon(Icon closedIcon) {
            if (delegate instanceof DefaultTreeCellRenderer) {
                ((DefaultTreeCellRenderer) delegate).setClosedIcon(closedIcon);
            }
            this.closedIcon = closedIcon;
        }
        
        public void setOpenIcon(Icon openIcon) {
            if (delegate instanceof DefaultTreeCellRenderer) {
                ((DefaultTreeCellRenderer) delegate).setOpenIcon(openIcon);
            }
            this.openIcon = openIcon;
        }
        
        public void setLeafIcon(Icon leafIcon) {
            if (delegate instanceof DefaultTreeCellRenderer) {
                ((DefaultTreeCellRenderer) delegate).setLeafIcon(leafIcon);
            }
            this.leafIcon = leafIcon;
        }
        
        public TreeCellRenderer getDelegateRenderer() {
            return delegate;
        }
            public Component getTreeCellRendererComponent(JTree tree, Object value, 
                    boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component result = delegate.getTreeCellRendererComponent(tree, value, 
                        selected, expanded, leaf, row, hasFocus);

                    if (highlighters != null) {
                        getComponentAdapter().row = row;
                        result = highlighters.apply(result, getComponentAdapter());
                    }

                 return result;
            }
    }

    
    protected ComponentAdapter getComponentAdapter() {
        return dataAdapter;
    }

    private final ComponentAdapter dataAdapter = new TreeAdapter(this);

    protected static class TreeAdapter extends ComponentAdapter {
        private final JXTree tree;

        /**
         * Constructs a <code>TableCellRenderContext</code> for the specified
         * target component.
         *
         * @param component the target component
         */
        public TreeAdapter(JXTree component) {
            super(component);
            tree = component;
        }
        public JXTree getTree() {
            return tree;
        }

        public boolean hasFocus() {
            return tree.isFocusOwner() && (tree.getLeadSelectionRow() == row);
        }

        public Object getValueAt(int row, int column) {
            TreePath path = tree.getPathForRow(row);
            return path.getLastPathComponent();
        }

        public Object getFilteredValueAt(int row, int column) {
            /** @todo Implement filtering */
            return getValueAt(row, column);
        }

        public boolean isSelected() {
            return tree.isRowSelected(row);
        }

        public boolean isExpanded() {
            return tree.isExpanded(row);
        }

        public boolean isLeaf() {
            return tree.getModel().isLeaf(getValue());
        }

        public boolean isCellEditable(int row, int column) {
            return false;	/** @todo  */
        }

        public void setValueAt(Object aValue, int row, int column) {
            /** @todo  */
        }
        
        public String getColumnName(int columnIndex) {
            return "Column_" + columnIndex;
        }
        
        public String getColumnIdentifier(int columnIndex) {
            return null;
        }
    }

}
