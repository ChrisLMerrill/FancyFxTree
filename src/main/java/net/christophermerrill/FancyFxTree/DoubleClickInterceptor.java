package net.christophermerrill.FancyFxTree;

import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.input.*;

/**
 * Intercepts double-click events BEFORE the tree can use it for expand/collapsing of nodes. Provided
 * with a MouseListener, this allows the double-click event to be used for editing the node, rather than
 * expand/collapse.
 *
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
class DoubleClickInterceptor
    {
    DoubleClickInterceptor(TreeCell cell, EventHandler<MouseEvent> double_click_listener)
        {
        _double_click_listener = double_click_listener;
        cell.addEventHandler(MouseEvent.ANY, event ->
	        {
	        if (event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY))
		        {
		        if (event.getEventType().equals(MouseEvent.MOUSE_CLICKED))
		        	_double_click_listener.handle(event);
	            event.consume();
		        }
	        });
        }

    private final EventHandler<MouseEvent> _double_click_listener;
    }


