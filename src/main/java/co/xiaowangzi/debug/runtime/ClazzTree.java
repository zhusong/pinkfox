package co.xiaowangzi.debug.runtime;

import co.xiaowangzi.debug.clazz.Clazz;

public class ClazzTree implements java.io.Serializable{

    ClazzNode root;

    int id;

    public ClazzTree(ClazzNode root ) {
        this.root = root;
    }

    public void addElement(String elementValue ) {
        String[] list = elementValue.split("/");
        root.addElement(root.incrementalPath, list);
    }

    public ClazzNode getRoot() {
        return root;
    }

    public void setRoot(ClazzNode root) {
        this.root = root;
    }

}