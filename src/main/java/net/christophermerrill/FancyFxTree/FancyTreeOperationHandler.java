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

    public boolean handleDelete(ObservableList<TreeItem<T>> selected_items) { return false; }
    public boolean handleCut(ObservableList<TreeItem<T>> selected_items) { return false; }
    public boolean handleCopy(ObservableList<TreeItem<T>> selected_items) { return false; }
    public boolean handlePaste(ObservableList<TreeItem<T>> selected_items) { return false; }
    public boolean handleUndo() { return false; }

    public StartDragInfo startDrag(List<List<Integer>> selection_paths, ObservableList<TreeItem<T>> selected_items) { return null; }
    public boolean finishDrag(TransferMode transfer_mode, Dragboard dragboard, T item, DropLocation location) { return false; }
    public void handleDoubleClick(TreeCell<T> cell, boolean control_down, boolean shift_down, boolean alt_down) { }
    public ContextMenu getContextMenu(ObservableList<TreeItem<T>> selected_items) { return null; }

    public DragOverInfo dragOver(Dragboard dragboard, T onto_node)
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

    /**
     * Creates context menu items for the EditTypes provided. Call this from the
     * #getContextMenu() method to easily add them to the menu.
     */
    protected MenuItem[] createEditMenuItems(ObservableList<TreeItem<T>> selected_items, EditType... types)
        {
        MenuItem[] items = new MenuItem[types.length];
        int index = 0;
        for (EditType type : types)
            {
            MenuItem item = new MenuItem(type.name());
            item.setId(type.getMenuId());
            items[index++] = item;
            switch (type)
                {
                case Cut:
                    item.setOnAction(event -> handleCut(selected_items));
                    break;
                case Copy:
                    item.setOnAction(event -> handleCopy(selected_items));
                    break;
                case Delete:
                    item.setOnAction(event -> handleDelete(selected_items));
                    break;
                case Paste:
                    item.setOnAction(event -> handlePaste(selected_items));
                    break;
                }
            }
        return items;
        }

    public enum EditType
        {
        Cut("ftoh-et-cut"),
        Copy("ftoh-et-copy"),
        Paste("ftoh-et-paste"),
        Delete("ftoh-et-delete");

        EditType(String id)
            {
            _id = id;
            }

        public String getMenuId()
            {
            return _id;
            }

        private String _id;
        }
    }


