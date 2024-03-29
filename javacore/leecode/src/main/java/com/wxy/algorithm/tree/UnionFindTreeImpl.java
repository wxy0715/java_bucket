package com.wxy.algorithm.tree;

/** 并查集树实现-基于size优化*/
public class UnionFindTreeImpl implements UnionFind{
    private int[] parent;
    private int[] sz;

    public UnionFindTreeImpl(int length){
        parent = new int[length];
        sz = new int[length];
        for (int i = 0; i < length; i++) {
            parent[i] = i;
            sz[i] = 1;
        }
    }
    public int find(int p) {
        if (p<0 || p>=parent.length){
            throw new IllegalArgumentException("p is out of bound");
        }
        while(p!=parent[p]){
            p = parent[p];
        }
        return p;
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
        if (sz[pid]<sz[qid]){
            parent[pid] = qid;
            sz[pid] += sz[qid];
        } else {
            parent[qid] = pid;
            sz[qid] += sz[pid];
        }

    }
}
