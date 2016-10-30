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
    public boolean handleDeleteKeystroke(ObservableList<TreeItem<T>> selected_items) { return false; }
    public boolean handleCutKeystroke(ObservableList<TreeItem<T>> selected_items) { return false; }
    public boolean handleCopyKeystroke(ObservableList<TreeItem<T>> selected_items) { return false; }
    public boolean handlePasteKeystroke(ObservableList<TreeItem<T>> selected_items) { return false; }

    public StartDragInfo startDrag(List<List<Integer>> selection_paths, ObservableList<TreeItem<T>> selected_items) { return null; }
    public boolean finishDrag(TransferMode transfer_mode, Dragboard dragboard, FancyTreeNodeFacade item, DropLocation location) { return false; }

    public DragOverInfo dragOver(Dragboard dragboard)
        {
        DragOverInfo info = new DragOverInfo();
        info.addAllModesAndLocations();
        return info;
        }

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

        public Map<DataFormat, Object> _content = new HashMap<>();
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

        public void addTransferMode(TransferMode mode)
            {
            _transfer_modes.add(mode);
            }

        public void removeTransferMode(TransferMode mode)
            {
            _transfer_modes.remove(mode);
            }

        public void addDropLocation(DropLocation location)
            {
            _drop_locations.add(location);
            }

        public void removeDropLocation(DropLocation location)
            {
            _drop_locations.remove(location);
            }

        public List<TransferMode> _transfer_modes = new ArrayList<>();
        public List<DropLocation> _drop_locations = new ArrayList<>();
        }

    public static DataFormat JSON_SERIALIZED_OBJECT = new DataFormat("application/x-json-serialized");
    }


