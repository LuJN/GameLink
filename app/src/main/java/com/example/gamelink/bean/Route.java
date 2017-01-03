package com.example.gamelink.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/29.
 */
public class Route {
    private List<Integer> nodeList;

    public Route() {
        nodeList = new ArrayList<>();
    }

    public void pushNode(int node) {
        nodeList.add(node);
    }

    public List<Integer> getNodeList() {
        return nodeList;
    }
}
