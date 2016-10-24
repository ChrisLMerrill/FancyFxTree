package net.christophermerrill.FancyFxTree.example;

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
    public boolean handleDelete(ObservableList selected_items)
        {
        _delete = true;
        _selected_items = selected_items;
        return true;
        }

    @Override
    public boolean handleCut(ObservableList selected_items)
        {
        _cut = true;
        _selected_items = selected_items;
        return true;
        }

    @Override
    public boolean handleCopy(ObservableList selected_items)
        {
        _copy = true;
        _selected_items = selected_items;
        return true;
        }

    @Override
    public boolean handlePaste(ObservableList selected_items)
        {
        _paste = true;
        _selected_items = selected_items;
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
        if (!_allow_drag_onto)
            info._drop_locations = new DropLocation[0];
        return info;
        }

    @Override
    public boolean finishDrag(TransferMode transfer_mode, Dragboard dragboard, FancyTreeNodeFacade item)
        {
        if (dragboard.getContent(LIST_OF_NODES) != null)
            {
            _dropped_nodes = (List<ExampleDataNode>) dragboard.getContent(LIST_OF_NODES);
            for (ExampleDataNode node : _dropped_nodes)
                {
                ExampleDataNode node_to_drop;
                if (transfer_mode.equals(TransferMode.COPY))
                    node_to_drop = ExampleDataNode.deepCopy(node, true);
                else
                    {
                    node_to_drop = node;
                    ExampleDataNode parent = _root.findParentFor(node);
                    parent.removeChild(node_to_drop);
                    }
                ((ExampleDataNode) item.getModelNode()).addChild(node_to_drop);
                }
            return true;
            }

        return false;
        }

    public ObservableList _selected_items;
    public boolean _delete = false;
    public boolean _cut = false;
    public boolean _copy = false;
    public boolean _paste = false;

    public boolean _allow_drag_onto = true;
    public int _drag_count;
    public ObservableList<TreeItem<ExampleTreeNodeFacade>> _dragged_items;
    public List<ExampleDataNode> _dropped_nodes;
    public Object _dropped_content;

    private ExampleDataNode _root;

    private final static DataFormat LIST_OF_NODES = new DataFormat("application/x-ListOfExampleDataNodes");
    }


