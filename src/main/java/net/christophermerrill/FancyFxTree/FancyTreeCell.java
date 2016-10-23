package net.christophermerrill.FancyFxTree;

import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.input.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class FancyTreeCell extends TreeCell
    {
    public FancyTreeCell(FancyTreeOperationHandler handler)
        {
        _handler = handler;

        setOnDragEntered(e ->
            {
//            resetCursorPosition();
            e.consume();
            });

        setOnDragDone(Event::consume);

        setOnDragDetected(e ->
            {
            FancyTreeView tree = (FancyTreeView) getTreeView();

            FancyTreeOperationHandler.StartDragInfo result = _handler.startDrag(tree.getSelectionPaths(), tree.getSelectionModel().getSelectedItems());
            if (result == null)
                return;

            ClipboardContent content = new ClipboardContent();
            for (DataFormat format : result._content.keySet())
                content.put(format, result._content.get(format));

            Dragboard dragboard = startDragAndDrop(result._transfer_modes);
            dragboard.setContent(content);

            e.consume();
            });

        setOnDragDropped(e ->
            {
            boolean completed = _handler.finishDrag(e.getTransferMode(), e.getDragboard());
            e.setDropCompleted(completed);
            e.consume();
            });

        setOnDragOver(e ->
            {
            FancyTreeOperationHandler.DragOverInfo info = _handler.dragOver(e.getDragboard());
            boolean drop_onto_allowed = false;
            for (FancyTreeOperationHandler.DropLocation location : info._drop_locations)
                if (location == FancyTreeOperationHandler.DropLocation.ON)
                    drop_onto_allowed = true;
            if (drop_onto_allowed)
                e.acceptTransferModes(info._transfer_modes);
            e.consume();
            });

        setOnDragExited(Event::consume);

        }

    @Override
    protected void updateItem(Object item, boolean empty)
        {
        super.updateItem(item, empty);

        if (empty)
            {
            setText(null);
            setGraphic(null);
//            setupStyle(DEFAULT_STYLE);  // TODO handle styles
            }
        else
            {
            FancyTreeItemValueHolder holder = (FancyTreeItemValueHolder) item;

            setText(holder.getValue().getLabelText());
            setGraphic(null);
            }
        }

    private final FancyTreeOperationHandler _handler;

    //
    // Styles for the cells
    //
    private static final String DEFAULT_STYLE = "fancytree-default";
    }


