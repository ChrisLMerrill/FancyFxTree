package net.christophermerrill.FancyFxTree.example;

import javafx.application.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import net.christophermerrill.FancyFxTree.*;

import java.util.*;
import java.util.stream.*;

import static net.christophermerrill.FancyFxTree.FancyTreeOperationHandler.EditType.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ExampleOperationHandler extends FancyTreeOperationHandler<ExampleTreeNodeFacade>
    {
    public ExampleOperationHandler(ExampleDataNode root)
        {
        _root = root;
        }

    @Override
    public boolean handleDelete(ObservableList<TreeItem<ExampleTreeNodeFacade>> selected_items)
        {
        for (TreeItem<ExampleTreeNodeFacade> item : selected_items)
            {
            ExampleDataNode node_to_remove = item.getValue().getModelNode();
            ExampleDataNode parent = _root.findParentFor(node_to_remove);
            if (parent != null)
                parent.removeChild(node_to_remove);
            }
        return true;
        }

    @Override
    public boolean handleCut(ObservableList<TreeItem<ExampleTreeNodeFacade>> selected_items)
        {
        _cut = true;
        _copy = false;
        _cut_or_copied_nodes = captureSelection(selected_items);
        return true;
        }

    @Override
    public boolean handleCopy(ObservableList<TreeItem<ExampleTreeNodeFacade>> selected_items)
        {
        _copy = true;
        _cut = false;
        _cut_or_copied_nodes = captureSelection(selected_items);
        return true;
        }

    private List<ExampleDataNode> captureSelection(ObservableList<TreeItem<ExampleTreeNodeFacade>> selected_items)
        {
        return selected_items.stream().filter(item -> item != null && item.getValue() != null).map(item -> item.getValue().getModelNode()).collect(Collectors.toList());
        }

    @Override
    public boolean handlePaste(ObservableList<TreeItem<ExampleTreeNodeFacade>> selected_items)
        {
        if (!_copy && !_cut)
            {
            System.out.println("Expected either copy or cut. Neither...fail!");
            return false;
            }

        ExampleDataNode target = selected_items.get(0).getValue().getModelNode();
        ExampleDataNode parent = _root.findParentFor(target);

        for (ExampleDataNode selected_node : _cut_or_copied_nodes)
            {
            if (_copy)
                {
                ExampleDataNode node_to_paste = ExampleDataNode.deepCopy(selected_node, true);
                parent.addAfter(node_to_paste, target);
                }
            else if (_cut)
                {
                ExampleDataNode parent_to_cut_from = _root.findParentFor(selected_node);
                parent_to_cut_from.removeChild(selected_node);
                parent.addAfter(selected_node, target);
                }
            }

        _copy = false;
        _cut = false;
        _selected_nodes = Collections.emptyList();
        return true;
        }

    @Override
    public boolean handleUndo()
        {
        System.out.println("Undo is not implemented for this example");
        return false;
        }

    @Override
    public StartDragInfo startDrag(List<List<Integer>> selection_paths, ObservableList<TreeItem<ExampleTreeNodeFacade>> selected_items)
        {
        StartDragInfo info = new StartDragInfo();
        _dragged_items = selected_items;
        List<ExampleDataNode> selections = selected_items.stream().map(item -> item.getValue().getModelNode()).collect(Collectors.toList());
        info.addContent(LIST_OF_NODES, selections);
        _drag_count = selected_items.size();
        return info;
        }

    @Override
    public DragOverInfo dragOver(Dragboard dragboard, ExampleTreeNodeFacade onto_node)
	    {
        DragOverInfo info = new DragOverInfo();
	    if (dragboard.getContent(LIST_OF_NODES) != null)
		    {
		    _dropped_nodes = (List<ExampleDataNode>) dragboard.getContent(LIST_OF_NODES);
		    if (_dropped_nodes.contains(onto_node.getModelNode()))
		    	return info;
            for (ExampleDataNode to_be_dropped : _dropped_nodes)
                if (to_be_dropped.isAncestorOf(onto_node.getModelNode()))
                    return info;
		    }

        info.addAllModesAndLocations();
        return info;
        }

    @Override
    public boolean finishDrag(TransferMode transfer_mode, Dragboard dragboard, ExampleTreeNodeFacade item, DropLocation location)
        {
        if (dragboard.getContent(LIST_OF_NODES) != null)
            {
            _dropped_nodes = (List<ExampleDataNode>) dragboard.getContent(LIST_OF_NODES);
            if (_dropped_nodes.contains(item.getModelNode()))
            	return false;  // TODO indicate this?
            ExampleDataNode parent;
            int add_index;
            switch (location)
                {
                case BEFORE:
                    parent = _root.findParentFor(item.getModelNode());
                    add_index = parent.getChildren().indexOf(item.getModelNode());
                    break;
                case ON:
                    parent = item.getModelNode();
                    add_index = (item.getModelNode()).getChildren().size();
                    break;
                case AFTER:
                    parent = _root.findParentFor(item.getModelNode());
                    add_index = parent.getChildren().indexOf(item.getModelNode()) + 1;
                    break;
                default:
                    return false;
                }

            for (ExampleDataNode node : _dropped_nodes)
                {
                ExampleDataNode node_to_drop;
                if (transfer_mode.equals(TransferMode.COPY))
                    node_to_drop = ExampleDataNode.deepCopy(node, true);
                else
                    {
                    node_to_drop = node;
                    Platform.runLater(() -> _root.findParentFor(node).removeChild(node_to_drop));
                    }

                final int index_to_add_at = add_index;
                Platform.runLater(() -> parent.addChild(index_to_add_at, node_to_drop)); // updates to tree should be done on the UI thread when possible?
                add_index++;
                }
            return true;
            }

        return false;
        }

    public void selectionChanged(ObservableList<TreeItem<ExampleTreeNodeFacade>> selected_items)
        {
        _selected_nodes = captureSelection(selected_items);
        }

    public List<ExampleDataNode> getSelectedNodes()
        {
        return _selected_nodes;
        }

    @SuppressWarnings("unused")  // public API
    ExampleDataNode getSelectedNode()
        {
        if (_selected_nodes.size() == 1)
            return _selected_nodes.get(0);
        return null;
        }

    @Override
    public void handleDoubleClick(TreeCell<ExampleTreeNodeFacade> cell, boolean control_down, boolean shift_down, boolean alt_down)
	    {
        _double_clicked_node_name = cell.getTreeItem().getValue().getModelNode().getName();
        if (cell.getTreeView().isEditable() && !cell.isEditing())
            cell.startEdit();
	    }

    @Override
    public ContextMenu getContextMenu(ObservableList<TreeItem<ExampleTreeNodeFacade>> selected_items)
        {
        ContextMenu menu = new ContextMenu();
        menu.getItems().add(new MenuItem(MENU_ITEM_1));
        menu.getItems().add(new MenuItem(MENU_ITEM_2));
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().addAll(createEditMenuItems(selected_items, Cut, Copy, Paste, Delete));
        return menu;
        }

    public String getDoubleClickedNodeName()
        {
        return _double_clicked_node_name;
        }

    private ExampleDataNode _root;

    private List<ExampleDataNode> _selected_nodes = Collections.emptyList();
    public List<ExampleDataNode> _cut_or_copied_nodes = Collections.emptyList();
    private boolean _cut = false;
    private boolean _copy = false;

    public int _drag_count;
    public ObservableList<TreeItem<ExampleTreeNodeFacade>> _dragged_items;
    public List<ExampleDataNode> _dropped_nodes;
    public Object _dropped_content;

    private String _double_clicked_node_name;


    private final static DataFormat LIST_OF_NODES = new DataFormat("application/x-ListOfExampleDataNodes");

    public final static String MENU_ITEM_1 = "click me";
    private final static String MENU_ITEM_2 = "click me 2";
    }


