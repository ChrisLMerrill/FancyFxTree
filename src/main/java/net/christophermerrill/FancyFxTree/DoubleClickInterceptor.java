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
public class DoubleClickInterceptor implements EventDispatcher
    {
    public DoubleClickInterceptor(TreeCell cell, EventHandler<MouseEvent> double_click_listener)
        {
        _original_dispatcher = cell.getEventDispatcher();
        cell.setEventDispatcher(this);
        _double_click_listener = double_click_listener;
        }

    @Override
    public Event dispatchEvent(Event event, EventDispatchChain tail)
        {
        if (event instanceof MouseEvent)
            {
            MouseEvent mouse_event = (MouseEvent) event;
            if (mouse_event.getEventType().equals(MouseEvent.MOUSE_PRESSED)
                && mouse_event.getButton() == MouseButton.PRIMARY
                && mouse_event.getClickCount() >= 2
                && !event.isConsumed())
                {
                _double_click_listener.handle(mouse_event);
                event.consume();
                }
            }
        return _original_dispatcher.dispatchEvent(event, tail);
        }

    private final EventDispatcher _original_dispatcher;
    private final EventHandler<MouseEvent> _double_click_listener;
    }


