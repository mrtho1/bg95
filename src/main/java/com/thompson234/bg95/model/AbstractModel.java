package com.thompson234.bg95.model;

import com.google.common.base.Strings;
import com.thompson234.bg95.json.Views;
import org.codehaus.jackson.map.annotate.JsonView;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

public abstract class AbstractModel<T extends Model> implements Model {

    private boolean _idFrozen = false;
    private String _id;

    public AbstractModel() {
    }

    public AbstractModel(String id) {
        setId(id);
    }

    protected static String newId() {
        return UUID.randomUUID().toString();
    }

    @Override
    @JsonView(value = {Views.Base.class})
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
    @JsonView(value = {Views.Summary.class})
    public String getType() {
        return getClass().getSimpleName().toLowerCase();
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
}
