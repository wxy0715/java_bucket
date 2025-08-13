package com.cjree.core.basic.util.tree;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.cjree.core.common.utils.CoreObjectUtil;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 树形结构工具类
 * @author wangxingyu
 */
public class GenericTreeUpdateHelper {

    /**
     * 通用的根据 ID 更新叶子节点信息的方法
     * @param getParentIdFunction 获取父节点ID的函数
     * @param getIdFunction 获取节点ID的函数
     * @param getInstanceIdFunction 获取实例ID的函数
     * @param getLeafFunction 设置叶子节点信息的函数
     * @param selectCountFunction 根据查询条件统计数量的函数
     * @param updateConsumer 根据查询条件和更新条件更新数据的函数
     * @param id 当前节点 ID
     * @param leaf 叶子节点信息
     * @param instanceId 实例 ID
     * @param <T> 节点类型
     */
    public static <T> void updateLeafById(
            SFunction<T, Long> getParentIdFunction,
            SFunction<T, Long> getIdFunction,
            SFunction<T, Long> getInstanceIdFunction,
            SFunction<T, String> getLeafFunction,
            Function<LambdaQueryWrapper<T>, Long> selectCountFunction,
            BiConsumer<LambdaQueryWrapper<T>, LambdaUpdateWrapper<T>> updateConsumer,
            Long id, String leaf, Long instanceId) {
        // 统计具有相同父节点ID和实例ID的子节点数量
        LambdaQueryWrapper<T> countQueryWrapper = new LambdaQueryWrapper<>();
        countQueryWrapper.eq(getParentIdFunction, id);
        countQueryWrapper.eq(getInstanceIdFunction, instanceId);
        Long count = selectCountFunction.apply(countQueryWrapper);
        if (count > 1) {
            return;
        }
        // 查询缓存数据
        LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(getIdFunction, id);
        queryWrapper.eq(getInstanceIdFunction, instanceId);
        queryWrapper.select(getIdFunction);
        // 更新缓存数据
        LambdaUpdateWrapper<T> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(getLeafFunction, leaf);
        updateWrapper.eq(getIdFunction, id);
        updateWrapper.eq(getInstanceIdFunction, instanceId);
        // 执行更新操作
        updateConsumer.accept(queryWrapper, updateWrapper);
    }

    /**
     * 通用的更新树节点层级和路径码的方法
     * @param getIdFunction 获取节点ID的函数
     * @param getLevelFunction 获取节点层级的函数
     * @param getPathCodeFunction 获取节点路径码的函数
     * @param setParentIdFunction 设置父节点ID的函数
     * @param setLevelFunction 设置节点层级的函数
     * @param setPathCodeFunction 设置节点路径码的函数
     * @param getByIdFunction 根据ID获取节点的函数
     * @param selectListSupplier 根据查询条件查询节点列表的函数
     * @param updateFunction 更新节点的函数
     * @param id 当前节点ID
     * @param curParentId 当前父节点ID
     * @param newParentId 新的父节点ID
     * @param <T> 节点类型
     */
    public static <T> void updateLevelAndPathCode(
            Function<T, Long> getIdFunction,
            Function<T, Integer> getLevelFunction,
            SFunction<T, String> getPathCodeFunction,
            BiConsumer<T, Long> setParentIdFunction,
            BiConsumer<T, Integer> setLevelFunction,
            BiConsumer<T, String> setPathCodeFunction,
            Function<Long, T> getByIdFunction,
            Supplier<LambdaQueryChainWrapper<T>> selectListSupplier,
            Function<T, T> updateFunction,
            Long id, Long curParentId, Long newParentId) {

        // 当前节点数据
        T currentNode = getByIdFunction.apply(id);
        String curParentPathCode = "";
        if (CoreObjectUtil.isNotEmpty(curParentId)) {
            // 当前父级节点数据
            T curParentNode = getByIdFunction.apply(curParentId);
            curParentPathCode = getPathCodeFunction.apply(curParentNode);
        }

        // 获取level差
        Integer level = 0;
        String newParentPathCode = "";
        // 新节点数据
        if (CoreObjectUtil.isNotEmpty(newParentId)) {
            T newParentNode = getByIdFunction.apply(newParentId);
            level = getLevelFunction.apply(currentNode) - (getLevelFunction.apply(newParentNode) + 1);
            newParentPathCode = getPathCodeFunction.apply(newParentNode);
        } else {
            level = getLevelFunction.apply(currentNode);
        }
        List<T> poList = selectListSupplier.get().likeRight(getPathCodeFunction, getPathCodeFunction.apply(currentNode)).list();
        for (T node : poList) {
            // 更新层级
            setLevelFunction.accept(node, getLevelFunction.apply(node) - level);
            // 更新路径码
            if (CoreObjectUtil.isNotEmpty(curParentId) && CoreObjectUtil.isNotEmpty(newParentId)) {
                setPathCodeFunction.accept(node, getPathCodeFunction.apply(node).replace(curParentPathCode, newParentPathCode));
            }
            if (CoreObjectUtil.isEmpty(curParentId) && CoreObjectUtil.isNotEmpty(newParentId)) {
                setPathCodeFunction.accept(node, newParentPathCode + "_" + getPathCodeFunction.apply(node));
            }
            if (CoreObjectUtil.isNotEmpty(curParentId) && CoreObjectUtil.isEmpty(newParentId)) {
                setPathCodeFunction.accept(node, getPathCodeFunction.apply(node).replace(curParentPathCode + "_", ""));
            }
            // 根据父级id
            if (getIdFunction.apply(node).equals(id)) {
                setParentIdFunction.accept(node, newParentId);
            }
            updateFunction.apply(node);
        }
    }


    /**
     * 通用的构建树形结构的方法
     * @param outList 扁平的对象列表
     * @param getIdFunction 获取对象 ID 的函数
     * @param getParentIdFunction 获取对象父 ID 的函数
     * @param setChildrenFunction 设置对象子节点列表的函数
     * @param <T> 对象类型
     * @return 树形结构的对象列表
     */
    public static <T> List<T> buildTree(List<T> outList,
                                    Function<T, Long> getIdFunction,
                                    Function<T, Long> getParentIdFunction,
                                    BiConsumer<T, List<T>> setChildrenFunction) {
        if (outList.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, List<T>> parentIdToChildrenMap = new HashMap<>();
        for (T obj : outList) {
            Long parentId = getParentIdFunction.apply(obj);
            parentIdToChildrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(obj);
        }
        List<T> tree = new ArrayList<>();
        for (T out : outList) {
            Long id = getIdFunction.apply(out);
            // 设置子元素列表
            List<T> children = parentIdToChildrenMap.getOrDefault(id, new ArrayList<>());
            setChildrenFunction.accept(out, children);
            // 检查是否为根元素
            if (getParentIdFunction.apply(out) == null) {
                tree.add(out);
            }
        }
        return tree;
    }
}
