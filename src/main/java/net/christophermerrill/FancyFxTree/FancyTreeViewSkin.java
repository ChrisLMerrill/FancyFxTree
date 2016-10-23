package net.christophermerrill.FancyFxTree;

import com.sun.javafx.scene.control.skin.*;
import javafx.scene.control.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class FancyTreeViewSkin extends TreeViewSkin
    {
    public FancyTreeViewSkin(TreeView tree)
        {
        super(tree);
        }

    public boolean isIndexVisible(int index)
        {
        if (flow.getFirstVisibleCell() != null &&
            flow.getLastVisibleCell() != null &&
            flow.getFirstVisibleCell().getIndex() <= index &&
            flow.getLastVisibleCell().getIndex() >= index)
            return true;
        return false;
        }
    }


