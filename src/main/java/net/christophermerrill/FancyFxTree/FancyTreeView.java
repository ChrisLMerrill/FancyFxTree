package net.christophermerrill.FancyFxTree;

import javafx.application.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.input.*;

import java.util.*;

/**
 * A tree with lots of fancy features already implemented...so you don't have to. It is intended to help
 * implement a tree on a complex hierarchical data model without forcing the model to comply with the expectations
 * of TreeView. This includes asynchronous state changes and complex user interactions, such as drag-and-drop.
 *
 * Fancy features include:
 * - update view when the model node properties change (not just when the entire model node is replaced). This requires the custom FancyTreeNode extension to notify the FancyTreeItemFacade when these changes happen.
 * - update view from asynchronous events (as above)
 * - smart scroll-to-item behavior
 * - expand tree to make an item visible
 * - cut/copy/paste keystroke support implemented, including common OS key combinations (Windows)
 * - drag and drop support implemented, including tree-aware drop targets (drop before, into, or after)
 * - hover cursor over a tree item to expand it during drag
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@SuppressWarnings("ALL")
public class FancyTreeView<T extends FancyTreeNodeFacade> extends TreeView
    {
    @SuppressWarnings("WeakerAccess") // part of public API
    public FancyTreeView(FancyTreeOperationHandler ops_handler)
        {
        this(ops_handler, true);
        }

    /**
     * @param enable_dnd False to disable drag-and-drop support.
     */
    @SuppressWarnings("unused,WeakerAccess")  // public API
    public FancyTreeView(FancyTreeOperationHandler ops_handler, boolean enable_dnd)
        {
        _ops_handler = ops_handler;
        _enable_dnd = enable_dnd;
        new FancyTreeKeyHandler(this, _ops_handler);
        setCellFactory(param ->
	        {
	        FancyTreeCell cell = new FancyTreeCell(_ops_handler, _enable_dnd);
	        cell.setHoverExpandDuration(_hover_expand_duration);

	        cell.addEventFilter(MouseEvent.MOUSE_PRESSED, (MouseEvent e) ->
		        {
		        if (e.getClickCount() == 2 && e.getButton().equals(MouseButton.PRIMARY))
			        {
			        e.consume();
                    _ops_handler.handleDoubleClick(cell, e.isControlDown(), e.isShiftDown(), e.isAltDown());
//                    Platform.runLater(() -> _ops_handler.handleDoubleClick(cell, e.isControlDown(), e.isShiftDown(), e.isAltDown()));
			        }
		        });

	        return cell;
	        });
        setSkin(new FancyTreeViewSkin(this));
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
	        _ops_handler.selectionChanged(getSelectionModel().getSelectedItems()));

        addEventFilter(MouseEvent.MOUSE_RELEASED, e->
            {
            if (_context_menu != null)
                {
                _context_menu.hide();
                _context_menu = null;
                }
            if (e.getButton() == MouseButton.SECONDARY)
                {
                ObservableList selections = getSelectionModel().getSelectedItems();
                _context_menu = _ops_handler.getContextMenu(selections);
                if (_context_menu != null)
                    _context_menu.show(this, e.getScreenX(), e.getScreenY());
                }
            });
        }

    @SuppressWarnings("WeakerAccess") // part of public API
    public void expandAll()
        {
        TreeItem<FancyTreeNodeFacade> root = getRoot();
        expandNodeAndChilren(root);
        }

    private void expandNodeAndChilren(TreeItem<FancyTreeNodeFacade> node)
        {
        node.setExpanded(true);
        node.getChildren().forEach(this::expandNodeAndChilren);
        }

    public void collapseAll()
        {
        TreeItem<FancyTreeNodeFacade> root = getRoot();
        collapseNodeAndChildren(root);
        }

    private void collapseNodeAndChildren(TreeItem<FancyTreeNodeFacade> node)
        {
        node.getChildren().forEach(this::collapseNodeAndChildren);
        node.setExpanded(false);
        }

    @SuppressWarnings("WeakerAccess") // part of public API
    public List<TreeItem<T>> expandToMakeVisible(Object node)
        {
        TreeItem<T> item = findItemForModelNode(node);
        if (item == null)
            return Collections.emptyList();

        List<TreeItem<T>> expanded = new ArrayList<>();
        item = item.getParent();
        while (item != null)
            {
            if (!item.isExpanded())
	            {
	            item.setExpanded(true);
	            expanded.add(item);
	            }
            item = item.getParent();
            }
        return expanded;
        }

    @SuppressWarnings({"unused", "WeakerAccess"})  // public API
    public List<TreeItem<T>> expandAndScrollTo(Object node)
        {
        List<TreeItem<T>> expanded = expandToMakeVisible(node);
        scrollToVisibleItem(node);
        return expanded;
        }

    @SuppressWarnings({"unused", "UnusedReturnValue"})  // public API
    public List<TreeItem<T>> expandScrollToAndSelect(Object node)
        {
        TreeItem item = findItemForModelNode(node);
        if (item == null)
            return Collections.emptyList();

        List<TreeItem<T>> expanded = expandToMakeVisible(node);
        scrollToVisibleItem(node);

        getSelectionModel().select(item);
        return expanded;
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
    public void scrollToAndMakeVisible(Object node)
        {
        TreeItem item = findItemForModelNode(node);
        if (item == null)
            return;
        expandToMakeVisible(item); // do this first - can't scoll to an item if it is hidden (any ancestor is not expanded)

        int index = findIndexOfVisibleItem(item);
        if (!((FancyTreeViewSkin)getSkin()).isIndexVisible(index))  // don't scroll if it is already visible on screen
            Platform.runLater(() -> scrollTo(index));
        }

    @SuppressWarnings("unused,WeakerAccess")  // public API
    public void scrollToVisibleItem(Object node)
        {
        TreeItem item = findItemForModelNode(node);
        if (item == null)
            return;

        int index = findIndexOfVisibleItem(item);
        if (!((FancyTreeViewSkin)getSkin()).isIndexVisible(index))  // don't scroll if it is already visible on screen
            Platform.runLater(() -> scrollTo(index));
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

    public void setRoot(FancyTreeNodeFacade root_facade)
        {
        setRoot(FancyTreeItemBuilder.create(root_facade));
        }

    @SuppressWarnings("WeakerAccess")  // part of public API
    public void setHoverExpandDuration(long hover_expand_duration)
        {
        _hover_expand_duration = hover_expand_duration;
        }



    private final FancyTreeOperationHandler _ops_handler;
    private final boolean _enable_dnd;
    private ContextMenu _context_menu = null;

    private long _hover_expand_duration = DEFAULT_HOVER_EXPAND_DURATION;
    static final long DEFAULT_HOVER_EXPAND_DURATION = 2000;
    }