package com.oyster.mycity;

import java.util.List;

/**
 * Created by dima on 26.07.14.
 */
public class ProblemFactory {
    private static List<Problem> mProblems;

    /**
     * Non-instantiable
     */
    private ProblemFactory() {}

    public synchronized static void setProblems(List<Problem> problems) {
        mProblems = problems;
    }

    public static synchronized Problem getProblem(int position) {
        return mProblems.get(position);
    }
}
