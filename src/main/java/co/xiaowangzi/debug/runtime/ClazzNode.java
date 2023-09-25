package co.xiaowangzi.debug.runtime;

import co.xiaowangzi.debug.utils.Id;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ClazzNode implements java.io.Serializable{

    protected List<ClazzNode> children;
    protected Integer id;
    protected String label;

    protected String icon = "iconfont icon-file";
    protected String incrementalPath;

    public ClazzNode(String nodeValue, String incrementalPath ) {
        children = new ArrayList<ClazzNode>();
        label = nodeValue;
        this. incrementalPath = incrementalPath;
    }

    public void addElement(String currentPath, String[] list) {

        //Avoid first element that can be an empty string if you split a string that has a starting slash as /sd/card/
        while( list[0] == null || list[0].equals("") ) {
            list = Arrays.copyOfRange(list, 1, list.length);
        }

        ClazzNode currentChild = new ClazzNode(list[0], currentPath+"/"+list[0]);
        currentChild.setId(Id.generateId(currentChild.incrementalPath));
        if(currentChild.incrementalPath.endsWith(".java")){
            currentChild.icon = "iconfont icon-source-file-1";
        }
        if ( list.length == 1 ) {
            children.add(currentChild);
            return;
        } else {
            int index = children.indexOf(currentChild);
            if ( index == -1 ) {
                children.add(currentChild);
                currentChild.addElement(currentChild.incrementalPath, Arrays.copyOfRange(list, 1, list.length));
            } else {
                ClazzNode nextChild = children.get(index);
                nextChild.addElement(currentChild.incrementalPath, Arrays.copyOfRange(list, 1, list.length));
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        ClazzNode cmpObj = (ClazzNode)obj;
        return incrementalPath.equals( cmpObj.incrementalPath ) && label.equals( cmpObj.label);
    }

    public List<ClazzNode> getChildren() {
        return children;
    }

    public void setChildren(List<ClazzNode> children) {
        this.children = children;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIncrementalPath() {
        return incrementalPath;
    }

    public void setIncrementalPath(String incrementalPath) {
        this.incrementalPath = incrementalPath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}