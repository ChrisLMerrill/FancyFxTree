package net.christophermerrill.FancyFxTree.example;

import java.util.*;

/**
 * This class builds nodes to provide initial data for the example (and unit tests).
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ExampleDataNodeBuilder
    {
    public static ExampleDataNode create(int[] num_descendents)
        {
        ExampleDataNode root = new ExampleDataNode("1");
        addDecendents(root, num_descendents);
        return root;
        }

    /**
     * Adds a hierarchy of children, grandchildren. The length of the array indicates
     * the depth of the hierarchy. The values of the array indicates the number of
     * descendants at each depth.
     */
    private static void addDecendents(ExampleDataNode parent, int[] num_descendents)
        {
        if (num_descendents.length < 1)
            return;

        int[] num_grandchildren = null;
        if (num_descendents.length > 1)
            num_grandchildren = Arrays.copyOfRange(num_descendents, 1, num_descendents.length);

        for (int i = 0; i < num_descendents[0]; i++)
            {
            ExampleDataNode child = new ExampleDataNode(parent.getName() + "." + (i+1));
            if (num_grandchildren !=  null)
                addDecendents(child, num_grandchildren);
            parent.addChild(child);
            }
        }

    public static ExampleDataNode createRandomLeaf(ExampleDataNode root)
        {
        ExampleDataNode node = root;
        List<ExampleDataNode> children = node.getChildren();
        while (children.size() > 0)
            {
            node = children.get(new Random().nextInt(children.size()));
            children = node.getChildren();
            }

        ExampleDataNode new_node = new ExampleDataNode(node.getName() + " - new leaf ");
        node.addChild(new_node);
        return new_node;
        }
    }


