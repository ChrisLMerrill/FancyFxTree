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
	public void singleNodeSelected()
		{
		createBasicTreeAndData();

		ExampleDataNode selected_node = _model.getNodeByName("1.1.1");
		clickOn(selected_node.getName());

		Assert.assertEquals("wrong number of items was selected", 1, _operations_handler.getSelectedNodes().size());
		Assert.assertTrue("wrong item was selected", selected_node == _operations_handler.getSelectedNodes().get(0));
		}

	@Test
	public void multipleNodesSelected()
		{
		createBasicTreeAndData();

		ExampleDataNode node1 = _model.getNodeByName("1.1.1");
		clickOn(node1.getName());

		ExampleDataNode node2 = _model.getNodeByName("1.2.1");
		press(KeyCode.CONTROL);
		clickOn(node2.getName());
		release(KeyCode.CONTROL);

		final List<List<Integer>> paths = _tree.getSelectionPaths();
		Assert.assertEquals(2, paths.size());

		final List<ExampleDataNode> selections = _operations_handler.getSelectedNodes();
		Assert.assertEquals(2, selections.size());
		Assert.assertEquals(node1, selections.get(0));
		Assert.assertEquals(node2, selections.get(1));
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
	public void copyPasteByAlphaControlKeys()
		{
		tryCopyPaste(CONTROL, C, CONTROL, V);
		}

	@Test
	public void copyPasteBySpecialKeys()
		{
        tryCopyPaste(CONTROL, INSERT, SHIFT, INSERT);
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
		Assert.assertNotNull("copy not found in tree", copied_node);
		Assert.assertTrue("copy not in right parent", node_to_copy_into.contains(copied_node, true));
		Assert.assertTrue("original not found in parent", original_node_parent.contains(original_node, true));
		}

	@Test
	public void cutPasteByAlphaControlKeys()
		{
		tryCutPaste(CONTROL, X, CONTROL, V);
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

		Assert.assertNotNull("cut node not found in tree", _model.getNodeByName(original_node.getName()));
		Assert.assertTrue("not pasted into right parent", node_to_paste_into.contains(original_node, true));
		Assert.assertFalse("original is still in parent", original_node_parent.contains(original_node, true));
		}

	@Test
	public void deleteByDeleteKey()
		{
		createBasicTreeAndData();

		ExampleDataNode node_to_delete = _model.getNodeByName("1.1.1");

		clickOn(node_to_delete.getName());
		push(DELETE);

		Assert.assertFalse("node was not removed from model", _model.contains(node_to_delete));
		}

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

		Assert.assertNotNull("node not found in tree", _model.getNodeByName(target_node.getName()));
		Assert.assertTrue("the target node is not displayed", exists(target_node.getName()));

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
	public void disableDragAndDrop()
		{
		_enable_dnd = false;
		createBasicTreeAndData();

		ExampleDataNode target = _model.getNodeByName("1.1");
		ExampleDataNode destination = _model.getNodeByName("1.2");
		drag(target.getName(), MouseButton.PRIMARY);
		dropTo(destination.getName());

		Assert.assertNull("something was dragged", _operations_handler._dragged_items);
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
		moveBy(0, -destination_area.getBoundsInParent().getHeight() * 0.4d);
		drop();

		Assert.assertFalse("The target node was not removed from its parent", target_parent.contains(target_node));
		Assert.assertTrue("The target node was not moved into the destination", destination_parent.contains(target_node));
		Assert.assertTrue("The target node is not in the right place in the destination", destination_parent.getChildren().get(1).equals(target_node));
		Assert.assertTrue("the target node is not displayed", exists(target_node.getName()));
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
		Assert.assertTrue("the target node is not displayed", exists(target_node.getName()));
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
		Assert.assertNull(lookup("1.1.1").query()); // make sure it is hidden

		drag("1.2.2");
		moveTo(collapsed);
		sleep(75);
		waitForUiEvents();
		release(MouseButton.PRIMARY);

		Assert.assertNotNull(lookup("1.1.1").query());
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
		Assert.assertFalse(exists(leaf.getName()));

		_tree.expandScrollToAndSelect(leaf);
		waitForUiEvents();

		// technically, checking for exists does not verify it is visible, but at least
		// we know it was added to the node graph, which implies it was expanded and visible
		Assert.assertTrue(exists(leaf.getName()));
		}

	@Test
	public void expandScrollToAndMakeVisible()
		{
		ExampleDataNode root = ExampleDataNodeBuilder.create(new int[]{3, 3, 3, 3, 3});
		setupTree(root, false);

		final ExampleDataNode node = _model.getNodeByName("1.3.3.3.1");
		Assert.assertFalse(exists(node.getName())); // not in tree yet because it hasn't been shown

		List<TreeItem<ExampleTreeNodeFacade>> expanded = _tree.expandAndScrollTo(node);
		waitForUiEvents();
		Assert.assertTrue(exists(node.getName()));
		// check the expanded nodes were returned
		Assert.assertEquals("1.3.3.3", expanded.get(0).getValue().getLabelText());
		Assert.assertEquals("1.3.3", expanded.get(1).getValue().getLabelText());
		Assert.assertEquals("1.3", expanded.get(2).getValue().getLabelText());
		Assert.assertEquals("1", expanded.get(3).getValue().getLabelText());

		final ExampleDataNode still_hidden_node = _model.getNodeByName("1.3.3.3.1.2");
		Assert.assertFalse("expanded 1 level too far", exists(still_hidden_node.getName()));
		}

	@Test
	public void defaultStyleApplied()
		{
		createBasicTreeAndData();
		Node node = lookup("1.1").query();
		Assert.assertTrue("style is missing from TreeCell", node.getStyleClass().contains(FancyTreeCell.CELL_STYLE_NAME));
		}

	@Test
	public void dropOntoStyleApplied()
		{
		createBasicTreeAndData();

		ExampleDataNode destination_node = _model.getNodeByName("1.2.2");
		drag("1.1.1", MouseButton.PRIMARY);
		moveTo(destination_node.getName());

		Node node = lookup(destination_node.getName()).query();
		Assert.assertTrue("drop-into style is missing from cell", node.getStyleClass().contains(FancyTreeCell.DROP_ON_STYLE_NAME));

		moveTo("1.1.1");
		Assert.assertFalse("drop-into style was not removed from cell", node.getStyleClass().contains(FancyTreeCell.DROP_ON_STYLE_NAME));

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
		Assert.assertTrue("drop-before style is missing from cell", node.getStyleClass().contains(FancyTreeCell.DROP_BEFORE_STYLE_NAME));

		moveTo("1.1.1");
		Assert.assertFalse("drop-before style was not removed from cell", node.getStyleClass().contains(FancyTreeCell.DROP_BEFORE_STYLE_NAME));

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
		Assert.assertTrue("drop-after style is missing from cell", node.getStyleClass().contains(FancyTreeCell.DROP_AFTER_STYLE_NAME));

		moveTo("1.1.1");
		Assert.assertFalse("drop-after style was not removed from cell", node.getStyleClass().contains(FancyTreeCell.DROP_AFTER_STYLE_NAME));

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

		Assert.assertEquals("double-click detected on wrong node", target_node.getName(), _operations_handler.getDoubleClickedNodeName());
		}

	@Test
	public void showContextMenu()
		{
		createBasicTreeAndData();
		ExampleDataNode target_node = _model.getNodeByName("1.1");
		clickOn(target_node.getName(), MouseButton.SECONDARY);

		Assert.assertTrue("context menu not visible", exists(ExampleOperationHandler.MENU_ITEM_1));
		}

	@Test
	public void cutByContextMenu()
		{
		createBasicTreeAndData();
		ExampleDataNode target_node = _model.getNodeByName("1.1");
		Assert.assertNotNull(target_node);
		clickOn(target_node.getName(), MouseButton.SECONDARY);

		Assert.assertTrue(exists(id(FancyTreeOperationHandler.EditType.Cut.getMenuId())));
		clickOn(id(FancyTreeOperationHandler.EditType.Cut.getMenuId()));

		Assert.assertEquals(1, _operations_handler._cut_or_copied_nodes.size());
		Assert.assertEquals(_model.getNodeByName("1.1"), _operations_handler._cut_or_copied_nodes.get(0));
		}

	@Test
	public void copyAndPasteByContextMenu()
		{
		createBasicTreeAndData();
		ExampleDataNode target_node = _model.getNodeByName("1.2.1");
		Assert.assertNotNull(target_node);
		clickOn(target_node.getName(), MouseButton.SECONDARY);

		Assert.assertTrue(exists(id(FancyTreeOperationHandler.EditType.Copy.getMenuId())));
		clickOn(id(FancyTreeOperationHandler.EditType.Copy.getMenuId()));

		Assert.assertEquals(1, _operations_handler._cut_or_copied_nodes.size());
		Assert.assertEquals(_model.getNodeByName("1.2.1"), _operations_handler._cut_or_copied_nodes.get(0));

		String copy_name = ExampleDataNode.getCopyName(target_node);
		Assert.assertFalse(exists(copy_name));

		clickOn(target_node.getName(), MouseButton.SECONDARY);
		clickOn(id(FancyTreeOperationHandler.EditType.Paste.getMenuId()));
		Assert.assertTrue(exists(copy_name));
		}

	@Test
	public void displayDefaultTextEditorOnDoubleClick()
		{
		createBasicTreeAndData();
		ExampleDataNode target_node = _model.getNodeByName("1.2.1");

		Assert.assertNull(lookup("." + TextCellEditor.NODE_STYLE).query());
		doubleClickOn(target_node.getName());
		Assert.assertNotNull(lookup("." + TextCellEditor.NODE_STYLE).query());
		}

	@Test
	public void notEditable()
		{
		createBasicTreeAndData();
		_tree.setEditable(false);
		ExampleDataNode target_node = _model.getNodeByName("1.2.1");
		doubleClickOn(target_node.getName());
		Assert.assertNull(lookup("." + TextCellEditor.NODE_STYLE).query());
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
        clickOn(withStyle(TextCellEditor.NODE_STYLE)).push(KeyCode.CONTROL, KeyCode.A).write("name1").push(KeyCode.ENTER);

        waitForUiEvents();
        Assert.assertFalse(exists("1.2.1"));
        Assert.assertTrue(exists("name1"));
        Assert.assertEquals("name1", target_node.getName());

        doubleClickOn(target_node.getName());
        clickOn(withStyle(TextCellEditor.NODE_STYLE)).push(KeyCode.CONTROL, KeyCode.A).write("name2").push(KeyCode.ENTER);
        waitForUiEvents();
        Assert.assertFalse(exists("1.2.1"));
        Assert.assertFalse(exists("name1"));
        Assert.assertTrue(exists("name2"));
        Assert.assertEquals("name2", target_node.getName());
		}

	@Test
	public void editThenCancelEdit()
		{
        createBasicTreeAndData();
        ExampleDataNode target_node = _model.getNodeByName("1.2.1");
        doubleClickOn(target_node.getName());
        clickOn(withStyle(TextCellEditor.NODE_STYLE)).push(KeyCode.CONTROL, KeyCode.A).write("name1").push(KeyCode.ENTER);

        waitForUiEvents();
        Assert.assertFalse(exists("1.2.1"));
        Assert.assertTrue(exists("name1"));
        Assert.assertEquals("name1", target_node.getName());

        doubleClickOn(target_node.getName());
        clickOn(withStyle(TextCellEditor.NODE_STYLE)).push(KeyCode.CONTROL, KeyCode.A).write("name2").push(KeyCode.ESCAPE);
        waitForUiEvents();
        Assert.assertFalse(exists("1.2.1"));
        Assert.assertFalse(exists("name2"));
        Assert.assertTrue(exists("name1"));
        Assert.assertEquals("name1", target_node.getName());
		}

	@Test
	public void editNodeWithChildren()  // ensure it doesn't expand the node instead of editing
		{
		createBasicTreeAndData();
		ExampleDataNode target_node = _model.getNodeByName("1.2");

		Assert.assertNull(lookup("." + TextCellEditor.NODE_STYLE).query());
		doubleClickOn(target_node.getName());
		Assert.assertNotNull(lookup("." + TextCellEditor.NODE_STYLE).query());
		}

	private void testTextEditCompletion(KeyCode final_keystroke, boolean changed)
		{
		createBasicTreeAndData();
		ExampleDataNode target_node = _model.getNodeByName("1.2.1");
		doubleClickOn(target_node.getName());

		// don't understand why fillFieldAndTabAway() doesn't work here :(
		clickOn(withStyle(TextCellEditor.NODE_STYLE)).push(KeyCode.CONTROL, KeyCode.A).write("newname").push(final_keystroke);
		waitForUiEvents();

		Assert.assertEquals(!changed, exists("1.2.1"));
		Assert.assertEquals(changed, exists("newname"));
		Assert.assertEquals(changed ? "newname" : "1.2.1", target_node.getName());
		}

	@Test
	public void showCustomEditor()
		{
		createBasicTreeAndData();

		ExampleDataNode target_node = _model.getNodeByName("1.1.2");
		target_node._use_custom_editor = true;
		Assert.assertNull(lookup("." + ExampleCustomCellEditor.NODE_STYLE).query());
		doubleClickOn(target_node.getName());
		Assert.assertNotNull(lookup("." + ExampleCustomCellEditor.NODE_STYLE).query());
		}

	@Test
	public void deleteByContextMenu()
		{
		createBasicTreeAndData();
		ExampleDataNode target_node = _model.getNodeByName("1.1");
		Assert.assertNotNull(target_node);
		clickOn(target_node.getName(), MouseButton.SECONDARY);

		Assert.assertTrue(exists(id(FancyTreeOperationHandler.EditType.Delete.getMenuId())));
		clickOn(id(FancyTreeOperationHandler.EditType.Delete.getMenuId()));

		Assert.assertNull(_model.getNodeByName("1.1"));
		}

	@Test
	public void applyCellStyles()
	    {
	    createBasicTreeAndData();
	    final String node_to_style = "1.2.1";
	    final String style_name = "style1";

	    Node styled_node = lookup(node_to_style).query();
	    Assert.assertFalse(styled_node.getStyleClass().contains(style_name));
	    final int num_default_styles = styled_node.getStyleClass().size();
	    Assert.assertTrue("should be at least 4 styles to start with", num_default_styles >= 4);

        ExampleDataNode styled_data = _model.getNodeByName(node_to_style);
	    styled_data.addStyle(style_name);
	    waitForUiEvents();
        styled_node = lookup(node_to_style).query();
	    Assert.assertTrue("style was not added", styled_node.getStyleClass().contains(style_name));

	    styled_data.removeStyle(style_name);
	    waitForUiEvents();
	    Assert.assertFalse("style was not removed", styled_node.getStyleClass().contains(style_name));
	    Assert.assertEquals("all initial styles not present", num_default_styles, styled_node.getStyleClass().size());
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
        Assert.assertNotNull("editor not shown", lookup("." + TextCellEditor.NODE_STYLE).query());

        target_node.setName("newnamefor1.1.2");
        waitForUiEvents();
        Assert.assertFalse("new name shown in tree", exists("newnamefor1.1.2"));
	    Assert.assertNotNull("editor disappeared", lookup("." + TextCellEditor.NODE_STYLE).query());
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
	    Assert.assertNotNull(node_again);  // the node became invisible
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
	private boolean _enable_dnd = true;
	private long _hover_duration = FancyTreeView.DEFAULT_HOVER_EXPAND_DURATION;
	}


