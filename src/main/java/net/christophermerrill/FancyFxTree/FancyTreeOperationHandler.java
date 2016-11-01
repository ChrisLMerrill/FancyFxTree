package net.christophermerrill.FancyFxTree;

import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.input.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public abstract class FancyTreeOperationHandler<T extends FancyTreeNodeFacade>
    {
    public void selectionChanged(ObservableList<TreeItem<T>> selected_items) { }

    public boolean handleDeleteKeystroke(ObservableList<TreeItem<T>> selected_items) { return false; }
    public boolean handleCutKeystroke(ObservableList<TreeItem<T>> selected_items) { return false; }
    public boolean handleCopyKeystroke(ObservableList<TreeItem<T>> selected_items) { return false; }
    public boolean handlePasteKeystroke(ObservableList<TreeItem<T>> selected_items) { return false; }

    public StartDragInfo startDrag(List<List<Integer>> selection_paths, ObservableList<TreeItem<T>> selected_items) { return null; }
    public boolean finishDrag(TransferMode transfer_mode, Dragboard dragboard, T item, DropLocation location) { return false; }
    public void handleDoubleClick(boolean control_down, boolean shift_down, boolean alt_down) { }

    public DragOverInfo dragOver(Dragboard dragboard)
        {
        DragOverInfo info = new DragOverInfo();
        info.addAllModesAndLocations();
        return info;
        }

    @SuppressWarnings("WeakerAccess") // part of public API
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

        @SuppressWarnings("WeakerAccess")  // part of public API
        public Map<DataFormat, Object> _content = new HashMap<>();

        @SuppressWarnings("WeakerAccess")  // part of public API
        public TransferMode[] _transfer_modes;
        }

    public class DragOverInfo
        {
        public void addAllModesAndLocations()
            {
            addTransferMode(TransferMode.COPY);
            addTransferMode(TransferMode.MOVE);
            addDropLocation(DropLocation.BEFORE);
            addDropLocation(DropLocation.ON);
            addDropLocation(DropLocation.AFTER);
            }

        @SuppressWarnings("WeakerAccess")  // part of public API
        public void addTransferMode(TransferMode mode)
            {
            _transfer_modes.add(mode);
            }

        @SuppressWarnings("WeakerAccess,unused")  // part of public API
        public void removeTransferMode(TransferMode mode)
            {
            _transfer_modes.remove(mode);
            }

        @SuppressWarnings("WeakerAccess")  // part of public API
        public void addDropLocation(DropLocation location)
            {
            _drop_locations.add(location);
            }

        @SuppressWarnings("WeakerAccess,unused")  // part of public API
        public void removeDropLocation(DropLocation location)
            {
            _drop_locations.remove(location);
            }

        List<TransferMode> _transfer_modes = new ArrayList<>();
        List<DropLocation> _drop_locations = new ArrayList<>();
        }
    }


