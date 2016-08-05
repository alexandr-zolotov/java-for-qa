package com.thomascook.jfq.lambdas;

import org.apache.commons.lang3.RandomUtils;
import static org.apache.commons.lang3.StringUtils.*;

public enum Action {

    CHASES, EATS, LIKES, CALLS, HATES, JUMPS_ON;

    public static Action random(){
        return Action.values()[RandomUtils.nextInt(0, Action.values().length)];
    }

    public String toString(){
        return replace(lowerCase(this.name()), "_", SPACE);
    }
}
