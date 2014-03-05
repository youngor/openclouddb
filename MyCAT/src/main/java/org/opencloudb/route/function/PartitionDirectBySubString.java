package org.opencloudb.route.function;

import org.opencloudb.config.model.rule.RuleAlgorithm;

/**
 * Created by bingoohuang on 14-2-28.
 */
public class PartitionDirectBySubString extends AbstractPartionAlgorithm implements RuleAlgorithm {
    private int startIndex;
    private int size;

    public void setStartIndex(String str) {
        startIndex = Integer.parseInt(str);
    }

    public void setSize(String str) {
        size = Integer.parseInt(str);
    }


    @Override
    public void init() {

    }

    @Override
    public Integer calculate(String columnValue) {
        return Integer.parseInt(columnValue.substring(startIndex, startIndex + size));
    }
}
