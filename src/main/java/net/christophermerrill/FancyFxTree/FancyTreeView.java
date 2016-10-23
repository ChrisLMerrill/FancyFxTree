package net.christophermerrill.FancyFxTree;

import javafx.collections.*;
import javafx.scene.control.*;
import net.christophermerrill.FancyFxTree.example.*;

import java.util.*;

/**
 * A tree with lots of fancy features already implemented...so you don't have to. It is intended to help
 * implement a tree on a complex data model without forcing the model to comply with the expectations
 * of TreeView. This includes asynchronous state changes and complex user interactions.
 *
 * Fancy features:
 * - update view when the model node properties change (not just when the entire model node is replaced). This requires the model adapter to notify the FancyTreeItemFacade when these changes happen.
 * - update view from asynchronous events (as above)
 * - smart scroll-to-item behavior
 * - expand tree to make an item visible
 * - cut/copy/paste keystroke support implemented, including common OS key combinations (Windows)
 * - drag and drop support implemented, including tree-aware drop targets (before, onto, after) and auto-expansion of tree nodes while hovering
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class FancyTreeView<T extends FancyTreeNodeFacade> extends TreeView
    {
    @SuppressWarnings("WeakerAccess") // part of public API
    public FancyTreeView(FancyTreeOperationHandler ops_handler)
        {
        _ops_handler = ops_handler;
        new FancyTreeKeyHandler(this, _ops_handler);
        setCellFactory(p -> new FancyTreeCell(_ops_handler));
        setSkin(new FancyTreeViewSkin(this));
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }

    @SuppressWarnings("WeakerAccess") // part of public API
    public void expandAll()
        {
        TreeItem<FancyTreeNodeFacade> root = getRoot();
        expandNodeAndChilren(root);
        }

    private void expandNodeAndChilren(TreeItem<FancyTreeNodeFacade> root)
        {
        root.setExpanded(true);
        root.getChildren().forEach(this::expandNodeAndChilren);
        }

    @SuppressWarnings("WeakerAccess") // part of public API
    public boolean expandToMakeVisible(Object node)
        {
        TreeItem<T> item = findItemForModelNode(node);
        if (item == null)
            return false;
        item.setExpanded(true);
        return true;
        }

    private TreeItem<T> findItemForModelNode(Object node)
        {
        return findItemForModelNode(getRoot(), node);
        }

    private TreeItem<T> findItemForModelNode(TreeItem<T> item, Object node)
        {
        if (item.getValue().getModelNode() == node)
            return item;
        for (TreeItem<T> child : item.getChildren())
            {
            TreeItem<T> found = findItemForModelNode(child, node);
            if (found != null)
                return found;
            }
        return null;
        }

    @SuppressWarnings("WeakerAccess") // part of public API
    public int findIndexOfVisibleItem(TreeItem target_item)
        {
        int index = 0;
        TreeItem<FancyTreeNodeFacade> item = getTreeItem(index);
        while (item != null)
            {
            if (item == target_item)
                return index;
            index++;
            item = getTreeItem(index);
            }

        return -1;
        }

    @SuppressWarnings("WeakerAccess") // part of public API
    public void scrollToAndMakeVisible(Object last_node)
        {
        TreeItem item = findItemForModelNode(last_node);
        expandNodeAndChilren(item); // do this first - can't scoll to an item if it is hidden (any ancestor is not expanded)

        int index = findIndexOfVisibleItem(item);
        if (!((FancyTreeViewSkin)getSkin()).isIndexVisible(index))  // don't scroll if it is already visible on screen
            scrollTo(index);
        }

    @SuppressWarnings("WeakerAccess") // part of public API
    public List<List<Integer>> getSelectionPaths()
        {
        ObservableList<TreeItem> selected_items = getSelectionModel().getSelectedItems();
        List<List<Integer>> paths = new ArrayList<>();
        for (TreeItem item : selected_items)
            {
            List<Integer> path = new ArrayList<>();
            createPathToItem(path, item);
            paths.add(path);
            }
        return paths;
        }

    private void createPathToItem(List<Integer> path, TreeItem item)
        {
        while (item.getParent() != null)
            {
            TreeItem parent = item.getParent();
            path.add(0, parent.getChildren().indexOf(item));
            item = parent;
            }
        }

    public void setRoot(ExampleTreeNodeFacade root_facade)
        {
        setRoot(FancyTreeItemBuilder.create(root_facade));
        }

    private final FancyTreeOperationHandler _ops_handler;
    }


