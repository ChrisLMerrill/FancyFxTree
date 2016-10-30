package net.christophermerrill.FancyFxTree;

import com.sun.javafx.scene.control.skin.*;
import javafx.scene.control.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class FancyTreeViewSkin extends TreeViewSkin
    {
    FancyTreeViewSkin(TreeView tree)
        {
        super(tree);
        }

    boolean isIndexVisible(int index)
        {
        if (flow.getFirstVisibleCell() != null &&
            flow.getLastVisibleCell() != null &&
            flow.getFirstVisibleCell().getIndex() <= index &&
            flow.getLastVisibleCell().getIndex() >= index)
            return true;
        return false;
        }
    }


