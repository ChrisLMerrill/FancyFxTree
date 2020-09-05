package net.christophermerrill.FancyFxTree.example;

import java.io.*;
import java.util.*;

/**
 * This example class represents your application data model. To fit your
 * data into the tree, implement FancyTreeNodeFacade to wrap these objects.

 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ExampleDataNode implements Serializable
    {
    public ExampleDataNode(String name)
        {
        _name = name;
        }

    private ExampleDataNode(ExampleDataNode original)
        {
        _name = original.getName();
        }

    static ExampleDataNode deepCopy(ExampleDataNode original, boolean annotate_label)
        {
        ExampleDataNode copy = new ExampleDataNode(original);
        if (annotate_label)
            copy.setName(getCopyName(original));
        for (ExampleDataNode child : original.getChildren())
            copy.addChild(deepCopy(child, annotate_label));
        return copy;
        }

    public static String getCopyName(ExampleDataNode node)
        {
        return node.getName() + " (copy)";
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

    void addChild(int index, ExampleDataNode child)
        {
        _children.add(index, child);
        fireChildAdded(child, index);
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

    String getExtraData()
        {
        return _extra_data;
        }

    void setExtraData(String extra_data)
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
    ExampleDataNode pickRandom()
        {
        List<ExampleDataNode> all = toList();
        int random_index = new Random().nextInt(all.size());
        return all.get(random_index);
        }

    private List<ExampleDataNode> toList()
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

    boolean isAncestorOf(ExampleDataNode target)
        {
        if (_children.contains(target))
            return true;
        for (ExampleDataNode child : _children)
            if (child.isAncestorOf(target))
                return true;
        return false;
        }

    void addAfter(ExampleDataNode to_add, ExampleDataNode to_follow)
        {
        int index = _children.indexOf(to_follow) + 1;
        _children.add(index, to_add);
        fireChildAdded(to_add, index);
        }

    public ExampleDataNode getNodeByName(String name)
        {
        if (name.equals(_name))
            return this;
        for (ExampleDataNode child : _children)
            {
            ExampleDataNode found = child.getNodeByName(name);
            if (found != null)
                return found;
            }
        return null;
        }

    public boolean contains(ExampleDataNode target)
        {
        return contains(target, false);
        }

    public boolean contains(ExampleDataNode target, boolean direct_children_only)
        {
        if (equals(target))
            return true;
        for (ExampleDataNode child : _children)
            {
            if (child == target)
                return true;
            if (!direct_children_only && child.contains(target))
                return true;
            }
        return false;
        }

    private UUID getId()
        {
        return _id;
        }

    public void addStyle(String style)
	    {
	    _styles.add(style);
	    firePropertyChange();
	    }

    public void removeStyle(String style)
	    {
	    _styles.remove(style);
	    firePropertyChange();
	    }

    List<String> getStyles()
	    {
	    return _styles;
	    }

    @Override
    public boolean equals(Object obj)
        {
        return obj instanceof ExampleDataNode
            && ((ExampleDataNode)obj).getId().equals(_id);
        }

    @Override
    public String toString()
	    {
	    return _name;
	    }

    private List<ExampleDataNode> _children = new ArrayList<>();
    private String _name;
    private String _extra_data;
    private List<String> _styles = new ArrayList<>();
    private UUID _id = UUID.randomUUID();

    public boolean _use_custom_editor = false;

    private transient List<ChangeListener> _change_listeners;

    private void firePropertyChange()
        {
        if (_change_listeners != null)
            //noinspection Convert2streamapi   (causes a ConcurrentModificationException)
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

    synchronized void addChangeListener(ChangeListener listener)
        {
        if (_change_listeners == null)
            _change_listeners = new ArrayList<>();
        if (!_change_listeners.contains(listener))
            _change_listeners.add(listener);
        }

    synchronized void removeChangeListener(ChangeListener listener)
        {
        if (_change_listeners != null)
            _change_listeners.remove(listener);
        }

    synchronized void replaceChangeListener(ChangeListener old_listener, ChangeListener new_listener)
        {
        if (_change_listeners != null)
            {
            _change_listeners.remove(old_listener);
            _change_listeners.add(new_listener);
            }
        }

    interface ChangeListener
        {
        void childAdded(ExampleDataNode child, int index);
        void childRemoved(ExampleDataNode child, int index);
        void propertyChanged();
        }
    }


