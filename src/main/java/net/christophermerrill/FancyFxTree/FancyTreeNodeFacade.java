package net.christophermerrill.FancyFxTree;

import javafx.scene.*;

import java.util.*;

/**
 * The tree-facing API of the tree data model. Allows for a complex data structure that is not
 * constrained by the expectations of the TreeView or TreeItem.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface FancyTreeNodeFacade<T>
    {
    /**
     * Due to a design flaw in the TreeView, the only way to force an update to a specific tree node is
     * to replace it. The FancyTreeNodeFacade allows this to happen without changing the underlying
     * datamodel. This method is necessary to accomplish that. Implementers should make a copy of this
     * object, including registering/deregistering any listeners on the underlying data model.
     */
    FancyTreeNodeFacade<T> copyAndDestroy();

    /**
     * This node facade will no longer be used. Implementers should deregister listeners and should
     * no longer make calls to the item facade.
     */
    void destroy();

    List<FancyTreeNodeFacade<T>> getChildren();
    Node getCustomCellUI();
    String getLabelText();
    T getModelNode();
    List<String> getStyles();

    /**
     * Start editing the cell. Return a FancyTreeCellEditor if a custom editor is needed.
     * Return null for the default editor (text field)
     */
    void editStarting();
    FancyTreeCellEditor getCustomEditorUI();
    void editFinished(); // called when the edit is done

    /**
     * Return an icon for the tree item or null if none.
     */
    Node getIcon();

    /**
     * Sets the correspnding FancyTreeItemFacade for this tree node. This is needed for adding and
     * removing tree nodes dynamically and refreshing the display due to other changes to the node.
     */
    void setTreeItemFacade(FancyTreeItemFacade item_facade);

    /**
     * Called by the default FancyTreeCellEditor (TextCellEditor) when the node text has changed.
     * May or may not be used by a custom FancyTreeCellEditor.
     */
    void setLabelText(String new_value);
    }

