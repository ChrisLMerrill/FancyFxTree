package net.christophermerrill.FancyFxTree.example;

import net.christophermerrill.FancyFxTree.*;

import java.util.*;

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
        _model.addChangeListener(new ExampleDataNode.ChangeListener()
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
            });
        }

    @Override
    public List<FancyTreeNodeFacade<ExampleDataNode>> getChildren()
        {
        List<FancyTreeNodeFacade<ExampleDataNode>> children = new ArrayList<>();
        for (ExampleDataNode child : _model.getChildren())
            children.add(new ExampleTreeNodeFacade(child));
        return children;
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

    private ExampleDataNode _model;
    private FancyTreeItemFacade _item_facade;
    }


