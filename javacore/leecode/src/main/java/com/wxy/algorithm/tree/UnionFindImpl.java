package com.wxy.algorithm.tree;

/** 并查集数组实现*/
public class UnionFindImpl implements UnionFind{
    private int[] id;

    public UnionFindImpl(int length){
        id = new int[length];
        for (int i = 0; i < length; i++) {
            id[i] = i;
        }
    }
    public int find(int p) {
        if (p<0 || p>=id.length){
            throw new IllegalArgumentException("p is out of bound");
        }
        return id[p];
    }

    @Override
    public boolean isConnected(int p, int q) {
        return find(p)==find(q);
    }

    @Override
    public void unionElements(int p, int q) {
        int pid = find(p);
        int qid = find(q);
        if (qid == pid) {
            return;
        }
        for (int i = 0; i < id.length; i++) {
            if (pid == id[i]) {
                id[i] = qid;
            }
        }
    }
}
