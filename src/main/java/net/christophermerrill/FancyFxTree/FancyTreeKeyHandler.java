package net.christophermerrill.FancyFxTree;

import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.input.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class FancyTreeKeyHandler
    {
    FancyTreeKeyHandler(TreeView tree_view, FancyTreeOperationHandler handler)
        {
        _tree = tree_view;
        _handler = handler;

        _tree.setOnKeyPressed(event ->
            {
            ObservableList selected_items = _tree.getSelectionModel().getSelectedItems();
            boolean handled;
            if (event.getCode().equals(KeyCode.DELETE) && !event.isShiftDown())
                handled = _handler.handleDelete(selected_items);
            else if ((event.isShortcutDown() && event.getCode().equals(KeyCode.C))
                || (event.isShortcutDown() && event.getCode().equals(KeyCode.INSERT)))
                handled = _handler.handleCopy(selected_items);
            else if ((event.isShortcutDown() && event.getCode().equals(KeyCode.X))
                || (event.isShiftDown() && event.getCode().equals(KeyCode.DELETE)))
                handled = _handler.handleCut(selected_items);
            else if ((event.isShortcutDown() && event.getCode().equals(KeyCode.V))
                || (event.isShiftDown() && event.getCode().equals(KeyCode.INSERT)))
                handled = _handler.handlePaste(selected_items);
            else if (event.isShortcutDown() && event.getCode().equals(KeyCode.Z))
                handled = _handler.handleUndo();
            else
                return;

            if (handled)
                event.consume();
            });
        }

    private TreeView _tree;
    private FancyTreeOperationHandler _handler;
    }


