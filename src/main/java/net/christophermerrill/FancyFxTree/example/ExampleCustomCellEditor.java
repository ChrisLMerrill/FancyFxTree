package net.christophermerrill.FancyFxTree.example;

import javafx.scene.*;
import javafx.scene.control.*;
import net.christophermerrill.FancyFxTree.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ExampleCustomCellEditor implements FancyTreeCellEditor
	{
	@Override
	public Node getNode()
		{
		final Label editor = new Label("custom editor");
		editor.getStyleClass().add(NODE_STYLE);
		return editor;
		}

	@Override
	public void setCell(FancyTreeCell cell)
		{

		}

    @Override
    public void cancelEdit()
        {

        }

    public final static String NODE_STYLE = "example-custom-cell-editor";
	}


