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
    List<FancyTreeNodeFacade<T>> getChildren();
    String getLabelText();
    T getModelNode();

    void setTreeItemFacade(FancyTreeItemFacade item_facade);
    }

