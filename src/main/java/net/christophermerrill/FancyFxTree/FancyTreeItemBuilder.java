package net.christophermerrill.FancyFxTree;

import javafx.scene.control.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class FancyTreeItemBuilder
    {
    public static TreeItem<FancyTreeNodeFacade> create(FancyTreeNodeFacade root)
        {
        TreeItem<FancyTreeNodeFacade> root_item = new TreeItem<>(root);
        root.setTreeItemFacade(new FancyTreeItemFacade(root_item));

        addChildren(root_item, root);
        return root_item;
        }

    public static void addChildren(TreeItem item, FancyTreeNodeFacade node)
        {
        for (Object child_node : node.getChildren())
            addChild(item, (FancyTreeNodeFacade) child_node);
        }

    public static void addChild(TreeItem item, FancyTreeNodeFacade child_node)
        {
        addChild(item, child_node, item.getChildren().size());
        }

    public static void addChild(TreeItem item, FancyTreeNodeFacade child_node, int index)
        {
        TreeItem child_item = new TreeItem<>(child_node);
        item.getChildren().add(index, child_item);
        addChildren(child_item, child_node);

        child_node.setTreeItemFacade(new FancyTreeItemFacade(child_item));
        }
    }


