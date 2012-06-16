package com.thompson234.bg95.model;

import com.google.common.base.Objects;

public abstract class AbstractModelSummary<T extends ModelSummary> implements ModelSummary, Comparable<ModelSummary> {
    private String _id;
    private String _label;

    public AbstractModelSummary() {

    }

    public AbstractModelSummary(String id, String label) {
        setId(id);
        setLabel(label);
    }

    @Override
    public String getId() {
        return _id;
    }

    public void setId(String id) {
        _id = id;
    }

    public T id(String id) {
        setId(id);
        return (T) this;
    }

    @Override
    public String getLabel() {
        return _label;
    }

    public void setLabel(String label) {
        _label = label;
    }

    public T label(String label) {
        setLabel(label);
        return (T) this;
    }

    public String getType() {
        final String simpleName = getClass().getSimpleName();
        return simpleName.substring(0, simpleName.length() - "Summary".length()).toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Model rhs = (Model) o;

        if (getId() != null ? !getId().equals(rhs.getId()) : rhs.getId() != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public int compareTo(ModelSummary o) {
        return getLabel().compareTo(o.getLabel());
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(getClass()).add("id", getId()).add("label", getLabel()).toString();
    }
}
