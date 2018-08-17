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
			if (!newValue && !_done) // focus lost
				{
				_done = true;
				_cell.getItem().setLabelText(_field.getText());
				}
			});
		_field.setOnKeyPressed(event ->
			{
			if (event.getCode().equals(KeyCode.ENTER))
				{
				_cell.getItem().setLabelText(_field.getText());
				_done = true;
				_cell.commitEdit(_cell.getItem());
				event.consume();
				}
			else if (event.getCode().equals(KeyCode.ESCAPE))
				{
				_done = true;
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
		_field.setText(cell.getItem().getLabelText());
		}

	private TextField _field;
	private FancyTreeCell _cell;

    @Override
    public void cancelEdit()
        {
        _done = true;
        }

    /**
	 * False until an edit has been completed. Then true to prevent further events from duplicating the commit.
	 */
	private boolean _done = false;

	final static String NODE_STYLE = "fancyfxtree-default-cell-editor";
	}