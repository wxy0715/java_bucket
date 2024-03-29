package com.wxy.algorithm.sort;

import lombok.Data;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

@Data
public class DataWithSource implements Comparable<DataWithSource> {
    /**
     * 数值
     */
    private int value;

    /**
     * 记录数值来源的数组
     */
    private int source;

    /**
     * 记录数值在数组中的索引
     */
    private int index;

    public DataWithSource(int value, int source, int index) {
        this.value = value;
        this.source = source;
        this.index = index;
    }

    /**
     *
     * 由于 PriorityQueue 使用小顶堆来实现，这里通过修改
     * 两个整数的比较逻辑来让 PriorityQueue 变成大顶堆
     */
    @Override
    public int compareTo(DataWithSource o) {
        return Integer.compare(o.getValue(), this.value);
    }
}

class Test {
    public static int[] getTop(int[][] data) {
        int rowSize = data.length;
        int columnSize = data[0].length;

        // 创建一个columnSize大小的数组，存放结果
        int[] result = new int[columnSize];

        PriorityQueue<DataWithSource> maxHeap = new PriorityQueue<>();
        for (int i = 0; i < rowSize; ++i) {
            // 将每个数组的最大一个元素放入堆中
            DataWithSource d = new DataWithSource(data[i][0], i, 0);
            maxHeap.add(d);
        }

        int num = 0;
        while (num < columnSize) {
            // 删除堆顶元素
            DataWithSource d = maxHeap.poll();
            result[num++] = d.getValue();
            if (num >= columnSize) {
                break;
            }

            d.setValue(data[d.getSource()][d.getIndex() + 1]);
            d.setIndex(d.getIndex() + 1);
            maxHeap.add(d);
        }
        return result;

    }


    /**
     * 小顶堆
     * @param data 数组
     * @param N 数据量
     */
    static void topN(int[] data, int N) {
        PriorityBlockingQueue<Integer> queue = new PriorityBlockingQueue<>();
        int minData = data[0];
        for (int i = 0; i < data.length; i++) {
            if (queue.size() == N) {
                if (minData < data[i]) {
                    // 大于原来最小的，就放进去新的,删除旧的
                    queue.offer(data[i]);
                    int header = queue.poll();
                    minData = header;
                }
            } else {
                // 最开始 N个，直接保留
                minData = Math.min(minData, data[i]);
                queue.offer(data[i]);
            }
        }
        while (!queue.isEmpty()) {
            System.out.println(queue.poll());

        }
    }

    /**
     * 大顶堆
     * @param data 数组
     * @param N 数据量
     */
    static void lowN(int[] data, int N) {
        PriorityBlockingQueue<Integer> queue = new PriorityBlockingQueue<>(N,(a,b)->b-a);
        int maxData = data[0];
        for (int i = 0; i < data.length; i++) {
            if (queue.size() == N) {
                if (maxData > data[i]) {
                    // 小于原来最大的，就放进去新的,删除旧的
                    queue.offer(data[i]);
                    int header = queue.poll();
                    maxData = header;
                }
            } else {
                // 最开始 N个，直接保留
                maxData = Math.max(maxData, data[i]);
                queue.offer(data[i]);
            }
        }
        while (!queue.isEmpty()) {
            System.out.println(queue.poll());

        }
    }

    public static void main(String[] args) {
        lowN(new int[]{1,5,4,0,2,3},3);
        topN(new int[]{1,5,4,0,2,3},3);
        int[][] data = {
                {29, 18, 14, 2, 1},
                {17, 17, 16, 15, 6},
                {30, 25, 20, 14, 5},
        };
        int[] top = getTop(data);
        System.out.println(Arrays.toString(top)); // [30, 29, 25, 20, 19]
    }
}