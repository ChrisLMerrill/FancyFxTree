module FancyFxTree {
    requires javafx.controls;
    opens net.christophermerrill.FancyFxTree to javafx.graphics;
    opens net.christophermerrill.FancyFxTree.example to javafx.graphics;
}