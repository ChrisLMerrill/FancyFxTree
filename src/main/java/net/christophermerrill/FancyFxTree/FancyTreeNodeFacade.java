package net.christophermerrill.FancyFxTree;

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
    List<FancyTreeNodeFacade<T>> getChildren();
    String getLabelText();
    T getModelNode();

    void setTreeItemFacade(FancyTreeItemFacade item_facade);
    }

