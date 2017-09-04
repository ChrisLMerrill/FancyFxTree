# FancyFxTree
An extension of JavaFX TreeView with many advanced features already implemented. Instead of 
learning the intracacies of the TreeView APIs, simply extend a few base classes and override
the behaviors you need.  

Notably, it provides separation between the TreeView expectations of the model classes and the implementation 
of your data tree - so you don't have to design your data model around the expectations of the TreeView authors
(through a facade interface that you must implement). Included 
is the ability to change how a tree item is rendered based on dynamic changes in your application
that are not necessarily a reflection of the nodes in your tree. The JavaFX TreeView assumes that this could 
only happen if the model object changes identity.  This is designed to allow updates to the appearance 
of a cell based on external changes (possibly asynchronous).   

Enable these capabilities by implementing the FancyTreeOperationHandler:

* Act on cut/copy/paste/delete/undo keystrokes (including multiple-selections)
* Drag & drop (including multiple-selections)
* Act when user double-clicks an item
* Show a context menu for a item
* Act when selection changes

Use these FancyTree APIs to manipulate the tree:

* Expand all items
* Expand all children of an item
* Collapse all items
* Collapse all children of an item
* Expand tree items as required to make a specific item visible
* Scroll to a specific item (expanding to make it visible, if needed)
* Scroll to an item that is visible (don't scroll if not in tree's viewport)
* Select a specific item (expanding and scrolling to it if needed)
* Get paths to selected items

Additional features:

* Hovering over a node during drag will expand it (hover duration is customizable)
* Clicking nodes does not expand/collapse them...it merely selects them. The user 
must click the chevron (expander) icon to expand/collapsed (like most other tree 
implementations that are not solely for navigating a hierarchy)
* Supply your own CSS for the look of a selected tree cell and the visual effects
for drop before/on/after indicators.  

## Example

The included example demonstrates many of the capabilities described above.

![Example Screenshot](https://github.com/ChrisLMerrill/FancyFxTree/raw/master/example-screenshot.png)

## Usages

The FancyFxTree is used by the navigation view in the [MuseIDE](http://ide4selenium.com), an
IDE for web test automation using Selenium. Most of the implementation was taken from MuseIDE's
test editor...which will soon be converted to use it as well.
