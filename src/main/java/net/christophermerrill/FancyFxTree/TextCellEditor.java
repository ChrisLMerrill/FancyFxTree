package net.christophermerrill.FancyFxTree;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TextCellEditor implements FancyTreeCellEditor
	{
	TextCellEditor()
		{
		_field = new TextField("value1");
		_field.getStyleClass().add(NODE_STYLE);
		_field.focusedProperty().addListener((observable, oldValue, newValue) ->
			{
			if (!newValue && !_cancelled) // focus lost
				_cell.getItem().textEdited(_field.getText());
			});
		_field.setOnKeyPressed(event ->
			{
			if (event.getCode().equals(KeyCode.ENTER))
				{
				_cell.getItem().textEdited(_field.getText());
				event.consume();
				}
			else if (event.getCode().equals(KeyCode.ESCAPE))
				{
				_cancelled = true;
				_cell.cancelEdit();
				event.consume();
				}
			});
		}

	@Override
	public Node getNode()
		{
		return _field;
		}

	@Override
	public void setCell(FancyTreeCell cell)
		{
		_cell = cell;
		}

	private TextField _field;
	private FancyTreeCell _cell;
	private boolean _cancelled = false;

	final static String NODE_STYLE = "fancyfxtree-example-editor";
	}