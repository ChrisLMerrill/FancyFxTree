package net.christophermerrill.FancyFxTree;

import javafx.css.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class FancyTreeCell extends TreeCell<FancyTreeNodeFacade>
    {
    FancyTreeCell(FancyTreeOperationHandler handler, boolean enable_dnd)
        {
        addStyle(CELL_STYLE_NAME);
        _handler = handler;

        if (enable_dnd)
            setupDragAndDrop();
        }

    private void setupDragAndDrop()
        {
        setOnDragEntered(e ->
            {
            resetCursorPosition();
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
            boolean completed = _handler.finishDrag(e.getTransferMode(), e.getDragboard(), getItem(), _drop_location);
            e.setDropCompleted(completed);
            e.consume();
            });

        setOnDragOver(e ->
            {
            removeStyle(DROP_BEFORE_STYLE_NAME);
            removeStyle(DROP_ON_STYLE_NAME);
            removeStyle(DROP_AFTER_STYLE_NAME);

            updateCursorPositionAndHoverTime(e);

            if (getItem().getChildren().size() > 0 && !(getTreeItem().isExpanded()) && isWaitingForTreeExpand())
                getTreeItem().setExpanded(true);

            Point2D sceneCoordinates = localToScene(0d, 0d);
            double cell_height = getHeight();
            double mouse_y = e.getSceneY() - (sceneCoordinates.getY());  // this will be the y-coord within the cell
            FancyTreeOperationHandler.DragOverInfo info = _handler.dragOver(e.getDragboard());
            DropLocationCalculator calculator = new DropLocationCalculator(cell_height, mouse_y, info);
            _drop_location = calculator.getDropLocation();
            if (_drop_location != null)
                {
                addStyle(DROP_LOCATION_TO_STYLE_MAP.get(_drop_location));
                TransferMode[] modes = new TransferMode[info._transfer_modes.size()];
                info._transfer_modes.toArray(modes);
                e.acceptTransferModes(modes);
                }
            e.consume();
            });

        setOnDragExited(event ->
            {
            removeStyle(DROP_BEFORE_STYLE_NAME);
            removeStyle(DROP_ON_STYLE_NAME);
            removeStyle(DROP_AFTER_STYLE_NAME);
            event.consume();
            });
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
            updateCellUI(item);
            }
        }

    private void updateCellUI(FancyTreeNodeFacade item)
	    {
	    Node node = item.getCustomCellUI();
	    if (node == null)
	        {
	        setText(item.getLabelText());
	        setGraphic(item.getIcon());
	        }
	    else
	        {
	        setText(null);
	        setGraphic(node);
	        }
        pseudoClassStateChanged(EDITING_CLASS, isEditing());
	    }

    private void addStyle(String new_style)
        {
        if (!getStyleClass().contains(new_style))
            getStyleClass().add(new_style);
        }

    private void removeStyle(String remove_style)
        {
        getStyleClass().remove(remove_style);
        }

    // for hover-to-expand feature
    private void updateCursorPositionAndHoverTime(DragEvent e)
        {
        if (e.getSceneX() == _cursor_x && e.getSceneY() == _cursor_y)
            return;

        _cursor_x = (int) e.getSceneX();
        _cursor_y = (int) e.getSceneY();
        _cursor_hover_since = System.currentTimeMillis();
        }

    // for hover-to-expand feature
    private boolean isWaitingForTreeExpand()
        {
        return System.currentTimeMillis() - _cursor_hover_since > _hover_expand_duration;
        }

    // for hover-to-expand feature
    private void resetCursorPosition()
        {
        _cursor_x = 0;
        _cursor_y = 0;
        _cursor_hover_since = 0;
        }

    @Override
    public void startEdit()
	    {
	    if (getTreeView() != null
		    && getTreeView().isEditable()
		    && isEditable())
		    {
		    setText(null);
		    final FancyTreeCellEditor editor = getCellEditor();
		    setGraphic(editor.getNode());
		    editor.getNode().requestFocus();
		    }
	    }


    @Override
    public void cancelEdit()
	    {
	    updateCellUI(getItem());
	    }

    @Override
    public void commitEdit(FancyTreeNodeFacade facade)
	    {
	    updateCellUI(getItem());
	    }

    private FancyTreeCellEditor getCellEditor()
	    {
	    FancyTreeCellEditor editor = getItem().getCustomCellEditor();
	    if (editor == null)
	        editor = new TextCellEditor();
	    editor.setCell(this);
	    return editor;
	    }

    void setHoverExpandDuration(long hover_expand_duration)
        {
        _hover_expand_duration = hover_expand_duration;
        }

    private final FancyTreeOperationHandler _handler;
    private FancyTreeOperationHandler.DropLocation _drop_location;
    private int _cursor_x;
    private int _cursor_y;
    private long _cursor_hover_since;
    private long _hover_expand_duration = FancyTreeView.DEFAULT_HOVER_EXPAND_DURATION;

    //
    // Styles for the cells
    //
    static final String CELL_STYLE_NAME = "fancytreecell";
    static final String DROP_BEFORE_STYLE_NAME = "fancytreecell-drop-before";
    static final String DROP_ON_STYLE_NAME = "fancytreecell-drop-on";
    static final String DROP_AFTER_STYLE_NAME = "fancytreecell-drop-after";

    //
    // Pseudo-styles for the cell
    //
    private static PseudoClass EDITING_CLASS = PseudoClass.getPseudoClass("editing");

    private static final Map<FancyTreeOperationHandler.DropLocation, String> DROP_LOCATION_TO_STYLE_MAP = new HashMap<>();
    static
        {
        DROP_LOCATION_TO_STYLE_MAP.put(FancyTreeOperationHandler.DropLocation.BEFORE, DROP_BEFORE_STYLE_NAME);
        DROP_LOCATION_TO_STYLE_MAP.put(FancyTreeOperationHandler.DropLocation.ON, DROP_ON_STYLE_NAME);
        DROP_LOCATION_TO_STYLE_MAP.put(FancyTreeOperationHandler.DropLocation.AFTER, DROP_AFTER_STYLE_NAME);
        }

    private class DropLocationCalculator
        {
        DropLocationCalculator(double cell_height, double mouse_y, FancyTreeOperationHandler.DragOverInfo info)
            {
            _cell_height = cell_height;
            _mouse_y = mouse_y;
            _info = info;
            calculate();
            }

        private void calculate()
            {
            // re-order and count the locations
            List<FancyTreeOperationHandler.DropLocation> _allowed_locations = new ArrayList<>();
            if (_info._drop_locations.contains(FancyTreeOperationHandler.DropLocation.BEFORE))
                _allowed_locations.add(FancyTreeOperationHandler.DropLocation.BEFORE);
            if (_info._drop_locations.contains(FancyTreeOperationHandler.DropLocation.ON))
                _allowed_locations.add(FancyTreeOperationHandler.DropLocation.ON);
            if (_info._drop_locations.contains(FancyTreeOperationHandler.DropLocation.AFTER))
                _allowed_locations.add(FancyTreeOperationHandler.DropLocation.AFTER);

            if (_allowed_locations.size() == 1)
                _drop_location = _allowed_locations.get(0);
            else if (_allowed_locations.size() == 2)
                {
                if (_mouse_y < (_cell_height * 0.5d))
                    _drop_location = _allowed_locations.get(0);
                else
                    _drop_location = _allowed_locations.get(1);
                }
            else if (_allowed_locations.size() == 3)
                {
                if (_mouse_y < (_cell_height * 0.25d))
                    _drop_location = _allowed_locations.get(0);
                else if (_mouse_y > (_cell_height * 0.75d))
                    _drop_location = _allowed_locations.get(2);
                else
                    _drop_location = _allowed_locations.get(1);
                }
            }

        FancyTreeOperationHandler.DropLocation getDropLocation()
            {
            return _drop_location;
            }

        double _cell_height;
        double _mouse_y;
        FancyTreeOperationHandler.DragOverInfo _info;
        FancyTreeOperationHandler.DropLocation _drop_location;
        }
    }


