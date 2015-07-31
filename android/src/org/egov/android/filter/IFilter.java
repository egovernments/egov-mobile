package org.egov.android.filter;

public interface IFilter<E1, E2> {
    public boolean filter(E1 data1, E2 data2);
}
