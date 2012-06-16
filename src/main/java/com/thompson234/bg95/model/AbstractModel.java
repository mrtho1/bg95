package com.thompson234.bg95.model;

import com.google.common.base.Strings;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

public abstract class AbstractModel<B extends Model, T extends ModelSummary> implements Model<T> {

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

    public B id(String id) {
        setId(id);
        return (B) this;
    }

    @Override
    @JsonIgnore
    public synchronized T getSummary() {

        if (_summary == null) {
            _summary = createSummary();
        }

        return _summary;
    }

    protected synchronized void destroySummary() {
        _summary = null;
    }

    protected abstract T createSummary();

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

        for (Iterator<String> it = strings.iterator(); it.hasNext();) {

            final String string = it.next();

            if (Strings.isNullOrEmpty(string)) {
                it.remove();
            }
        }
    }

    public void sanitize() {
    }
}
