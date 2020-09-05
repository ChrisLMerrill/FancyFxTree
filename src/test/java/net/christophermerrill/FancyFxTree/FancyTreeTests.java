package net.christophermerrill.FancyFxTree;

import javafx.application.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import net.christophermerrill.FancyFxTree.example.*;
import net.christophermerrill.testfx.*;
import org.junit.jupiter.api.*;
import org.testfx.api.*;

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
	public void singleNodeSelected()
		{
		createBasicTreeAndData();

		ExampleDataNode selected_node = _model.getNodeByName("1.1.1");
		clickOn(selected_node.getName());

		Assertions.assertEquals(1, _operations_handler.getSelectedNodes().size(), "wrong number of items was selected");
        Assertions.assertSame(selected_node, _operations_handler.getSelectedNodes().get(0), "wrong item was selected");
		}

	@Test
	public void multipleNodesSelected()
		{
		createBasicTreeAndData();

		ExampleDataNode node1 = _model.getNodeByName("1.1.1");
		clickOn(node1.getName());

		ExampleDataNode node2 = _model.getNodeByName("1.2.1");
		press(SHORTCUT);
		clickOn(node2.getName());
		release(SHORTCUT);

		final List<List<Integer>> paths = _tree.getSelectionPaths();
		Assertions.assertEquals(2, paths.size());

		final List<ExampleDataNode> selections = _operations_handler.getSelectedNodes();
		Assertions.assertEquals(2, selections.size());
		Assertions.assertEquals(node1, selections.get(0));
		Assertions.assertEquals(node2, selections.get(1));
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

		Assertions.assertFalse(exists(old_label), "old label is still visible");
		Assertions.assertTrue(exists(new_label), "new label is not visible");
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

		Assertions.assertTrue(exists(new_child.getName()), "new child is not visible");
		}

	private void addNodeAndVerifyDisplayed(ExampleDataNode node)
		{
		final String new_node_label = node.getName() + "-new" + node.getChildren().size();
		ExampleDataNode new_node = new ExampleDataNode(new_node_label);
		node.addChild(new_node);
		waitForUiEvents();

		Assertions.assertTrue(exists(new_node_label), "new node is not visible");
		}

	private void insertNodeAndVerifyDisplayed(ExampleDataNode parent, ExampleDataNode new_node)
		{
		int index = parent.getChildren().size() > 0 ? 1 : 0;
		parent.insertChild(new_node, index);
		waitForUiEvents();

		_tree.expandToMakeVisible(new_node);
		waitForUiEvents();

		Assertions.assertTrue(exists(new_node.getName()), "new node is not visible");
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
		Assertions.assertFalse(exists(to_remove.getName()), "removed node is still visible");
		}

	@Test
	public void copyPasteByAlphaControlKeys()
		{
		tryCopyPaste(SHORTCUT, C, SHORTCUT, V);
		}

	@Test
	public void copyPasteBySpecialKeys()
		{
        tryCopyPaste(SHORTCUT, INSERT, SHIFT, INSERT);
		}

	private void tryCopyPaste(KeyCode copy_modifier, KeyCode copy_key, KeyCode paste_modifier, KeyCode paste_key)
		{
		createBasicTreeAndData();

		ExampleDataNode original_node = _model.getNodeByName("1.1.1");
		ExampleDataNode original_node_parent = _model.findParentFor(original_node);
		ExampleDataNode node_to_copy_after = _model.getNodeByName("1.2.2");
		ExampleDataNode node_to_copy_into = _model.getNodeByName("1.2");
		String copy_name = ExampleDataNode.getCopyName(original_node);

		clickOn(original_node.getName());
		push(copy_modifier, copy_key); // copy
		clickOn(node_to_copy_after.getName());
		push(paste_modifier, paste_key); // paste

		ExampleDataNode copied_node = _model.getNodeByName(copy_name);
		Assertions.assertNotNull(copied_node, "copy not found in tree");
		Assertions.assertTrue(node_to_copy_into.contains(copied_node, true), "copy not in right parent");
		Assertions.assertTrue(original_node_parent.contains(original_node, true), "original not found in parent");
		}

	@Test
	public void cutPasteByAlphaControlKeys()
		{
		tryCutPaste(SHORTCUT, X, SHORTCUT, V);
		}

	@Test
	public void cutPasteBySpecialKeys()
		{
//        Test fails due to: https://github.com/TestFX/TestFX/issues/310
//        tryCutPaste(SHIFT, DELETE, CONTROL, INSERT);
		}

	private void tryCutPaste(KeyCode copy_modifier, KeyCode copy_key, KeyCode paste_modifier, KeyCode paste_key)
		{
		createBasicTreeAndData();

		ExampleDataNode original_node = _model.getNodeByName("1.1.1");
		ExampleDataNode original_node_parent = _model.findParentFor(original_node);
		ExampleDataNode node_to_paste_after = _model.getNodeByName("1.2.2");
		ExampleDataNode node_to_paste_into = _model.getNodeByName("1.2");

		clickOn(original_node.getName());
		push(copy_modifier, copy_key); // copy
		clickOn(node_to_paste_after.getName());
		push(paste_modifier, paste_key); // paste

		Assertions.assertNotNull(_model.getNodeByName(original_node.getName()), "cut node not found in tree");
		Assertions.assertTrue(node_to_paste_into.contains(original_node, true), "not pasted into right parent");
		Assertions.assertFalse(original_node_parent.contains(original_node, true), "original is still in parent");
		}

	@Test
	public void deleteByDeleteKey()
		{
		createBasicTreeAndData();

		ExampleDataNode node_to_delete = _model.getNodeByName("1.1.1");

		clickOn(node_to_delete.getName());
		push(DELETE);

		Assertions.assertFalse(_model.contains(node_to_delete), "node was not removed from model");
		}

	@Test
	public void moveByDragInto()
		{
		createBasicTreeAndData();

		ExampleDataNode target_parent = _model.getNodeByName("1.1");
		ExampleDataNode target_node = _model.getNodeByName("1.1.1");
		ExampleDataNode destination_node = _model.getNodeByName("1.2.2");

        Assertions.assertEquals(2, target_parent.getChildren().size(), "expected root to start with 2 children");
        Assertions.assertEquals(0, destination_node.getChildren().size(), "expected destination node to start with no children");

		drag(target_node.getName(), MouseButton.PRIMARY).dropTo(destination_node.getName());
		waitForUiEvents();

		Assertions.assertNotNull(_model.getNodeByName(target_node.getName()), "node not found in tree");
		Assertions.assertTrue(exists(target_node.getName()), "the target node is not displayed");

        Assertions.assertEquals(1, target_parent.getChildren().size(), "node was not removed from root");
        Assertions.assertEquals(1, destination_node.getChildren().size(), "node was not added to destination");
		}

    @Test
    public void dragOntoSelfDenied()
        {
        createBasicTreeAndData();

        ExampleDataNode target_node = _model.getNodeByName("1.1.1");
        FxRobot robot = drag(target_node.getName(), MouseButton.PRIMARY);
        robot.moveTo("1.2.2");
        robot.dropTo(target_node.getName());

        Assertions.assertNull(_operations_handler._dropped_content, "the drop should have been denied");
        }

    @Test
    public void dragOntoChildDenied()
        {
        createBasicTreeAndData();

        ExampleDataNode target_node = _model.getNodeByName("1");
        FxRobot robot = drag(target_node.getName(), MouseButton.PRIMARY);
        robot.moveTo("1.2.1");
        robot.dropTo("1.1");

        Assertions.assertNull(_operations_handler._dropped_content, "the drop should have been denied");
        }

    @Test
    public void dragOntoDescendentDenied()
        {
        createBasicTreeAndData();

        ExampleDataNode target_node = _model.getNodeByName("1");
        FxRobot robot = drag(target_node.getName(), MouseButton.PRIMARY);
        robot.moveTo("1.2.1");
        robot.dropTo("1.1.1");

        Assertions.assertNull(_operations_handler._dropped_content, "the drop should have been denied");
        }

    @Test
	public void copyByDragInto()
		{
		createBasicTreeAndData();

		ExampleDataNode target_parent = _model.getNodeByName("1.1");
		ExampleDataNode target_node = _model.getNodeByName("1.1.1");
		ExampleDataNode destination_node = _model.getNodeByName("1.2.2");

        Assertions.assertEquals(2, target_parent.getChildren().size(), "expected root to start with 2 children");
        Assertions.assertEquals(0, destination_node.getChildren().size(), "expected destination node to start with no children");

		drag(target_node.getName(), MouseButton.PRIMARY);
		press(SHORTCUT);
		dropTo(destination_node.getName());
		release(SHORTCUT);
		waitForUiEvents();

		Assertions.assertNotNull(_model.getNodeByName(ExampleDataNode.getCopyName(target_node)), "copy not found");

        Assertions.assertNotEquals(1, target_parent.getChildren().size(), "node was removed from root");
        Assertions.assertEquals(1, destination_node.getChildren().size(), "node was not added to destination");
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

		Assertions.assertEquals(2, _operations_handler._drag_count, "2 items should be dragged");
		Assertions.assertTrue(dropListContains(_operations_handler._dropped_nodes, target1), "first item was not dropped");
		Assertions.assertTrue(dropListContains(_operations_handler._dropped_nodes, target2), "second item was not dropped");

		Assertions.assertTrue(destination.contains(target1));
		Assertions.assertTrue(destination.contains(target2));
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
		press(SHORTCUT);
		drag(target_node_2.getName(), MouseButton.PRIMARY);
		dropTo(destination.getName());
		release(SHORTCUT);
		waitForUiEvents();

		Assertions.assertEquals(2, _operations_handler._drag_count, "2 items should be dragged");
		Assertions.assertTrue(dragListContains(_operations_handler._dragged_items, _model.getNodeByName("1.1.1")), "first item was not dragged");
		Assertions.assertTrue(dragListContains(_operations_handler._dragged_items, _model.getNodeByName("1.1.2")), "second item was not dragged");

		Assertions.assertTrue(target_parent.contains(target_node_1), "orignal #1 was removed");
		Assertions.assertTrue(target_parent.contains(target_node_2), "orignal #2 was removed");

		ExampleDataNode copy_1 = _model.getNodeByName(ExampleDataNode.getCopyName(target_node_1));
		Assertions.assertNotNull(copy_1, "1st node was not copied");
		ExampleDataNode copy_2 = _model.getNodeByName(ExampleDataNode.getCopyName(target_node_2));
		Assertions.assertNotNull(copy_2, "2nd node was not copied");
		Assertions.assertTrue(destination.contains(copy_1), "1st copy is not in destination");
		Assertions.assertTrue(destination.contains(copy_2), "2nd copy is not in destination");
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
	public void disableDragAndDrop()
		{
		_enable_dnd = false;
		createBasicTreeAndData();

		ExampleDataNode target = _model.getNodeByName("1.1");
		ExampleDataNode destination = _model.getNodeByName("1.2");
		drag(target.getName(), MouseButton.PRIMARY);
		dropTo(destination.getName());

		Assertions.assertNull(_operations_handler._dragged_items, "something was dragged");
		}

	@Test
	public void dragAndDropIntoDenied()
		{
		createBasicTreeAndData();

		drag("1.1", MouseButton.PRIMARY);
		dropTo("1.2.2");

        Assertions.assertNull(_operations_handler._dropped_content, "the drop should have been denied");
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
		moveBy(0, -destination_area.getBoundsInParent().getHeight() * 0.4d);
		drop();

		Assertions.assertFalse(target_parent.contains(target_node), "The target node was not removed from its parent");
		Assertions.assertTrue(destination_parent.contains(target_node), "The target node was not moved into the destination");
        Assertions.assertEquals(destination_parent.getChildren().get(1), target_node, "The target node is not in the right place in the destination");
		Assertions.assertTrue(exists(target_node.getName()), "the target node is not displayed");
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

		Assertions.assertFalse(target_parent.contains(target_node), "The target node was not removed from its parent");
		Assertions.assertTrue(destination_parent.contains(target_node), "The target node was not moved into the destination");
        Assertions.assertEquals(destination_parent.getChildren().get(1), target_node, "The target node is not in the right place in the destination");
		Assertions.assertTrue(exists(target_node.getName()), "the target node is not displayed");
		}

	@Test
	public void expandOnHover()
		{
		_hover_duration = 50;
		createBasicTreeAndData();

		TreeItem item = _tree.getTreeItem(1);
		item.setExpanded(false);
		waitForUiEvents();

		Node collapsed = lookup("1.1").query();
		Assertions.assertFalse(exists("1.1.1")); // make sure it is hidden

		drag("1.2.2");
		moveTo(collapsed);
		sleep(75);
		waitForUiEvents();
		release(MouseButton.PRIMARY);

		Assertions.assertTrue(exists("1.1.1"));
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
		ExampleDataNode root = ExampleDataNodeBuilder.create(new int[]{2, 2, 3, 2, 1});
		setupTree(root);

		ExampleDataNode last_node = root.getChildren().get(1)
			.getChildren().get(1)
			.getChildren().get(1)
			.getChildren().get(1)
			.getChildren().get(0);

		_tree.scrollToAndMakeVisible(last_node);
		waitForUiEvents();

		// there is currently no way to positively determine if the item is visible on-screen.
		// https://groups.google.com/forum/#!topic/testfx-discuss/R0WM_TaloDI
		//
		// This test is left here for manual use and documentation of the issue.
		// To test manually, set a breakpoint on the next line and visually verify
		// that the view is scrolled to the bottom of the tree (1.2.2.2.2.2)
		System.out.println("is node 1.2.2.2.2.2 visible?");
		}

	@Test
	public void expandScrollToAndSelectNewItem()
		{
		ExampleDataNode root = ExampleDataNodeBuilder.create(new int[]{3, 3, 3, 3, 3});
		setupTree(root, false);
		waitForUiEvents();

		ExampleDataNode leaf = ExampleDataNodeBuilder.createRandomLeaf(root);
		waitForUiEvents();

		// won't exist in tree if it wasn't shown initially
		Assertions.assertFalse(exists(leaf.getName()));

		_tree.expandScrollToAndSelect(leaf);
		waitForUiEvents();

		// technically, checking for exists does not verify it is visible, but at least
		// we know it was added to the node graph, which implies it was expanded and visible
		Assertions.assertTrue(exists(leaf.getName()));
		}

	@Test
	public void expandScrollToAndMakeVisible()
		{
		ExampleDataNode root = ExampleDataNodeBuilder.create(new int[]{3, 3, 3, 3, 3});
		setupTree(root, false);

		final ExampleDataNode node = _model.getNodeByName("1.3.3.3.1");
		Assertions.assertFalse(exists(node.getName())); // not in tree yet because it hasn't been shown

		List<TreeItem<ExampleTreeNodeFacade>> expanded = _tree.expandAndScrollTo(node);
		waitForUiEvents();
		Assertions.assertTrue(exists(node.getName()));
		// check the expanded nodes were returned
		Assertions.assertEquals("1.3.3.3", expanded.get(0).getValue().getLabelText());
		Assertions.assertEquals("1.3.3", expanded.get(1).getValue().getLabelText());
		Assertions.assertEquals("1.3", expanded.get(2).getValue().getLabelText());
		Assertions.assertEquals("1", expanded.get(3).getValue().getLabelText());

		final ExampleDataNode still_hidden_node = _model.getNodeByName("1.3.3.3.1.2");
		Assertions.assertFalse(exists(still_hidden_node.getName()), "expanded 1 level too far");
		}

	@Test
	public void defaultStyleApplied()
		{
		createBasicTreeAndData();
		Node node = lookup("1.1").query();
		Assertions.assertTrue(node.getStyleClass().contains(FancyTreeCell.CELL_STYLE_NAME), "style is missing from TreeCell");
		}

	@Test
	public void dropOntoStyleApplied()
		{
		createBasicTreeAndData();

		ExampleDataNode destination_node = _model.getNodeByName("1.2.2");
		drag("1.1.1", MouseButton.PRIMARY);
		moveTo(destination_node.getName());

		Node node = lookup(destination_node.getName()).query();
		Assertions.assertTrue(node.getStyleClass().contains(FancyTreeCell.DROP_ON_STYLE_NAME), "drop-into style is missing from cell");

		moveTo("1.1.1");
		Assertions.assertFalse(node.getStyleClass().contains(FancyTreeCell.DROP_ON_STYLE_NAME), "drop-into style was not removed from cell");

		// leave the mouse in a normal state by dropping the drag that we started. If this doesn't happen, it can affect the next test
		drop();
		clickOn("1.1");
		}

	@Test
	public void dropBeforeStyleApplied()
		{
		createBasicTreeAndData();

		ExampleDataNode destination_node = _model.getNodeByName("1.2.2");
		Node destination_area = lookup(destination_node.getName()).query();
		drag("1.1.1", MouseButton.PRIMARY);
		moveTo(destination_node.getName());
		moveBy(0, -destination_area.getBoundsInParent().getHeight() * 0.4d);

		Node node = lookup(destination_node.getName()).query();
		Assertions.assertTrue(node.getStyleClass().contains(FancyTreeCell.DROP_BEFORE_STYLE_NAME), "drop-before style is missing from cell");

		moveTo("1.1.1");
		Assertions.assertFalse(node.getStyleClass().contains(FancyTreeCell.DROP_BEFORE_STYLE_NAME), "drop-before style was not removed from cell");

		// leave the mouse in a normal state by dropping the drag that we started. If this doesn't happen, it can affect the next test
		drop();
		clickOn("1.1");
		}

	@Test
	public void dropAfterStyleApplied()
		{
		createBasicTreeAndData();

		ExampleDataNode destination_node = _model.getNodeByName("1.2.2");
		Node destination_area = lookup(destination_node.getName()).query();
		drag("1.1.1", MouseButton.PRIMARY);
		moveTo(destination_node.getName());
		moveBy(0, destination_area.getBoundsInParent().getHeight() * 0.4d);

		Node node = lookup(destination_node.getName()).query();
		Assertions.assertTrue(node.getStyleClass().contains(FancyTreeCell.DROP_AFTER_STYLE_NAME), "drop-after style is missing from cell");

		moveTo("1.1.1");
		Assertions.assertFalse(node.getStyleClass().contains(FancyTreeCell.DROP_AFTER_STYLE_NAME), "drop-after style was not removed from cell");

		// leave the mouse in a normal state by dropping the drag that we started. If this doesn't happen, it can affect the next test
		drop();
		clickOn("1.1");
		}

	@Test
	public void doubleClick()
		{
		createBasicTreeAndData();
		ExampleDataNode target_node = _model.getNodeByName("1.2.1");
		doubleClickOn(target_node.getName());

		Assertions.assertEquals("double-click detected on wrong node", target_node.getName(), _operations_handler.getDoubleClickedNodeName());
		}

	@Test
	public void showContextMenu()
		{
		createBasicTreeAndData();
		ExampleDataNode target_node = _model.getNodeByName("1.1");
		clickOn(target_node.getName(), MouseButton.SECONDARY);

		Assertions.assertTrue(exists(ExampleOperationHandler.MENU_ITEM_1), "context menu not visible");
		}

	@Test
	public void cutByContextMenu()
		{
		createBasicTreeAndData();
		ExampleDataNode target_node = _model.getNodeByName("1.1");
		Assertions.assertNotNull(target_node);
		clickOn(target_node.getName(), MouseButton.SECONDARY);

		Assertions.assertTrue(exists(id(FancyTreeOperationHandler.EditType.Cut.getMenuId())));
		clickOn(id(FancyTreeOperationHandler.EditType.Cut.getMenuId()));

		Assertions.assertEquals(1, _operations_handler._cut_or_copied_nodes.size());
		Assertions.assertEquals(_model.getNodeByName("1.1"), _operations_handler._cut_or_copied_nodes.get(0));
		}

	@Test
	public void copyAndPasteByContextMenu()
		{
		createBasicTreeAndData();
		ExampleDataNode target_node = _model.getNodeByName("1.2.1");
		Assertions.assertNotNull(target_node);
		clickOn(target_node.getName(), MouseButton.SECONDARY);

		Assertions.assertTrue(exists(id(FancyTreeOperationHandler.EditType.Copy.getMenuId())));
		clickOn(id(FancyTreeOperationHandler.EditType.Copy.getMenuId()));

		Assertions.assertEquals(1, _operations_handler._cut_or_copied_nodes.size());
		Assertions.assertEquals(_model.getNodeByName("1.2.1"), _operations_handler._cut_or_copied_nodes.get(0));

		String copy_name = ExampleDataNode.getCopyName(target_node);
		Assertions.assertFalse(exists(copy_name));

		clickOn(target_node.getName(), MouseButton.SECONDARY);
		clickOn(id(FancyTreeOperationHandler.EditType.Paste.getMenuId()));
		Assertions.assertTrue(exists(copy_name));
		}

	@Test
	public void displayDefaultTextEditorOnDoubleClick()
		{
		createBasicTreeAndData();
		ExampleDataNode target_node = _model.getNodeByName("1.2.1");

		Assertions.assertFalse(exists("." + TextCellEditor.NODE_STYLE));
		doubleClickOn(target_node.getName());
		Assertions.assertTrue(exists("." + TextCellEditor.NODE_STYLE));
		}

	@Test
	public void notEditable()
		{
		createBasicTreeAndData();
		_tree.setEditable(false);
		ExampleDataNode target_node = _model.getNodeByName("1.2.1");
		doubleClickOn(target_node.getName());
		Assertions.assertFalse(exists("." + TextCellEditor.NODE_STYLE));
		}

	@Test
	public void editTextCompletedByTab()
		{
		testTextEditCompletion(KeyCode.TAB, true);
		}

	@Test
	public void cancelTextEdit()
		{
		testTextEditCompletion(KeyCode.ESCAPE, false);
		}

	@Test
	public void editTextCompletedByEnter()
		{
		testTextEditCompletion(KeyCode.ENTER, true);
		}

	@Test
	public void editTwice()
		{
        createBasicTreeAndData();
        ExampleDataNode target_node = _model.getNodeByName("1.2.1");
        doubleClickOn(target_node.getName());
        clickOn(withStyle(TextCellEditor.NODE_STYLE)).push(SHORTCUT, KeyCode.A).write("name1").push(KeyCode.ENTER);

        waitForUiEvents();
        Assertions.assertFalse(exists("1.2.1"));
        Assertions.assertTrue(exists("name1"));
        Assertions.assertEquals("name1", target_node.getName());

        doubleClickOn(target_node.getName());
        clickOn(withStyle(TextCellEditor.NODE_STYLE)).push(SHORTCUT, KeyCode.A).write("name2").push(KeyCode.ENTER);
        waitForUiEvents();
        Assertions.assertFalse(exists("1.2.1"));
        Assertions.assertFalse(exists("name1"));
        Assertions.assertTrue(exists("name2"));
        Assertions.assertEquals("name2", target_node.getName());
		}

	@Test
	public void editThenCancelEdit()
		{
        createBasicTreeAndData();
        ExampleDataNode target_node = _model.getNodeByName("1.2.1");
        doubleClickOn(target_node.getName());
        clickOn(withStyle(TextCellEditor.NODE_STYLE)).push(SHORTCUT, KeyCode.A).write("name1").push(KeyCode.ENTER);

        waitForUiEvents();
        Assertions.assertFalse(exists("1.2.1"));
        Assertions.assertTrue(exists("name1"));
        Assertions.assertEquals("name1", target_node.getName());

        doubleClickOn(target_node.getName());
        clickOn(withStyle(TextCellEditor.NODE_STYLE)).push(SHORTCUT, KeyCode.A).write("name2").push(KeyCode.ESCAPE);
        waitForUiEvents();
        Assertions.assertFalse(exists("1.2.1"));
        Assertions.assertFalse(exists("name2"));
        Assertions.assertTrue(exists("name1"));
        Assertions.assertEquals("name1", target_node.getName());
		}

	@Test
	public void editNodeWithChildren()  // ensure it doesn't expand the node instead of editing
		{
		createBasicTreeAndData();
		ExampleDataNode target_node = _model.getNodeByName("1.2");

		Assertions.assertFalse(exists("." + TextCellEditor.NODE_STYLE));
		doubleClickOn(target_node.getName());
		Assertions.assertTrue(exists("." + TextCellEditor.NODE_STYLE));
		}

	private void testTextEditCompletion(KeyCode final_keystroke, boolean changed)
		{
		createBasicTreeAndData();
		ExampleDataNode target_node = _model.getNodeByName("1.2.1");
		doubleClickOn(target_node.getName());

		// don't understand why fillFieldAndTabAway() doesn't work here :(
		clickOn(withStyle(TextCellEditor.NODE_STYLE)).push(SHORTCUT, KeyCode.A).write("newname").push(final_keystroke);
		waitForUiEvents();

		Assertions.assertEquals(!changed, exists("1.2.1"));
		Assertions.assertEquals(changed, exists("newname"));
		Assertions.assertEquals(changed ? "newname" : "1.2.1", target_node.getName());
		}

	@Test
	public void showCustomEditor()
		{
		createBasicTreeAndData();

		ExampleDataNode target_node = _model.getNodeByName("1.1.2");
		target_node._use_custom_editor = true;
		Assertions.assertFalse(exists("." + ExampleCustomCellEditor.NODE_STYLE));
		doubleClickOn(target_node.getName());
		Assertions.assertTrue(exists("." + ExampleCustomCellEditor.NODE_STYLE));
		}

	@Test
	public void deleteByContextMenu()
		{
		createBasicTreeAndData();
		ExampleDataNode target_node = _model.getNodeByName("1.1");
		Assertions.assertNotNull(target_node);
		clickOn(target_node.getName(), MouseButton.SECONDARY);

		Assertions.assertTrue(exists(id(FancyTreeOperationHandler.EditType.Delete.getMenuId())));
		clickOn(id(FancyTreeOperationHandler.EditType.Delete.getMenuId()));

		Assertions.assertNull(_model.getNodeByName("1.1"));
		}

	@Test
	public void applyCellStyles()
	    {
	    createBasicTreeAndData();
	    final String node_to_style = "1.2.1";
	    final String style_name = "style1";

	    Node styled_node = lookup(node_to_style).query();
	    Assertions.assertFalse(styled_node.getStyleClass().contains(style_name));
	    final int num_default_styles = styled_node.getStyleClass().size();
	    Assertions.assertTrue(num_default_styles >= 4, "should be at least 4 styles to start with");

        ExampleDataNode styled_data = _model.getNodeByName(node_to_style);
	    styled_data.addStyle(style_name);
	    waitForUiEvents();
        styled_node = lookup(node_to_style).query();
	    Assertions.assertTrue(styled_node.getStyleClass().contains(style_name), "style was not added");

	    styled_data.removeStyle(style_name);
	    waitForUiEvents();
	    Assertions.assertFalse(styled_node.getStyleClass().contains(style_name), "style was not removed");
	    Assertions.assertEquals(num_default_styles, styled_node.getStyleClass().size(), "all initial styles not present");
	    }

	/**
	 * While editing, changes to the node should be ignored (rather than updating
	 * the cell UI...which would close the editor).
	 */
	@Test
	public void delayUpdatesWhileEditing()
	    {
	    createBasicTreeAndData();
	    ExampleDataNode target_node = _model.getNodeByName("1.1.2");
        doubleClickOn(target_node.getName());
        lookup("." + TextCellEditor.NODE_STYLE).query();

        target_node.setName("newnamefor1.1.2");
        waitForUiEvents();
        Assertions.assertFalse(exists("newnamefor1.1.2"), "new name shown in tree");
        lookup("." + TextCellEditor.NODE_STYLE).query();
        }

	@Test
	public void doubleClickCollapseExpandBug()
	    {
	    createBasicTreeAndData();

	    ExampleDataNode target_node = _model.getNodeByName("1.1");
        doubleClickOn(target_node.getName());

        final Node node = lookup(target_node.getName()).query();
	    final Bounds bounds = bounds(node).query();
        final Point2D chevron = new Point2D(bounds.getMinX() - 10, bounds.getMinY() + (bounds.getMaxY() - bounds.getMinY())/2);

//        type(KeyCode.ESCAPE);  // this fixes the bug
//        waitForUiEvents();

        clickOn(chevron);
	    waitForUiEvents();

	    final Node node_again = lookup(target_node.getName()).query();
	    Assertions.assertNotNull(node_again);  // the node became invisible
	    }

	private void createBasicTreeAndData()
		{
		ExampleDataNode root = ExampleDataNodeBuilder.create(new int[]{2, 2});
		setupTree(root);
		}

	private void setupTree(ExampleDataNode root)
		{
		setupTree(root, true);
		}

	private void setupTree(ExampleDataNode root, boolean expand_all)
		{
		_model = root;
		ExampleTreeNodeFacade root_facade = new ExampleTreeNodeFacade(_model);
		TreeItem<FancyTreeNodeFacade> root_item = FancyTreeItemBuilder.create(root_facade);

		_operations_handler = new ExampleOperationHandler(_model);
		_tree = new FancyTreeView(_operations_handler, _enable_dnd);
		_tree.setHoverExpandDuration(_hover_duration);
		_tree.setRoot(root_item);
		_tree.setEditable(true);
		if (expand_all)
			_tree.expandAll();

		Platform.runLater(() -> _pane.setCenter(_tree));
		waitForUiEvents();
		}

	private void checkNodesVisible122()
		{
		Assertions.assertTrue(exists("1"), "The root node (1) is not visible");
		Assertions.assertTrue(exists("1.1"), "node 1.1 is not visible");
		Assertions.assertTrue(exists("1.1.1"), "node 1.1.1 is not visible");
		Assertions.assertTrue(exists("1.1.2"), "node 1.1.2 is not visible");
		Assertions.assertTrue(exists("1.2"), "node 1.2 is not visible");
		Assertions.assertTrue(exists("1.2.1"), "node 1.2.1 is not visible");
		Assertions.assertTrue(exists("1.2.2"), "node 1.2.2 is not visible");
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
	private boolean _enable_dnd = true;
	private long _hover_duration = FancyTreeView.DEFAULT_HOVER_EXPAND_DURATION;
	}