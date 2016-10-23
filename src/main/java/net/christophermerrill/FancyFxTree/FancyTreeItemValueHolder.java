package net.christophermerrill.FancyFxTree;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class FancyTreeItemValueHolder<T extends FancyTreeNodeFacade>
    {
    FancyTreeItemValueHolder(T value)
        {
        _value = value;
        }

    FancyTreeItemValueHolder(FancyTreeItemValueHolder<T> replaced)
        {
        _value = replaced.getValue();
        }

    public T getValue()
        {
        return _value;
        }

    @Override
    public String toString()
        {
        return _value.getLabelText();
        }

    private T _value;
    }


