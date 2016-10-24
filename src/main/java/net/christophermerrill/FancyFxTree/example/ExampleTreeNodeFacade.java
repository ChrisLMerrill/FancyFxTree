package net.christophermerrill.FancyFxTree.example;

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
        }

    private ExampleTreeNodeFacade(ExampleDataNode model, FancyTreeItemFacade facade)
        {
        _model = model;
        _item_facade = facade;
        }

    @Override
    public List<FancyTreeNodeFacade<ExampleDataNode>> getChildren()
        {
        return _model.getChildren().stream().map(ExampleTreeNodeFacade::new).collect(Collectors.toList());
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
    public ExampleDataNode getModelNode()
        {
        return _model;
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
            ExampleTreeNodeFacade copy = new ExampleTreeNodeFacade(_model, _item_facade);
            _model.replaceChangeListener(_listener, copy._listener);
            return copy;
            }
        }

    private ExampleDataNode.ChangeListener _listener = new ExampleDataNode.ChangeListener()
        {
        @Override
        public void childAdded(ExampleDataNode child, int index)
            {
            _item_facade.addChild(new ExampleTreeNodeFacade(child), index);
            }

        @Override
        public void childRemoved(ExampleDataNode child, int index)
            {
            _item_facade.removeChild(new ExampleTreeNodeFacade(child), index);
            }

        @Override
        public void propertyChanged()
            {
            if (_item_facade != null)
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
    }


