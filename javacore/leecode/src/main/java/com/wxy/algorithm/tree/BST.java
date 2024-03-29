package com.wxy.algorithm.tree;

import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author wxy
 * @description 二分搜索树
 * @date 2022/2/18 15:33
 */
public class BST <T extends Comparable<T>>{
    public class Node {
        public T element;
        public Node left,right;
        public Node(T element) {
            this.element = element;
            left = null;
            right =null;
        }
    }

    private Node root;
    private int size;

    public BST(){
        this.size = 0;
        this.root = null;
    }

    public int size(){
        return size;
    }

    public boolean isEmpty(){
        return size==0;
    }

    public void add(T element) {
        this.root = add(this.root,element);
    }

    public Node add(Node node, T element) {
        if (node == null) {
            node = new Node(element);
            size++;
            return node;
        }
        if (node.element.compareTo(element) < 0) {
            node.right = add(node.right, element);
        } else if (node.element.compareTo(element) > 0) {
            node.left = add(node.left, element);
        }
        return node;
    }

    public boolean contains(T element){
       return contains(root,element);
    }

    public boolean contains(Node node,T element){
        if (node == null) {
            return false;
        }
        if (element.compareTo(node.element) == 0) {
            return true;
        } else if (element.compareTo(node.element) > 0){
            return contains(node.right, element);
        } else {
            return contains(node.left, element);
        }
    }

    // 前序遍历递归
    public void preOrder(Node node){
        if (node == null) {
            return;
        }
        System.out.print(node.element+" ");
        preOrder(node.left);
        preOrder(node.right);
    }

    // 前序遍历非递归
    public void preOrderR(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("节点为空!");
        }
        Stack<Node> stack = new Stack<>();
        stack.push(node);
        while (!stack.isEmpty()) {
            Node pop = stack.pop();
            System.out.print(pop.element+" ");
            if (pop.right != null) {
                stack.push(pop.right);
            }
            if (pop.left != null) {
                stack.push(pop.left);
            }
        }
    }

    // 中序遍历
    public void middleOrder(Node node){
        if (node == null) {
            return;
        }
        middleOrder(node.left);
        System.out.print(node.element+" ");
        middleOrder(node.right);
    }

    // 后序遍历
    public void postOrder(Node node){
        if (node == null) {
            return;
        }
        postOrder(node.left);
        postOrder(node.right);
        System.out.print(node.element+" ");
    }

    // 层序遍历
    public void sequenceOrder(Node node) throws Exception {
        if (node == null) {
            throw new IllegalArgumentException("节点为空!");
        }
        Queue<Node> stack = new LinkedBlockingDeque<>();
        stack.offer(node);
        while (true) {
            Node pop = stack.poll();
            if (pop==null) {
                break;
            }
            System.out.print(pop.element+" ");
            if (pop.left != null) {
                stack.add(pop.left);
            }
            if (pop.right != null) {
                stack.add(pop.right);
            }
        }
    }

    // 删除最小的 size--
    public void deleteMin(Node node){
        if (node == null) {
            throw new IllegalArgumentException("节点为空!");
        }
        // 根据中序遍历找到最小的左节点
        findMin(node);
    }

    public Node findMin(Node node){
        if (node.left == null){
            if (node.right!=null){
                node = node.right;
            } else {
                node = null;
            }
            return node;
        }
        node.left = findMin(node.left);
        return node;
    }

    // 删除最大的 size--
    public void deleteMax(Node node){
        if (node == null) {
            throw new IllegalArgumentException("节点为空!");
        }
        // 根据中序遍历找到最小的左节点
        findMax(node);
    }

    private Node findMax(Node node) {
        if (node.right == null){
            if (node.left!=null){
                node = node.left;
            } else {
                node = null;
            }
            return node;
        }
        node.right = findMax(node.right);
        return node;
    }

    // 删除任意的 size--
    public void delete(Node node,T target){
        if (node == null) {
            throw new IllegalArgumentException("节点为空!");
        }
        if (!contains(target)){
            throw new IllegalArgumentException("不存在该节点!");
        }
        deleteThis(node,target);
    }

    private Node deleteThis(Node node, T target) {
        if (node == null ){
            return null;
        }
        if (target.equals(node.element)){
            // 左右都空
            if (node.left == null && node.right == null){
                return null;
            }
            // 左或者右有一个空
            if (node.left != null && node.right == null){
                node = node.left;
                return node;
            }
            if (node.left == null){
                node = node.right;
                return node;
            }
            // 都不为空,找后继节点
            edit(node);
            return node;
        }
        node.left = deleteThis(node.left,target);
        node.right = deleteThis(node.right,target);
        return node;
    }

    private void edit(Node node) {
        Node rightNode =node.right;
        // 获取最下的元素
        while(rightNode.left!=null){
            rightNode = rightNode.left;
        }
        node.element = rightNode.element;
        node.right = findMin(node.right);
    }


    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        generateBSTString(this.root,0,stringBuilder);
        return stringBuilder.toString();
    }

    private void generateBSTString(Node node, int depth, StringBuilder stringBuilder) {
        if (node == null) {
            stringBuilder.append(generateDepthString(depth)).append("null\n");
            return;
        }
        stringBuilder.append(generateDepthString(depth)).append(node.element).append("\n");
        generateBSTString(node.left,depth+1,stringBuilder);
        generateBSTString(node.right,depth+1,stringBuilder);
    }

    private String generateDepthString(int depth) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            stringBuilder.append("--");
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) throws Exception {
        BST<Integer> integerBST = new BST<>();
        integerBST.add(6);
        integerBST.add(4);
        integerBST.add(10);
        integerBST.add(7);
        integerBST.add(8);
        integerBST.add(12);
        integerBST.add(3);
        integerBST.add(5);
        integerBST.add(1);
        //integerBST.deleteMin(integerBST.root);
        //integerBST.deleteMax(integerBST.root);
        integerBST.delete(integerBST.root,6);
        System.out.print("前序");
        integerBST.preOrder(integerBST.root);
        System.out.print("\n前序非递归");
        integerBST.preOrderR(integerBST.root);
        System.out.print("\n中序");
        integerBST.middleOrder(integerBST.root);
        System.out.print("\n后序");
        integerBST.postOrder(integerBST.root);
        System.out.print("\n层序");
        integerBST.sequenceOrder(integerBST.root);

    }
}

/*
    *    6
    * 4     10
    3   5  7   12
  1          8
* */