package com.wxy.algorithm.tree;

/** 并查集接口*/
public interface UnionFind {
    boolean isConnected(int p,int q);
    void unionElements(int p, int q);
}
