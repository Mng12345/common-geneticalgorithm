package com.zhangm.algorithms;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author zhangming
 * @Date 2020/6/30 15:25
 */
@Getter
@Setter
public abstract class Individual<T1, T2> {

    public Individual() {
    }

    // 用于具体的实现类获取data
    private T1 data;
    // 用于具体的实现类获取指标
    private T2 index;

    public abstract Individual<T1, T2> mutate();

      /**
     * 计算指标，便于适应度值排序
     */
    public abstract void calculateIndex();

    @Override
    public abstract Individual<T1, T2> clone();

}
