# common-geneticalgorithm
make it easy to optimize something with Genetic algorithm

# describe
There is only need to extends the abstract class Individual by using this project's package, so that you can do some optimization work with Genetic Algorithm!

# example

if you want to find the maximal value of the function `x^2+3*sin(x*y*z)+y^2-z^2`, and the related value of x, y, z, you can write the code below.

## the function's graph

![image](https://github.com/Mng12345/common-geneticalgorithm/blob/master/graph/g01.jpg)

## code
```java
package examples;

import algorithms.CommonGA;
import algorithms.Individual;
import com.zhangm.easyutil.Tuple;


/**
 * 计算x^2+3*sin(x*y*z)+y^2-z^2的最大值，并求处于该值时x, y, z的值
 * @Author zhangming
 * @Date 2020/6/30 16:31
 */
public class SimpleIndividual extends Individual<double[], Double> {

    public SimpleIndividual() {
        double[] data = new double[] {Math.random() * 20 - 10, Math.random() * 20 - 10, Math.random() * 20 - 10};
        this.setData(data);
        this.setIndex(0.);
    }

    private double[][] bounds = new double[][] {new double[]{-10, 10}, new double[]{-10, 10}, new double[]{-10, 10}};

    @Override
    public SimpleIndividual mutate() {
        SimpleIndividual _this = this.clone();
        int dataLen = this.getData().length;
        // 随机挑选一个值发生变异
        int index = (int) Math.floor(Math.random() * dataLen);
        if (Math.random() < 0.5) {
            // 向下偏移0.1
            _this.getData()[index] -= (this.bounds[index][1] - this.bounds[index][0]) * 0.1;
            if (_this.getData()[index] < this.bounds[index][0]) {
                _this.getData()[index] = this.bounds[index][0];
            }
        } else {
            // 向上偏移0.1
            _this.getData()[index] += (this.bounds[index][1] - this.bounds[index][0]) * 0.1;
            if (_this.getData()[index] > this.bounds[index][1]) {
                _this.getData()[index] = this.bounds[index][1];
            }
        }
        return _this;
    }

    @Override
    public void calculateIndex() {
        // 计算指标
        double x = this.getData()[0];
        double y = this.getData()[1];
        double z = this.getData()[2];
        this.setIndex(x * x + 3 * Math.sin(x * y * z) + y * y - z * z);
    }

    @Override
    public SimpleIndividual clone() {
        double[] data = new double[3];
        System.arraycopy(this.getData(), 0, data, 0, 3);
        double index = this.getIndex();
        SimpleIndividual newIndividual = new SimpleIndividual();
        newIndividual.setData(data);
        newIndividual.setIndex(index);
        return newIndividual;
    }

    public static void main(String[] args) {
        CommonGA.CrossFunction<double[], Double> crossFunction = (Individual<double[], Double> individual1, Individual<double[], Double> individual2) -> {
            int index = (int) Math.floor(Math.random() * 3);
            Individual<double[], Double> newIndividual1 = individual1.clone();
            Individual<double[], Double> newIndividual2 = individual2.clone();
            newIndividual1.getData()[index] = individual2.getData()[index];
            newIndividual2.getData()[index] = individual1.getData()[index];
            return new Tuple<>(newIndividual1, newIndividual2);
        };

        CommonGA.InitFunction<double[], Double> initFunction = SimpleIndividual::new;

        // 由于算法以小为优，此处取最大值
        CommonGA.ComparatorFunction<double[], Double> comparatorFunction = () -> (individual1, individual2) ->
            individual1.getIndex() < individual2.getIndex() ? 1 : individual1.getIndex().equals(individual2.getIndex()) ? 0 : -1;

        CommonGA<double[], Double> ga = CommonGA.of(SimpleIndividual.class, crossFunction, comparatorFunction, initFunction)
                .withGeneralSize(200).withPopSize(100);
        ga.start();
        Individual<double[], Double> bestIndividual = ga.getBestIndividual();
        System.out.printf("max: %f, x: %f, y: %f, z: %f\n", bestIndividual.getIndex(),
                bestIndividual.getData()[0], bestIndividual.getData()[1], bestIndividual.getData()[2]);
    }

}
```

## result

`max: 202.686378, x: -10.000000, y: -10.000000, z: -0.302324`