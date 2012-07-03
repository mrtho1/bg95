package com.thompson234.bg95.model;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.thompson234.bg95.json.Views;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.annotate.JsonView;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@JsonAutoDetect
public class Airman extends AbstractModel<Airman> implements Comparable<Airman> {

    private Name _name;
    private List<String> _ranks = Lists.newArrayList();
    private Set<String> _roles = Sets.newHashSet();
    private Set<String> _notes = Sets.newHashSet();
    private Set<String> _units = Sets.newHashSet();
    private Set<String> _imageUrls = Sets.newHashSet();

    public Airman() {
        super(newId());
    }

    @JsonView(value = {Views.Summary.class})
    public String getLabel() {
        return getFullName();
    }

    @JsonView(value = {Views.Summary.class})
    public String getFullName() {
        return _name.getFullName();
    }

    public void setFullName(String fullName) {
        _name = new Name(fullName);
    }

    public Airman fullName(String fullName) {
        setFullName(fullName);
        return this;
    }

    @JsonView(value = {Views.Storage.class, Views.Detail.class})
    public Name getName() {
        return _name;
    }

    public void setName(Name name) {
        _name = name;
    }

    public Airman name(Name name) {
        setName(name);
        return this;
    }

    @JsonView(value = {Views.Storage.class, Views.Detail.class})
    public ImmutableList<String> getRanks() {
        return ImmutableList.copyOf(_ranks);
    }

    public void setRanks(Collection<String> ranks) {
        _ranks.clear();
        _ranks.addAll(ranks);
    }

    public Airman ranks(Collection<String> ranks) {
        setRanks(ranks);
        return this;
    }

    public Airman rank(String rank) {
        _ranks.add(rank);
        return this;
    }

    @JsonView(value = {Views.Summary.class})
    public String getRank() {
        if (_ranks.isEmpty()) {
            return null;
        }

        return _ranks.get(_ranks.size() - 1);
    }

    @JsonView(value = {Views.Storage.class, Views.Detail.class})
    public ImmutableSet<String> getRoles() {
        return ImmutableSet.copyOf(_roles);
    }

    public void setRoles(Collection<String> roles) {
        _roles.clear();
        _roles.addAll(roles);
    }

    public Airman roles(Collection<String> roles) {
        setRoles(roles);
        return this;
    }

    public Airman role(String role) {
        _roles.add(role);
        return this;
    }

    @JsonView(value = {Views.Storage.class, Views.Detail.class})
    public ImmutableSet<String> getNotes() {
        return ImmutableSet.copyOf(_notes);
    }

    public void setNotes(Collection<String> notes) {
        _notes.clear();
        _notes.addAll(notes);
    }

    public Airman notes(Collection<String> notes) {
        setRoles(notes);
        return this;
    }

    public Airman note(String note) {
        _notes.add(note);
        return this;
    }

    @JsonView(value = {Views.Storage.class, Views.Detail.class})
    public ImmutableSet<String> getUnits() {
        return ImmutableSet.copyOf(_units);
    }

    public void setUnits(Collection<String> units) {
        _units.clear();
        _units.addAll(units);
    }

    public Airman units(Collection<String> units) {
        setUnits(units);
        return this;
    }

    public Airman unit(String unit) {
        _units.add(unit);
        return this;
    }

    @JsonView(value = {Views.Storage.class, Views.Detail.class})
    public ImmutableSet<String> getImageUrls() {
        return ImmutableSet.copyOf(_imageUrls);
    }

    public void setImageUrls(Collection<String> imageUrls) {
        _imageUrls.clear();
        _imageUrls.addAll(imageUrls);
    }

    public Airman imageUrls(Collection<String> imageUrls) {
        setImageUrls(imageUrls);
        return this;
    }

    public Airman imageUrl(String imageUrl) {
        _imageUrls.add(imageUrl);
        return this;
    }

    @Override
    public void sanitize() {
        super.sanitize();
        sanitizeStrings(_ranks);
        sanitizeStrings(_units);
        sanitizeStrings(_roles);
        sanitizeStrings(_notes);
        sanitizeStrings(_imageUrls);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(getClass())
                .add("fullName", getFullName())
                .add("ranks", getRanks())
                .add("units", getUnits())
                .add("roles", getRoles())
                .add("notes", getNotes())
                .add("imageUrls", getImageUrls())
                .toString();
    }

    @Override
    public int compareTo(Airman rhs) {

        if (rhs == null) {
            return -1;
        }

        return getFullName().compareTo(rhs.getFullName());
    }
}
