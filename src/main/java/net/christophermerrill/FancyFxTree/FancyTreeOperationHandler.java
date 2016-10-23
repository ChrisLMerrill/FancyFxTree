package net.christophermerrill.FancyFxTree;

import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.input.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public abstract class FancyTreeOperationHandler
    {
    public boolean handleDelete(ObservableList selected_items) { return false; }
    public boolean handleCut(ObservableList selected_items) { return false; }
    public boolean handleCopy(ObservableList selected_items) { return false; }
    public boolean handlePaste(ObservableList selected_items) { return false; }

    public StartDragInfo startDrag(List<List<Integer>> selection_paths, ObservableList<TreeItem> selected_items) { return null; }
    public boolean finishDrag(TransferMode transfer_mode, Dragboard dragboard) { return false; }

    public DragOverInfo dragOver(Dragboard dragboard) {return new DragOverInfo();}

    public enum DropLocation
        {
        BEFORE,
        AFTER,
        ON
        }

    public class StartDragInfo
        {
        public StartDragInfo()
            {
            _transfer_modes = new TransferMode[] {TransferMode.COPY, TransferMode.MOVE};
            }

        public void addContent(DataFormat format, Object content)
            {
            _content.put(format, content);
            }

        Map<DataFormat, Object> _content = new HashMap<>();
        TransferMode[] _transfer_modes;
        }

    public class DragOverInfo
        {
        public DragOverInfo()
            {
            _transfer_modes = new TransferMode[] {TransferMode.COPY, TransferMode.MOVE};
            _drop_locations = new DropLocation[] {DropLocation.BEFORE, DropLocation.ON, DropLocation.AFTER};
            }

        TransferMode[] _transfer_modes;
        DropLocation[] _drop_locations;
        }
    }


