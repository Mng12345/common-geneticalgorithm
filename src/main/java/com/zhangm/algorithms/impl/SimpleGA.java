package com.zhangm.algorithms.impl;

import com.zhangm.algorithms.CommonGA;
import com.zhangm.algorithms.Individual;
import com.zhangm.easyutil.RangeUtil;
import com.zhangm.easyutil.Streams;
import com.zhangm.easyutil.Tuple;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


/**
 * @Author zhangming
 * @Date 2020/6/30 15:37
 */
@Getter
@Setter
public class SimpleGA<T1, T2> extends CommonGA<T1, T2> {

    private List<Individual<T1, T2>> individuals;
    private int popSize;
    private int generalSize;
    private double pCross;
    private double pMutate;
    private Class<? extends Individual<T1, T2>> individualClazz;
    private CrossFunction<T1, T2> crossFunction;
    private ComparatorFunction<T1, T2> comparatorFunction;
    private InitFunction<T1, T2> initFunction;

    public SimpleGA(Class<? extends Individual<T1, T2>> individualClazz, CrossFunction<T1, T2> crossFunction,
                    ComparatorFunction<T1, T2> comparatorFunction, InitFunction<T1, T2> initFunction) {
        this.popSize = 100;
        this.generalSize = 200;
        this.pCross = 0.75;
        this.pMutate = 0.05;
        this.individualClazz = individualClazz;
        this.crossFunction = crossFunction;
        this.comparatorFunction = comparatorFunction;
        this.initFunction = initFunction;
        this.setTrace(new ArrayList<>());
        initPop();
    }

    @Override
    public List<Individual<T1, T2>> start() {
        for (int i=0; i<generalSize; i++) {
            select();
            cross();
            mutate();
            saveBest();
        }
        return this.getTrace();
    }

    @Override
    public CommonGA<T1, T2> withPopSize(int popSize) {
        this.popSize = popSize;
        initPop();
        return this;
    }

    @Override
    public CommonGA<T1, T2> withGeneralSize(int generalSize) {
        this.generalSize = generalSize;
        return this;
    }

    @Override
    public CommonGA<T1, T2> withPCross(double pCross) {
        this.pCross = pCross;
        return this;
    }

    @Override
    public CommonGA<T1, T2> withPMutate(double pMutate) {
        this.pMutate = pMutate;
        return this;
    }

    private void initPop() {
        this.individuals = new ArrayList<>();
        for (int i=0; i<popSize; i++) {
            Individual<T1, T2> individual = this.initFunction.apply();
            individual.calculateIndex();
            this.individuals.add(individual);
        }
        this.setBestIndividual(this.individuals.stream().min(this.comparatorFunction.apply()).orElse(null));
    }

    private void select() {
        // 将种群进行排序
        this.individuals.sort(this.comparatorFunction.apply());
        int first20 = (int) (0.2 * this.popSize);
        int last80 = this.popSize - first20;
        // 从first20中挑选出last80个个体
        List<Individual<T1, T2>> leftIndividuals = RangeUtil.random(this.individuals.subList(0, first20), last80);
        List<Individual<T1, T2>> rightIndividuals = RangeUtil.random(this.individuals.subList(first20, popSize), first20);
        this.individuals = RangeUtil.merge(leftIndividuals, rightIndividuals);
    }

    private void cross() {
        List<Tuple<Integer, Integer>> crossedIndexes = new ArrayList<>();
        for (int i=0; i<this.popSize; i++) {
            if (Math.random() < this.pCross) {
                // 从种群中随机挑选出两个不重复的个体
                int[] index = new int[2];
                RangeUtil.randomNotRepeat(this.individuals, 2, index);
                crossedIndexes.add(new Tuple<>(index[0], index[1]));
            }
        }
        crossedIndexes.parallelStream().forEach(twoCrossIndex -> {
            Tuple<Individual<T1, T2>, Individual<T1, T2>> crossedIndividuals = this.crossFunction.apply(
                    this.individuals.get(twoCrossIndex.getV1()), this.individuals.get(twoCrossIndex.getV2()));
            this.individuals.set(twoCrossIndex.getV1(), crossedIndividuals.getV1());
            this.individuals.set(twoCrossIndex.getV2(), crossedIndividuals.getV2());
        });
        Streams.forEachIndexedParallel(this.individuals.stream(),
                (individual, index) -> this.individuals.get(index.intValue()).calculateIndex());
    }

    private void mutate() {
        for (int i=0; i<this.popSize; i++) {
            if (Math.random() < this.pMutate) {
                // 随机挑选出一个个体进行变异
                int randomIndex = RangeUtil.random(0, this.popSize - 1);
                this.individuals.set(randomIndex, this.individuals.get(randomIndex).mutate());
                this.individuals.get(randomIndex).calculateIndex();
            }
        }
    }

    private void saveBest() {
        // 找到当前种群中的最优个体
        Individual<T1, T2> currentBestIndividual = this.individuals.stream().min(this.comparatorFunction.apply()).orElse(null);
        if (currentBestIndividual == null) {
            throw new RuntimeException("无法找到当前种群中的最优值");
        }
        int compareValue = this.comparatorFunction.apply().compare(currentBestIndividual, this.getBestIndividual());
        if (compareValue < 0) {
            this.setBestIndividual(currentBestIndividual.clone());
        }
        // 将全局最佳个体随机替换进种群中
        this.individuals.set((int)Math.floor(Math.random() * popSize), getBestIndividual().clone());
        // 记录全局最佳个体
        this.getTrace().add(this.getBestIndividual().clone());
    }
}
