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
    public void addNode()
        {
        createBasicTreeAndData();
        addNodeAndVerifyDisplayed(_model);
        addNodeAndVerifyDisplayed(_model.getChildren().get(0));
        addNodeAndVerifyDisplayed(_model.getChildren().get(0).getChildren().get(0));
        }

    @Test
    public void addNodeWithChildren()
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

/* Test test fails due to a bug in TestFX: https://github.com/TestFX/TestFX/issues/310
   This function works when tested manually.
    public void cutByShiftDeleteKeys()
        {
        createBasicTreeAndData();

        clickOn("1.1");
        press(SHIFT).push(DELETE).release(SHIFT);
//        push(SHIFT, DELETE);

        Assert.assertTrue("shift-delete event not captured", _operations_handler._cut && _operations_handler._selected_items.size() == 1);
        }
*/

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

/* Test test fails due to a bug in TestFX: https://github.com/TestFX/TestFX/issues/310
   This function works when tested manually.
    @Test
    public void pasteByShiftInsertKeys()
        {
        createBasicTreeAndData();

        clickOn("1.1");
        press(SHIFT).push(INSERT).release(SHIFT);
//        push(SHIFT, INSERT);

        Assert.assertTrue("shift-insert event not captured", _operations_handler._paste && _operations_handler._selected_items.size() == 1);
        }
*/

    @Test
    public void moveByDragInto()
        {
        createBasicTreeAndData();

        ExampleDataNode target_parent = _model.getNodeByName("1.1");
        ExampleDataNode target_node = _model.getNodeByName("1.1.1");
        ExampleDataNode destination_node = _model.getNodeByName("1.2.2");

        Assert.assertTrue("expected root to start with 2 children", target_parent.getChildren().size() == 2);
        Assert.assertTrue("expected destination node to start with no children", destination_node.getChildren().size() == 0);

        drag(target_node.getName(), MouseButton.PRIMARY).dropTo(destination_node.getName());
        waitForUiEvents();

        Assert.assertNotNull("node not found in tree", _model.getNodeByName("1.1.1"));

        Assert.assertTrue("node was not removed from root", target_parent.getChildren().size() == 1);
        Assert.assertTrue("node was not added to destination", destination_node.getChildren().size() == 1);
        }

    @Test
    public void copyByDragInto()
        {
        createBasicTreeAndData();

        ExampleDataNode target_parent = _model.getNodeByName("1.1");
        ExampleDataNode target_node = _model.getNodeByName("1.1.1");
        ExampleDataNode destination_node = _model.getNodeByName("1.2.2");

        Assert.assertTrue("expected root to start with 2 children", target_parent.getChildren().size() == 2);
        Assert.assertTrue("expected destination node to start with no children", destination_node.getChildren().size() == 0);

        drag(target_node.getName(), MouseButton.PRIMARY);
        press(CONTROL);
        dropTo(destination_node.getName());
        release(CONTROL);
        waitForUiEvents();

        Assert.assertNotNull("copy not found", _model.getNodeByName(ExampleDataNode.getCopyName(target_node)));

        Assert.assertFalse("node was removed from root", target_parent.getChildren().size() == 1);
        Assert.assertTrue("node was not added to destination", destination_node.getChildren().size() == 1);
        }

    /**
     * This test passes in isolation but fails when run after certain tests.
     * Diagnosis reveals that the drag handler (FancyTreeCell) never gets called.
     * Unable to determine cause.
     */
    @Test
    public void moveMultipleByDragInto()
        {
        createBasicTreeAndData();

        ExampleDataNode target1 = _model.getNodeByName("1.1.1");
        ExampleDataNode target2 = _model.getNodeByName("1.1.2");
        ExampleDataNode destination = _model.getNodeByName("1.2.1");

        clickOn(target1.getName());
        press(SHIFT).clickOn(target2.getName()).release(SHIFT);
        drag(target2.getName(), MouseButton.PRIMARY);
        dropTo(destination.getName());
        waitForUiEvents();

        Assert.assertEquals("2 items should be dragged", 2, _operations_handler._drag_count);
        Assert.assertTrue("first item was not dropped", dropListContains(_operations_handler._dropped_nodes, target1));
        Assert.assertTrue("second item was not dropped", dropListContains(_operations_handler._dropped_nodes, target2));

        Assert.assertTrue(destination.contains(target1));
        Assert.assertTrue(destination.contains(target2));
        }

    /**
     * This test passes in isolation but fails when run after certain tests.
     * Diagnosis reveals that the drag handler (FancyTreeCell) never gets called.
     * Unable to determine cause.
     */
    @Test
    public void copyMultipleByDragInto()
        {
        createBasicTreeAndData();

        ExampleDataNode target_parent = _model.getNodeByName("1.1");
        ExampleDataNode target_node_1 = _model.getNodeByName("1.1.1");
        ExampleDataNode target_node_2 = _model.getNodeByName("1.1.2");
        ExampleDataNode destination = _model.getNodeByName("1.2.2");

        clickOn(target_node_1.getName());
        press(SHIFT).clickOn(target_node_2.getName()).release(SHIFT);
        press(CONTROL);
        drag(target_node_2.getName(), MouseButton.PRIMARY);
        dropTo(destination.getName());
        release(CONTROL);
        waitForUiEvents();

        Assert.assertEquals("2 items should be dragged", 2, _operations_handler._drag_count);
        Assert.assertTrue("first item was not dragged", dragListContains(_operations_handler._dragged_items, _model.getNodeByName("1.1.1")));
        Assert.assertTrue("second item was not dragged", dragListContains(_operations_handler._dragged_items, _model.getNodeByName("1.1.2")));

        Assert.assertTrue("orignal #1 was removed", target_parent.contains(target_node_1));
        Assert.assertTrue("orignal #2 was removed", target_parent.contains(target_node_2));

        ExampleDataNode copy_1 = _model.getNodeByName(ExampleDataNode.getCopyName(target_node_1));
        Assert.assertNotNull("1st node was not copied", copy_1);
        ExampleDataNode copy_2 = _model.getNodeByName(ExampleDataNode.getCopyName(target_node_2));
        Assert.assertNotNull("2nd node was not copied", copy_2);
        Assert.assertTrue("1st copy is not in destination", destination.contains(copy_1));
        Assert.assertTrue("2nd copy is not in destination", destination.contains(copy_2));
        }

    private boolean dragListContains(ObservableList<TreeItem<ExampleTreeNodeFacade>> dragged_items, ExampleDataNode node)
        {
        for (TreeItem<ExampleTreeNodeFacade> item : dragged_items)
            if (item.getValue().getModelNode() == node)
                return true;
        return false;
        }

    private boolean dropListContains(List<ExampleDataNode> dropped_nodes, ExampleDataNode node)
        {
        for (ExampleDataNode dropped_node : dropped_nodes)
            if (dropped_node.equals(node))
                return true;
        return false;
        }

    @Test
    public void dragAndDropIntoDenied()
        {
        createBasicTreeAndData();

        drag("1.1", MouseButton.PRIMARY);
        dropTo("1.2.2");

        Assert.assertEquals("the drop should have been denied", null, _operations_handler._dropped_content);
        }

    @Test
    public void moveByDragBefore()
        {
        createBasicTreeAndData();

        ExampleDataNode target_node = _model.getNodeByName("1.1.1");
        ExampleDataNode target_parent = _model.getNodeByName("1.1");
        ExampleDataNode destination_node = _model.getNodeByName("1.2.2");
        ExampleDataNode destination_parent = _model.getNodeByName("1.2");

        Node destination_area = lookup(destination_node.getName()).query();
        drag(target_node.getName(), MouseButton.PRIMARY);
        moveTo(destination_area);
        moveBy(0, - destination_area.getBoundsInParent().getHeight() * 0.4d);
        drop();

        Assert.assertFalse("The target node was not removed from its parent", target_parent.contains(target_node));
        Assert.assertTrue("The target node was not moved into the destination", destination_parent.contains(target_node));
        Assert.assertTrue("The target node is not in the right place in the destination", destination_parent.getChildren().get(1).equals(target_node));
        }

    @Test
    public void moveByDragAfter()
        {
        createBasicTreeAndData();

        ExampleDataNode target_node = _model.getNodeByName("1.1.1");
        ExampleDataNode target_parent = _model.getNodeByName("1.1");
        ExampleDataNode destination_node = _model.getNodeByName("1.2.1");
        ExampleDataNode destination_parent = _model.getNodeByName("1.2");

        Node destination_area = lookup(destination_node.getName()).query();
        drag(target_node.getName(), MouseButton.PRIMARY);
        moveTo(destination_area);
        moveBy(0, destination_area.getBoundsInParent().getHeight() * 0.4d);
        drop();

        Assert.assertFalse("The target node was not removed from its parent", target_parent.contains(target_node));
        Assert.assertTrue("The target node was not moved into the destination", destination_parent.contains(target_node));
        Assert.assertTrue("The target node is not in the right place in the destination", destination_parent.getChildren().get(1).equals(target_node));
        }

    @Test
    public void expandOnHover()
        {
        // hover over a collapsed tree item to expand it

        Assert.fail("this test is not yet finished"); // TODO
        }

    @Test
    public void expandToNode()
        {
        // This capability is tested indirectly by the node addition tests, since they must make the node visible
        // in order to check that it displayed in the tree. This no-op test remains as documententation of such.
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

    @Test
    public void defaultStyleApplied()
        {
        createBasicTreeAndData();
        Node node = lookup("1.1").query();
        Assert.assertTrue("style is missing from TreeCell", node.getStyleClass().contains(FancyTreeCell.CELL_STYLE_NAME));
        }

    @Test
    public void dropIntoStyleApplied()
        {
        createBasicTreeAndData();

        ExampleDataNode destination_node = _model.getNodeByName("1.2.2");
        drag("1.1.1", MouseButton.PRIMARY);
        moveTo(destination_node.getName());

        Node node = lookup(destination_node.getName()).query();
        Assert.assertTrue("drop-into style is missing from cell", node.getStyleClass().contains(FancyTreeCell.DROP_ON_STYLE_NAME));

        moveTo("1.1.1");
        Assert.assertFalse("drop-into style was not removed from cell", node.getStyleClass().contains(FancyTreeCell.DROP_ON_STYLE_NAME));

        drop(); // leave the mouse in a normal state by dropping the drag that we started.
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
        TreeItem<FancyTreeNodeFacade> root_item = FancyTreeItemBuilder.create(root_facade);

        _operations_handler = new ExampleOperationHandler(_model);
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

    private BorderPane _pane;
    private ExampleDataNode _model;
    private FancyTreeView<ExampleTreeNodeFacade> _tree;
    private ExampleOperationHandler _operations_handler;
    }


