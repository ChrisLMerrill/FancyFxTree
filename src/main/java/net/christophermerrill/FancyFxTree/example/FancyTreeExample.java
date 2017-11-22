package net.christophermerrill.FancyFxTree.example;

import javafx.application.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import net.christophermerrill.FancyFxTree.*;

import java.net.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class FancyTreeExample extends Application
    {
    public static void main(String[] args)
        {
        launch(args);
        }

    @Override
    public void start(Stage stage) throws Exception
        {
        stage.setTitle(getClass().getSimpleName());
        BorderPane root = new BorderPane();

        FlowPane button_bar = new FlowPane();
        button_bar.getChildren().add(createNodeChangeButton());
        _add_node_button = createAddNodeButton();
        button_bar.getChildren().add(_add_node_button);
        button_bar.getChildren().add(createAddLeafNodeButton());
        button_bar.getChildren().add(createStyleNodeButton());
        button_bar.getChildren().add(createExpandAllButton());
        button_bar.getChildren().add(createCollapseAllButton());
        root.setTop(button_bar);

        _model_root = ExampleDataNodeBuilder.create(new int[] {4,1,3,2});
        _tree = new FancyTreeView(new ExampleOperationHandler(_model_root)
            {
            @Override
            public void selectionChanged(ObservableList<TreeItem<ExampleTreeNodeFacade>> selected_items)
                {
                super.selectionChanged(selected_items);
                _status.setText(String.format("%d items selected", selected_items.size()));
                _add_node_button.setDisable(selected_items.size() != 1);
                }
            });
        _tree.setRoot(new ExampleTreeNodeFacade(_model_root));
        _tree.expandAll();
        _tree.setEditable(true);
        root.setCenter(_tree);

        _status = new Label();
        root.setBottom(_status);

        URL resource = getClass().getResource("FancyTreeExample.css");
        _tree.getStylesheets().add(resource.toExternalForm());

        stage.setScene(new Scene(root, 300, 250));
        stage.show();
        }

    private Button createNodeChangeButton()
        {
        Button button = new Button();
        button.setText("Change a node");
        button.setOnAction(event ->
            {
            ExampleDataNode node = _model_root.pickRandom();
            if (node.getExtraData() == null)
                node.setExtraData("modified");
            else
                node.setExtraData(null);
            });
        return button;
        }

    private Button createAddNodeButton()
        {
        Button button = new Button();
        button.setText("Add a node");
        button.setOnAction(event ->
            {
            MultipleSelectionModel<TreeItem<ExampleTreeNodeFacade>> selectionModel = _tree.getSelectionModel();
            ExampleDataNode node = selectionModel.getSelectedItem().getValue().getModelNode();
            ExampleDataNode parent = _model_root.findParentFor(node);
            parent.addAfter(new ExampleDataNode("after " + node.getName()), node);
            });
        button.setDisable(true);
        return button;
        }

    private Button createAddLeafNodeButton()
        {
        Button button = new Button();
        button.setText("Add a leaf");
        button.setOnAction(event ->
            {
            ExampleDataNode leaf = ExampleDataNodeBuilder.createRandomLeaf(_model_root);
            _tree.expandScrollToAndSelect(leaf);
            });
        return button;
        }

    private Button createStyleNodeButton()
        {
        Button button = new Button();
        button.setText("Style the node");
        button.setOnAction(event ->
	        Platform.runLater(() ->
		        {
		        final ObservableList items = _tree.getSelectionModel().getSelectedItems();
		        for (Object item : items)
			        {
			        ExampleTreeNodeFacade node = ((TreeItem<ExampleTreeNodeFacade>) item).getValue();
			        ExampleDataNode data = node.getModelNode();
			        if (data.getStyles().isEmpty())
				        data.addStyle(STYLE1);
			        else
				        {
				        String current_style = data.getStyles().get(0);
				        switch (current_style)
					        {
					        case STYLE1:
						        data.addStyle(STYLE2);
						        break;
					        case STYLE2:
						        data.addStyle(STYLE3);
						        break;
					        }
				        data.removeStyle(current_style);
				        }
			        }
		        }));
        return button;
        }

    private Button createExpandAllButton()
        {
        Button button = new Button();
        button.setText("Expand All");
        button.setOnAction(event -> _tree.expandAll());
        return button;
        }

    private Button createCollapseAllButton()
        {
        Button button = new Button();
        button.setText("Collapse All");
        button.setOnAction(event -> _tree.collapseAll());
        return button;
        }

    private ExampleDataNode _model_root;
    private Button _add_node_button;
    private FancyTreeView<ExampleTreeNodeFacade> _tree;
    private Label _status;

    private final static String STYLE1 = "customstyle1";
    private final static String STYLE2 = "customstyle2";
    private final static String STYLE3 = "customstyle3";
    }