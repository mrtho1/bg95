package com.thompson234.bg95.model;

public interface Model<T extends ModelSummary> {

    public String getId();
    public T getSummary();

    void sanitize();
}
