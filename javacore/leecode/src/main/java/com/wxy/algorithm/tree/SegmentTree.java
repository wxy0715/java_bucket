package com.wxy.algorithm.tree;

/** 线段树*/
public class SegmentTree<T> {
    private Merge<T> merger ;

    private T[] data;

    private T[] tree;

    public SegmentTree(T[] arr,Merge<T> merge){
        data = (T[]) new Object[arr.length];
        System.arraycopy(arr,0,data,0,arr.length);
        tree = (T[]) new Object[4*arr.length];
        // 构建线段树
        merger = merge;
        buildSegmentTree(0,0,arr.length-1);
    }

    private void buildSegmentTree(int index, int l, int r) {
        if (l == r){
            tree[index] = data[l];
            return;
        }
        int leftIndex = leftChild(index);
        int rightIndex = rightChild(index);
        int mid = l+(r-l)/2;
        buildSegmentTree(leftIndex,l,mid);
        buildSegmentTree(rightIndex,mid+1,r);
        tree[index] = merger.merge(tree[leftIndex] , tree[rightIndex]);
    }

    public int getSize(){
        return data.length;
    }

    public T get(int index){
        if (index<0 || index >= data.length) {
            throw new IllegalArgumentException("index is illegal.");
        }
        return data[index];
    }

    /** 查询区间范围的值*/
    public T query(int queryL, int queryR){
        if (queryL<0 || queryL >= data.length || queryR<0 || queryR>=data.length || queryL>queryR) {
            throw new IllegalArgumentException("param is illegal.");
        }
        return query(0,0,data.length,queryL,queryR);
    }

    public T query(int index, int l, int r,int queryL, int queryR){
        if(l==queryL && r == queryR) {
            return tree[index];
        }
        int leftIndex = leftChild(index);
        int rightIndex = rightChild(index);
        int mid = l+(r-l)/2;
        if (mid+1<=queryL){
            return query(rightIndex,mid+1,r,mid+1,queryR);
        }
        if (queryR<=mid) {
            return query(leftIndex,l,mid,queryL,mid);
        }
        T rightResult = query(rightIndex,mid+1,r,mid+1,queryR);
        T leftResult = query(leftIndex,l,mid,queryL,mid);
        return merger.merge(leftResult,rightResult);
    }

    /** 更新操作*/
    public void update(T e,int index){
        if(index < 0 || index > data.length) {
            throw new IllegalArgumentException("index is illegal");
        }
        update(0,0,data.length,e,index);
    }

    public void update(int treeIndex,int l,int r, T e, int index){
        if(l==r) {
            tree[treeIndex] = e;
        }
        int mid = l+(r-l)/2;
        int leftIndex = leftChild(index);
        int rightIndex = rightChild(index);
        if (mid+1<=index){
            update(rightIndex,mid+1,r,e,index);
        } else {
            update(leftIndex,l,mid,e,index);
        }
        tree[treeIndex] = merger.merge(tree[leftIndex],tree[rightIndex]);
    }

    /** 返回左孩子节点索引*/
    public int leftChild(int index) {
        return index*2+1;
    }

    /** 返回右孩子节点索引*/
    public int rightChild(int index) {
        return index*2+2;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (int i = 0; i < tree.length; i++) {
            if (tree[i] == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(tree[i]);
            }
            if (i!=tree.length-1){
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        Integer[] arr = new Integer[]{1,2,3,4,5,6};
        SegmentTree<Integer> integerSegmentTree = new SegmentTree<>(arr, Integer::sum);
        System.out.println(integerSegmentTree);
        System.out.println(integerSegmentTree.query(1,2));
    }
}

/*
* 123456
* 123 456
* 12 3  45 6
* 1 2  4 5
* * */