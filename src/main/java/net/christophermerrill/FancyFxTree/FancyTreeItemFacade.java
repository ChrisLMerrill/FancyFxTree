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
    public FancyTreeItemFacade(TreeItem<FancyTreeItemValueHolder> item)
        {
        _item = item;
        }

    /**
     * Re-render the node. Should be called when non-structural changes require a change to the visual presentation.
     */
    public void refreshDisplay()
        {
        _item.setValue(new FancyTreeItemValueHolder(_item.getValue()));
        }

    public void addChild(FancyTreeNodeFacade child, int index)
        {
        FancyTreeItemBuilder.addChild(_item, child, index);
        }

    public void removeChild(FancyTreeNodeFacade child, int index)
        {
        try
            {
            FancyTreeItemValueHolder holder = _item.getChildren().get(index).getValue();
            if (holder.getValue().getModelNode() == child.getModelNode())
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

    private final TreeItem<FancyTreeItemValueHolder> _item;
    }

