package com.thompson234.bg95.model;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractModel<T extends Model> implements Model {

    protected static final String ID_SUMMARY = "id";
    protected static final String TYPE_SUMMARY = "type";

    private boolean _idFrozen = false;
    private String _id;
    private T _summary;

    public AbstractModel() {
    }

    public AbstractModel(String id) {
        setId(id);
    }

    protected static String newId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String getId() {
        return _id;
    }

    public void setId(String id) {
        if (isIdFrozen()) {
            throw new IllegalStateException();
        }

        _id = id;
    }

    protected void freezeId() {
        _idFrozen = true;
    }

    protected boolean isIdFrozen() {
        return _idFrozen;
    }

    public T id(String id) {
        setId(id);
        return (T) this;
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

    protected void sanitizeStrings(Collection<String> strings) {

        for (Iterator<String> it = strings.iterator(); it.hasNext(); ) {

            final String string = it.next();

            if (Strings.isNullOrEmpty(string)) {
                it.remove();
            }
        }
    }

    public void sanitize() {
    }

    @Override
    public Map<String, Object> summarize() {
        final Map<String, Object> summary = Maps.newHashMap();
        summary.put(ID_SUMMARY, getId());
        summary.put(TYPE_SUMMARY, getClass().getSimpleName().toLowerCase());

        return summary;
    }
}
