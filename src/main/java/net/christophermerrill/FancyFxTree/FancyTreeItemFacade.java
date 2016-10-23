package net.christophermerrill.FancyFxTree;

import javafx.scene.control.*;

/**
 * The model-facing API to a tree item. Allows the model to notify the tree of asynchronous changes
 * that require updates in the tree.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class FancyTreeItemFacade
    {
    @SuppressWarnings("WeakerAccess")  // part of the public API
    public FancyTreeItemFacade(TreeItem<FancyTreeNodeFacade> item)
        {
        _item = item;
        }

    /**
     * Re-render the node. Should be called when non-structural changes require a change to the visual presentation.
     */
    public void refreshDisplay()
        {
        _item.setValue(_item.getValue().copyAndDestroy());
        }

    public void addChild(FancyTreeNodeFacade child, int index)
        {
        FancyTreeItemBuilder.addChild(_item, child, index);
        }

    public void removeChild(FancyTreeNodeFacade child, int index)
        {
        try
            {
            FancyTreeNodeFacade node = _item.getChildren().get(index).getValue();
            if (node.getModelNode().equals(child.getModelNode()))
                _item.getChildren().remove(index);
            else
                throw new IllegalArgumentException(String.format("The indexed sub-item (%d) didn't match the node selected for removal: %s", index, child.getModelNode().toString()));
            }
        catch (Exception e)
            {
         // index doesn't exist
            throw new IllegalArgumentException(String.format("Unable to locate the indexed sub-item (%d) for removal: %s", index, child.getModelNode().toString()));
            }
        }

    private final TreeItem<FancyTreeNodeFacade> _item;
    }

