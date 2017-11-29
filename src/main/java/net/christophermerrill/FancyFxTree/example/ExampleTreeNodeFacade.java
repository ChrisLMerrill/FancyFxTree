package net.christophermerrill.FancyFxTree.example;

import javafx.scene.Node;
import net.christophermerrill.FancyFxTree.*;

import java.util.*;
import java.util.stream.*;

/**
 * The implementation of FancyTreeNodeFacade is intended to wrap your existing
 * hierarchical data model to provide the API that FancyTreeView needs to model
 * and render your data in the tree.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ExampleTreeNodeFacade implements FancyTreeNodeFacade<ExampleDataNode>
    {
    public ExampleTreeNodeFacade(ExampleDataNode model)
        {
        _model = model;
        _model.addChangeListener(_listener);
        _children.addAll(_model.getChildren().stream().map(ExampleTreeNodeFacade::new).collect(Collectors.toList()));
        }

    private ExampleTreeNodeFacade(ExampleDataNode model, FancyTreeItemFacade facade, List<FancyTreeNodeFacade<ExampleDataNode>> children)
        {
        _model = model;
        _item_facade = facade;
        _children = children;
        }

    @Override
    public List<FancyTreeNodeFacade<ExampleDataNode>> getChildren()
        {
        return _children;
        }

    @Override
    public String getLabelText()
        {
        if (_model.getExtraData() == null)
            return _model.getName();
        else
            return String.format("%s (%s)", _model.getName(), _model.getExtraData());
        }

    @Override
    public Node getIcon()
        {
        return null;
        }

    @Override
    public Node getCustomCellUI()
        {
        return null;
        }

    @Override
    public FancyTreeCellEditor getCustomEditorUI()
	    {
	    if (_model._use_custom_editor)
		    return new ExampleCustomCellEditor();
	    return null;
	    }

    @Override
    public void editStarting()
	    {
	    _is_editing = true;
	    }

    @Override
    public void editFinished()
	    {
	    _is_editing = false;
	    }

    @Override
    public ExampleDataNode getModelNode()
        {
        return _model;
        }

    @Override
    public void setLabelText(String new_value)
	    {
	    _model.setName(new_value);
	    }

    @Override
    public void setTreeItemFacade(FancyTreeItemFacade item_facade)
        {
        _item_facade = item_facade;
        }

    @Override
    public FancyTreeNodeFacade<ExampleDataNode> copyAndDestroy()
        {
        synchronized (_model)
            {
            ExampleTreeNodeFacade copy = new ExampleTreeNodeFacade(_model, _item_facade, _children);
            _model.replaceChangeListener(_listener, copy._listener);
            return copy;
            }
        }

    @Override
    public void destroy()
        {
        _model.removeChangeListener(_listener);
        _item_facade = null;
        }

    @Override
    public List<String> getStyles()
	    {
	    return _model.getStyles();
	    }

    private ExampleDataNode.ChangeListener _listener = new ExampleDataNode.ChangeListener()
        {
        @Override
        public void childAdded(ExampleDataNode child, int index)
            {
            if (_item_facade == null)
                System.out.println("why don't I have a facade?");
            ExampleTreeNodeFacade child_facade = new ExampleTreeNodeFacade(child);
            _children.add(index, child_facade);
            _item_facade.addChild(child_facade, index);
            }

        @Override
        public void childRemoved(ExampleDataNode child, int index)
            {
            _item_facade.removeChild(index, new ExampleTreeNodeFacade(child));
            _children.remove(index);
            }

        @Override
        public void propertyChanged()
            {
            if (_item_facade != null && !_is_editing)
                _item_facade.refreshDisplay();
            }
        };

    @Override
    public String toString()
        {
        return _model.getName();
        }

    private ExampleDataNode _model;
    private FancyTreeItemFacade _item_facade;
    private List<FancyTreeNodeFacade<ExampleDataNode>> _children = new ArrayList<>();
    private boolean _is_editing = false;
    }


