package net.christophermerrill.FancyFxTree;

import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.input.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class FancyTreeCell extends TreeCell<FancyTreeNodeFacade>
    {
    public FancyTreeCell(FancyTreeOperationHandler handler)
        {
        addStyle(CELL_STYLE);
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
            Map<DataFormat, Object> content_map = result._content;
            for (DataFormat format : content_map.keySet())
                content.put(format, result._content.get(format));

            Dragboard dragboard = startDragAndDrop(result._transfer_modes);
            dragboard.setContent(content);

            e.consume();
            });

        setOnDragDropped(e ->
            {
            boolean completed = _handler.finishDrag(e.getTransferMode(), e.getDragboard(), getItem());
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
    protected void updateItem(FancyTreeNodeFacade item, boolean empty)
        {
        super.updateItem(item, empty);

        if (empty)
            {
            setText(null);
            setGraphic(null);
            }
        else
            {
            setText(item.getLabelText());
            setGraphic(null);
            }
        }

    private final FancyTreeOperationHandler _handler;

    private void addStyle(String new_style)
        {
        for (String style : getStyleClass())
            if (style.equals(new_style))
                return;
        getStyleClass().add(new_style);
        }

    //
    // Styles for the cells
    //
    public static final String CELL_STYLE = "fancytreecell-default";
    }


