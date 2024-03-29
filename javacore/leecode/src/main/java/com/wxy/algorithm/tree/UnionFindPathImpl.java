package com.wxy.algorithm.tree;

/** 并查集树实现-基于路径压缩优化*/
public class UnionFindPathImpl implements UnionFind {
    private int[] parent;
    private int[] rank;

    public UnionFindPathImpl(int length){
        parent = new int[length];
        rank = new int[length];
        for (int i = 0; i < length; i++) {
            parent[i] = i;
            rank[i] = 1;
        }
    }
    public int find(int p) {
        if (p<0 || p>=parent.length){
            throw new IllegalArgumentException("p is out of bound");
        }
        while(p!=parent[p]){
            parent[p] = parent[parent[p]];
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
        if (rank[pid]<rank[qid]){
            parent[pid] = qid;
        } else if (rank[pid]>rank[qid]){
            parent[qid] = pid;
        } else {
            parent[qid] = pid;
            rank[pid] += 1;
        }

    }
}
