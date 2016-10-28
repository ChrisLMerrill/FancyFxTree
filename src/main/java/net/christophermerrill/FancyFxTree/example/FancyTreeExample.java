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
        root.setTop(button_bar);

        _model_root = ExampleDataNodeBuilder.create(new int[] {4,1,3,2});
        _tree = new FancyTreeView(new ExampleOperationHandler(_model_root));
        _tree.setRoot(new ExampleTreeNodeFacade(_model_root));
        _tree.expandAll();
        root.setCenter(_tree);

        _tree.getSelectionModel().getSelectedItems().addListener(new ListChangeListener()
            {
            @Override
            public void onChanged(Change c)
                {
                _add_node_button.setDisable(_tree.getSelectionModel().getSelectedItems().size() != 1);
                }
            });

        URL resource = getClass().getResource("ExampleTree.css");
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

    private ExampleDataNode _model_root;
    private Button _add_node_button;
    private FancyTreeView<ExampleTreeNodeFacade> _tree;
    }

