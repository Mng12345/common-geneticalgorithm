package algorithms;

import algorithms.impl.SimpleGA;
import com.zhangm.easyutil.Tuple;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;

/**
 * @Author zhangming
 * @Date 2020/6/30 15:03
 */
@Getter
@Setter
public abstract class CommonGA<T1, T2> {

    private Individual<T1, T2> bestIndividual;
    private List<Individual<T1, T2>> trace;

    public interface CrossFunction<T1, T2> {
        Tuple<Individual<T1, T2>, Individual<T1, T2>> apply(Individual<T1, T2> individual1, Individual<T1, T2> individual2);
    }

    /**
     * 默认以小为优
     * @param <T1>
     * @param <T2>
     */
    public interface ComparatorFunction<T1, T2> {
        Comparator<Individual<T1, T2>> apply();
    }

    public interface InitFunction<T1, T2> {
        Individual<T1, T2> apply();
    }

    public static <T1, T2> CommonGA<T1, T2> of(Class<? extends Individual<T1, T2>> individualClazz,
                                CrossFunction<T1, T2> crossFunction, ComparatorFunction<T1, T2> comparatorFunction,
                                InitFunction<T1, T2> initFunction) {
        return new SimpleGA<T1, T2>(individualClazz, crossFunction, comparatorFunction, initFunction);
    }

    // 启动
    public abstract List<Individual<T1, T2>> start();

    // 调整参数
    public abstract CommonGA<T1, T2> withPopSize(int popSize);
    public abstract CommonGA<T1, T2> withGeneralSize(int generalSize);
    public abstract CommonGA<T1, T2> withPCross(double pCross);
    public abstract CommonGA<T1, T2> withPMutate(double pMutate);

}
