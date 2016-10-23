package net.christophermerrill.FancyFxTree.example;

import java.util.*;

/**
 * This example class represents your application data model. To fit your
 * data into the tree, implement FancyTreeNodeFacade to wrap these objects.

 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ExampleDataNode
    {
    public ExampleDataNode(String name, ExampleDataNode[] children)
        {
        _name = name;
        for (ExampleDataNode child : children)
            addChild(child);
        }

    public ExampleDataNode(String name)
        {
        _name = name;
        }

    public List<ExampleDataNode> getChildren()
        {
        return Collections.unmodifiableList(_children);
        }

    public void addChild(ExampleDataNode child)
        {
        _children.add(child);
        fireChildAdded(child, _children.size() - 1);
        }

    public void removeChild(ExampleDataNode child)
        {
        int index = _children.indexOf(child);
        if (_children.remove(child))
            fireChildRemoved(child, index);
        }

    public String getName()
        {
        return _name;
        }

    public void setName(String new_name)
        {
        _name = new_name;
        firePropertyChange();
        }

    public String getExtraData()
        {
        return _extra_data;
        }

    public void setExtraData(String extra_data)
        {
        _extra_data = extra_data;
        firePropertyChange();
        }

    public void insertChild(ExampleDataNode child, int index)
        {
        _children.add(index, child);
        fireChildAdded(child, index);
        }

    /**
     * Picks a random node in the hierarchy.
     */
    public ExampleDataNode pickRandom()
        {
        List<ExampleDataNode> all = toList();
        int random_index = new Random().nextInt(all.size());
        return all.get(random_index);
        }

    public List<ExampleDataNode> toList()
        {
        List<ExampleDataNode> all = new ArrayList<>();
        all.add(this);
        addDescendantsToList(all);
        return all;
        }

    private void addDescendantsToList(List<ExampleDataNode> list)
        {
        for (ExampleDataNode child : _children)
            {
            list.add(child);
            child.addDescendantsToList(list);
            }
        }

    public ExampleDataNode findParentFor(ExampleDataNode target)
        {
        if (_children.contains(target))
            return this;
        for (ExampleDataNode child : _children)
            {
            ExampleDataNode parent = child.findParentFor(target);
            if (parent != null)
                return parent;
            }
        return null;
        }

    public void addChildAfterChild(ExampleDataNode to_add, ExampleDataNode to_follow)
        {
        int index = _children.indexOf(to_follow) + 1;
        _children.add(index, to_add);
        fireChildAdded(to_add, index);
        }


    private List<ExampleDataNode> _children = new ArrayList<>();
    private String _name;
    private String _extra_data;

    private transient List<ChangeListener> _change_listeners;

    private void firePropertyChange()
        {
        if (_change_listeners != null)
            for (ChangeListener listener : _change_listeners)
                listener.propertyChanged();
        }

    private void fireChildAdded(ExampleDataNode node, int index)
        {
        if (_change_listeners != null)
            for (ChangeListener listener : _change_listeners)
                listener.childAdded(node, index);
        }

    private void fireChildRemoved(ExampleDataNode node, int index)
        {
        if (_change_listeners != null)
            for (ChangeListener listener : _change_listeners)
                listener.childRemoved(node, index);
        }

    public void addChangeListener(ChangeListener listener)
        {
        if (_change_listeners == null)
            _change_listeners = new ArrayList<>();
        if (!_change_listeners.contains(listener))
            _change_listeners.add(listener);
        }

    public interface ChangeListener
        {
        void childAdded(ExampleDataNode child, int index);
        void childRemoved(ExampleDataNode child, int index);
        void propertyChanged();
        }
    }


