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
    public boolean handleDelete(ObservableList<TreeItem<T>> selected_items) { return false; }
    public boolean handleCut(ObservableList<TreeItem<T>> selected_items) { return false; }
    public boolean handleCopy(ObservableList<TreeItem<T>> selected_items) { return false; }
    public boolean handlePaste(ObservableList<TreeItem<T>> selected_items) { return false; }

    public StartDragInfo startDrag(List<List<Integer>> selection_paths, ObservableList<TreeItem<T>> selected_items) { return null; }
    public boolean finishDrag(TransferMode transfer_mode, Dragboard dragboard, FancyTreeNodeFacade item) { return false; }

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

        public Map<DataFormat, Object> _content = new HashMap<>();
        public TransferMode[] _transfer_modes;
        }

    public class DragOverInfo
        {
        public DragOverInfo()
            {
            _transfer_modes = new TransferMode[] {TransferMode.COPY, TransferMode.MOVE};
            _drop_locations = new DropLocation[] {DropLocation.BEFORE, DropLocation.ON, DropLocation.AFTER};
            }

        public TransferMode[] _transfer_modes;
        public DropLocation[] _drop_locations;
        }

    public static DataFormat JSON_SERIALIZED_OBJECT = new DataFormat("application/x-json-serialized");
    }


