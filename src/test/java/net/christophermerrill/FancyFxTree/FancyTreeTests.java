package net.christophermerrill.FancyFxTree;

import javafx.application.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import net.christophermerrill.FancyFxTree.example.*;
import net.christophermerrill.testfx.*;
import org.junit.*;

import java.util.*;

import static javafx.scene.input.KeyCode.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class FancyTreeTests extends ComponentTest
    {
    @Test
    public void showNodes()
        {
        createBasicTreeAndData();

        // look for the nodes
        checkNodesVisible122();
        }

    @Test
    public void modelValueChanged()
        {
        createBasicTreeAndData();

        // root node
        changeNodeAndVerifyDisplayChange(_model);

        // first child
        changeNodeAndVerifyDisplayChange(_model.getChildren().get(0));

        // first grandchild
        changeNodeAndVerifyDisplayChange(_model.getChildren().get(0).getChildren().get(0));
        }

    private void changeNodeAndVerifyDisplayChange(ExampleDataNode node)
        {
        // change the node so that the display value changes
        final String old_label = node.getName();
        final String new_label = old_label + "-changed";
        node.setName(new_label);
        waitForUiEvents();

        Assert.assertFalse("old label is still visible", exists(old_label));
        Assert.assertTrue("new label is not visible", exists(new_label));
        }

    @Test
    public void nodeAdded()
        {
        createBasicTreeAndData();

        addNodeAndVerifyDisplayed(_model);
        addNodeAndVerifyDisplayed(_model.getChildren().get(0));
        addNodeAndVerifyDisplayed(_model.getChildren().get(0).getChildren().get(0));
        }

    @Test
    public void nodeInserted()
        {
        createBasicTreeAndData();

        insertNodeAndVerifyDisplayed(_model);
        insertNodeAndVerifyDisplayed(_model.getChildren().get(0));
        insertNodeAndVerifyDisplayed(_model.getChildren().get(0).getChildren().get(0));
        }

    @Test
    public void nodeWithChildrenAdded()
        {
        createBasicTreeAndData();
        ExampleDataNode new_parent = new ExampleDataNode("new_parent");
        ExampleDataNode new_child = new ExampleDataNode("new_child");
        new_parent.addChild(new_child);
        insertNodeAndVerifyDisplayed(_model, new_parent);
        waitForUiEvents();

        _tree.expandToMakeVisible(new_child);
        waitForUiEvents();

        Assert.assertTrue("new child is not visible", exists(new_child.getName()));
        }

    private void addNodeAndVerifyDisplayed(ExampleDataNode node)
        {
        final String new_node_label = node.getName() + "-new" + node.getChildren().size();
        ExampleDataNode new_node = new ExampleDataNode(new_node_label);
        node.addChild(new_node);
        waitForUiEvents();

        Assert.assertTrue("new node is not visible", exists(new_node_label));
        }

    private void insertNodeAndVerifyDisplayed(ExampleDataNode parent)
        {
        final String new_node_label = parent.getName() + "-new" + parent.getChildren().size();
        ExampleDataNode new_node = new ExampleDataNode(new_node_label);
        insertNodeAndVerifyDisplayed(parent, new_node);
        }

    private void insertNodeAndVerifyDisplayed(ExampleDataNode parent, ExampleDataNode new_node)
        {
        int index = parent.getChildren().size() > 0 ? 1 : 0;
        parent.insertChild(new_node, index);
        waitForUiEvents();

        _tree.expandToMakeVisible(new_node);
        waitForUiEvents();

        Assert.assertTrue("new node is not visible", exists(new_node.getName()));
        }

    @Test
    public void removeFirstChildNode()
        {
        createBasicTreeAndData();
        ExampleDataNode to_remove = _model.getChildren().get(0);
        removeNode(_model, to_remove);
        }

    @Test
    public void removeLastChildNode()
        {
        createBasicTreeAndData();
        ExampleDataNode to_remove = _model.getChildren().get(_model.getChildren().size() - 1);
        removeNode(_model, to_remove);
        }

    @Test
    public void removeGrandchild()
        {
        createBasicTreeAndData();
        ExampleDataNode to_remove = _model.getChildren().get(0).getChildren().get(0);
        removeNode(_model.getChildren().get(0), to_remove);
        }

    private void removeNode(ExampleDataNode parent, ExampleDataNode to_remove)
        {
        parent.removeChild(to_remove);
        waitForUiEvents();
        Assert.assertFalse("removed node is still visible", exists(to_remove.getName()));
        }

    @Test
    public void copyByControlCKeys()
        {
        createBasicTreeAndData();

        clickOn("1.1");
        push(CONTROL, C);

        Assert.assertTrue("control-C event not captured", _operations_handler._copy && _operations_handler._selected_items.size() == 1);
        }

    @Test
    public void copyByControlInsertKeys()
        {
        createBasicTreeAndData();

        clickOn("1.1");
        push(CONTROL, INSERT);

        Assert.assertTrue("control-insert event not captured", _operations_handler._copy && _operations_handler._selected_items.size() == 1);
        }

    @Test
    public void cutByControlXKeys()
        {
        createBasicTreeAndData();

        clickOn("1.1");
        push(CONTROL, X);

        Assert.assertTrue("control-x event not captured", _operations_handler._cut && _operations_handler._selected_items.size() == 1);
        }

    @Test
    public void cutByShiftDeleteKeys()
        {
        createBasicTreeAndData();

        clickOn("1.1");
        press(SHIFT).push(DELETE).release(SHIFT);
//        push(SHIFT, DELETE);

        Assert.assertTrue("shift-delete event not captured", _operations_handler._cut && _operations_handler._selected_items.size() == 1);
        }

    @Test
    public void deleteByDeleteKey()
        {
        createBasicTreeAndData();

        clickOn("1.1");
        push(DELETE);

        Assert.assertTrue("delete event not captured", _operations_handler._delete && _operations_handler._selected_items.size() == 1);
        }

    @Test
    public void pasteByControlVKeys()
        {
        createBasicTreeAndData();

        clickOn("1.1");
        press(CONTROL).push(V).release(CONTROL);

        Assert.assertTrue("control-v event not captured", _operations_handler._paste && _operations_handler._selected_items.size() == 1);
        }

    @Test
    public void pasteByShiftInsertKeys()
        {
        createBasicTreeAndData();

        clickOn("1.1");
        press(SHIFT).push(INSERT).release(SHIFT);
//        push(SHIFT, INSERT);

        Assert.assertTrue("shift-insert event not captured", _operations_handler._paste && _operations_handler._selected_items.size() == 1);
        }

    /**
     * This test passes in isolation but fails when run after certain tests.
     * Diagnosis reveals that the drag handler (FancyTreeCell) never gets called.
     * Unable to determine cause.
     */
    @Test
    public void dragAndDropOnto()
        {
        createBasicTreeAndData();

        drag("1.1", MouseButton.PRIMARY).dropTo("1.2.2");
        waitForUiEvents();

        Assert.assertEquals("the content was not dropped", "1.1", _operations_handler._dropped_content);
        }

    /**
     * This test passes in isolation but fails when run after certain tests.
     * Diagnosis reveals that the drag handler (FancyTreeCell) never gets called.
     * Unable to determine cause.
     */
    @Test
    public void dragAndDropMultiple()
        {
        createBasicTreeAndData();

        clickOn("1.1.1");
        press(SHIFT).clickOn("1.1.2").release(SHIFT);
        drag("1.1.2", MouseButton.PRIMARY);
        dropTo("1.2.2");

        Assert.assertEquals("2 items should be dragged", 2, _operations_handler._drag_count);
        }

    @Test
    public void dragAndDropOntoDenied()
        {
        createBasicTreeAndData();

        drag("1.1", MouseButton.PRIMARY);
        dropTo("1.2.2");

        Assert.assertEquals("the drop should have been denied", null, _operations_handler._dropped_content);
        }

    @Test
    public void dragAndDropBefore()
        {
        createBasicTreeAndData();

        drag("1.1", MouseButton.PRIMARY).dropTo("1.2");

        // how to detect?

        Assert.fail("this test is not yet finished"); // TODO
        }

    @Test
    public void dragAndDropAfter()
        {
        createBasicTreeAndData();

        clickOn("1.1");
        drag("1.1", MouseButton.PRIMARY).dropTo("1.2");

        // how to detect?

        Assert.fail("this test is not yet finished"); // TODO
        }

/*
    @Test
    public void moveByDrag_single()
        {

        Assert.fail("this test is not yet finished"); // TODO
        }
*/

    @Test
    public void copyByDrag_multiple_continguous()
        {

        Assert.fail("this test is not yet finished"); // TODO
        }

    @Test
    public void moveByDrag_multiple_continguous()
        {

        Assert.fail("this test is not yet finished"); // TODO
        }

/*
    @Test
    public void copyByDrag_multiple_dispersed()
        {

        Assert.fail("this test is not yet finished"); // TODO
        }
*/

    @Test
    public void moveByDrag_multiple_dispersed()
        {

        Assert.fail("this test is not yet finished"); // TODO
        }

    @Test
    public void pasteByDragFromOtherTree()
        {

        Assert.fail("this test is not yet finished"); // TODO
        }

/*
    @Test
    public void cutToOtherTree()
        {

        Assert.fail("this test is not yet finished"); // TODO
        }
*/

    @Test
    public void expandToNode()
        {
        // this is tested indirectly by the node addition tests, since they must make the node visible
        // in order to check that it displayed in the tree. This no-op test remains for clarity/documentation.
        }

    @Test
    public void scrollToNode()
        {
        ExampleDataNode root = ExampleDataNodeBuilder.create(new int[] {2, 2, 3, 2, 1});
        setupTree(root);

        ExampleDataNode last_node = root.getChildren().get(1)
                                                .getChildren().get(1)
                                                .getChildren().get(1)
                                                .getChildren().get(1)
                                                .getChildren().get(0);

        _tree.scrollToAndMakeVisible(last_node);
        waitForUiEvents();

        // there is currently no way to visibly determine if the item is visible on-screen.
        // https://groups.google.com/forum/#!topic/testfx-discuss/R0WM_TaloDI
        //
        // This test is left here for manual use and documentation of the issue.
        // To test manually, set a breakpoint on the next line and visually verify
        // that the view is scrolled to the bottom of the tree (1.2.2.2.2.2)
        System.out.println("is node 1.2.2.2.2.2 visible?");
        }

    private void createBasicTreeAndData()
        {
        ExampleDataNode root = ExampleDataNodeBuilder.create(new int[] {2,2});
        setupTree(root);
        }

    private void setupTree(ExampleDataNode root)
        {
        _model = root;
        ExampleTreeNodeFacade root_facade = new ExampleTreeNodeFacade(_model);
        TreeItem<FancyTreeItemValueHolder> root_item = FancyTreeItemBuilder.create(root_facade);

        _operations_handler = new MockOperationHandler();
        _tree = new FancyTreeView(_operations_handler);
        _tree.setRoot(root_item);
        _tree.expandAll();

        Platform.runLater(() -> _pane.setCenter(_tree));
        waitForUiEvents();
        }

    private void checkNodesVisible122()
        {
        Assert.assertTrue("The root node (1) is not visible", exists("1"));
        Assert.assertTrue("node 1.1 is not visible", exists("1.1"));
        Assert.assertTrue("node 1.1.1 is not visible", exists("1.1.1"));
        Assert.assertTrue("node 1.1.2 is not visible", exists("1.1.2"));
        Assert.assertTrue("node 1.2 is not visible", exists("1.2"));
        Assert.assertTrue("node 1.2.1 is not visible", exists("1.2.1"));
        Assert.assertTrue("node 1.2.2 is not visible", exists("1.2.2"));
        }

    @Override
    protected Node createComponentNode()
        {
        _pane = new BorderPane();
        return _pane;
        }

    @Override
    protected double getDefaultHeight()
        {
        return super.getDefaultHeight() * 2;
        }

    private class MockOperationHandler extends FancyTreeOperationHandler
        {
        @Override
        public boolean handleDelete(ObservableList selected_items)
            {
            _delete = true;
            _selected_items = selected_items;
            return true;
            }

        @Override
        public boolean handleCut(ObservableList selected_items)
            {
            _cut = true;
            _selected_items = selected_items;
            return true;
            }

        @Override
        public boolean handleCopy(ObservableList selected_items)
            {
            _copy = true;
            _selected_items = selected_items;
            return true;
            }

        @Override
        public boolean handlePaste(ObservableList selected_items)
            {
            _paste = true;
            _selected_items = selected_items;
            return true;
            }

        @Override
        public StartDragInfo startDrag(List<List<Integer>> selection_paths, ObservableList<TreeItem> selected_items)
            {
            StartDragInfo info = new StartDragInfo();
            info.addContent(DataFormat.PLAIN_TEXT, selected_items.get(0).getValue().toString());
            _drag_count = selected_items.size();
            return info;
            }

        @Override
        public DragOverInfo dragOver(Dragboard dragboard)
            {
            DragOverInfo info = new DragOverInfo();
            if (!_allow_drag_onto)
                info._drop_locations = new DropLocation[0];
            return info;
            }

        @Override
        public boolean finishDrag(TransferMode transfer_mode, Dragboard dragboard)
            {
            _dropped_content = dragboard.getContent(DataFormat.PLAIN_TEXT);
            return true;
            }

        ObservableList _selected_items;
        boolean _delete = false;
        boolean _cut = false;
        boolean _copy = false;
        boolean _paste = false;

        boolean _allow_drag_onto = true;
        int _drag_count;
        Object _dropped_content;
        }

    private BorderPane _pane;
    private ExampleDataNode _model;
    private FancyTreeView _tree;
    private MockOperationHandler _operations_handler;
    }


