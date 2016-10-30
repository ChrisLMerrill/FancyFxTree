package net.christophermerrill.FancyFxTree.example;

import javafx.application.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import net.christophermerrill.FancyFxTree.*;

import java.util.*;

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
    public boolean handleDeleteKeystroke(ObservableList<TreeItem<ExampleTreeNodeFacade>> selected_items)
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
    public boolean handleCutKeystroke(ObservableList<TreeItem<ExampleTreeNodeFacade>> selected_items)
        {
        _cut = true;
        _copy = false;
        _cut_or_copied_nodes = captureSelection(selected_items);
        return true;
        }

    @Override
    public boolean handleCopyKeystroke(ObservableList<TreeItem<ExampleTreeNodeFacade>> selected_items)
        {
        _copy = true;
        _cut = false;
        _cut_or_copied_nodes = captureSelection(selected_items);
        return true;
        }

    private List<ExampleDataNode> captureSelection(ObservableList<TreeItem<ExampleTreeNodeFacade>> selected_items)
        {
        List<ExampleDataNode> selected_nodes = new ArrayList<>();
        for (TreeItem<ExampleTreeNodeFacade> item : selected_items)
            if (item != null && item.getValue() != null)
                selected_nodes.add(item.getValue().getModelNode());
        return selected_nodes;
        }

    @Override
    public boolean handlePasteKeystroke(ObservableList<TreeItem<ExampleTreeNodeFacade>> selected_items)
        {
        ExampleDataNode target = selected_items.get(0).getValue().getModelNode();
        ExampleDataNode parent = _root.findParentFor(target);

        for (ExampleDataNode selected_node : _cut_or_copied_nodes)
            {
            ExampleDataNode node_to_paste;
            if (_copy)
                node_to_paste = ExampleDataNode.deepCopy(selected_node, true);
            else if (_cut)
                {
                ExampleDataNode parent_to_cut_from = _root.findParentFor(selected_node);
                parent_to_cut_from.removeChild(selected_node);
                node_to_paste = selected_node;
                }
            else
                {
                System.out.println("Expected either copy or paste. Neither...fail!");
                continue;
                }
            parent.addAfter(node_to_paste, target);
            }

        _copy = false;
        _cut = false;
        _selected_nodes = Collections.emptyList();
        return true;
        }

    @Override
    public StartDragInfo startDrag(List<List<Integer>> selection_paths, ObservableList<TreeItem<ExampleTreeNodeFacade>> selected_items)
        {
        StartDragInfo info = new StartDragInfo();
        _dragged_items = selected_items;
        List<ExampleDataNode> selections = new ArrayList<>();
        for (TreeItem<ExampleTreeNodeFacade> item : selected_items)
            selections.add(item.getValue().getModelNode());
        info.addContent(LIST_OF_NODES, selections);
        _drag_count = selected_items.size();
        return info;
        }

    @Override
    public DragOverInfo dragOver(Dragboard dragboard)
        {
        DragOverInfo info = new DragOverInfo();
        info.addAllModesAndLocations();
        return info;
        }

    @Override
    public boolean finishDrag(TransferMode transfer_mode, Dragboard dragboard, FancyTreeNodeFacade item, DropLocation location)
        {
        if (dragboard.getContent(LIST_OF_NODES) != null)
            {
            _dropped_nodes = (List<ExampleDataNode>) dragboard.getContent(LIST_OF_NODES);
            ExampleDataNode parent;
            int add_index;
            switch (location)
                {
                case BEFORE:
                    parent = _root.findParentFor((ExampleDataNode) item.getModelNode());
                    add_index = parent.getChildren().indexOf(item.getModelNode());
                    break;
                case ON:
                    parent = (ExampleDataNode) item.getModelNode();
                    add_index = ((ExampleDataNode) item.getModelNode()).getChildren().size();
                    break;
                case AFTER:
                    parent = _root.findParentFor((ExampleDataNode) item.getModelNode());
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

    private List<ExampleDataNode> _selected_nodes = Collections.emptyList();
    private List<ExampleDataNode> _cut_or_copied_nodes = Collections.emptyList();
    private boolean _cut = false;
    private boolean _copy = false;

    public int _drag_count;
    public ObservableList<TreeItem<ExampleTreeNodeFacade>> _dragged_items;
    public List<ExampleDataNode> _dropped_nodes;
    public Object _dropped_content;

    private ExampleDataNode _root;

    private final static DataFormat LIST_OF_NODES = new DataFormat("application/x-ListOfExampleDataNodes");
    }


