package net.christophermerrill.FancyFxTree;

import com.sun.javafx.scene.control.skin.*;
import javafx.scene.control.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class FancyTreeViewSkin extends TreeViewSkin
    {
    public FancyTreeViewSkin(TreeView treeView)
        {
        super(treeView);
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


