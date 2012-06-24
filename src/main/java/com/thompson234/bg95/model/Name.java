package com.thompson234.bg95.model;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.ListIterator;

public class Name implements Comparable<Name> {

    private static final ImmutableMap<String, String> SALUTATION_MAP =
            ImmutableMap.<String, String>builder().put("mr", "Mr.")
                    .put("mister", "Mr.")
                    .put("master", "Mr.")
                    .put("mrs", "Mrs.")
                    .put("miss", "Ms.")
                    .put("ms", "Ms.")
                    .put("dr", "Dr.")
                    .put("rev", "Rev.")
                    .put("fr", "Fr.").build();

    private static final ImmutableMap<String, String> SUFFIX_MAP =
            ImmutableMap.<String, String>builder().put("i", "I")
                    .put("ii", "II")
                    .put("iii", "III")
                    .put("iv", "IV")
                    .put("v", "V")
                    .put("senior", "Senior")
                    .put("junior", "Junior")
                    .put("sr", "Sr.")
                    .put("jr", "Jr.")
                    .put("phd", "PhD")
                    .put("apr", "APR")
                    .put("rph", "RPh")
                    .put("pe", "PE")
                    .put("md", "MD")
                    .put("ma", "MA")
                    .put("dmd", "DMD")
                    .put("cme", "CME").build();

    private static final ImmutableMap<String, String> COMPOUND_LAST_NAME_MAP =
            ImmutableMap.<String, String>builder().put("vere", "Vere")
                    .put("von", "Von")
                    .put("van", "Van")
                    .put("de", "De")
                    .put("del", "Del")
                    .put("della", "Della")
                    .put("di", "Di")
                    .put("da", "Da")
                    .put("pietro", "Pietro")
                    .put("vanden", "Vanden")
                    .put("du", "Du")
                    .put("st", "St.")
                    .put("la", "La")
                    .put("ter", "Ter").build();

    private static final String NMI = "(NMI)";
    private static final String FNU = "(FNU)";

    private String _salutation;
    private String _firstName;
    private String _lastName;
    private String _middle;
    private List<String> _suffixes = Lists.newArrayList();

    public Name() {

    }

    public Name(String fullName) {
        setFullName(fullName);
    }

    protected boolean isInitial(String candidate) {
        return candidate != null && candidate.length() == 2 && candidate.charAt(1) == '.';
    }

    public String getFullName() {
        String firstName = getFirstName();

        if (firstName != null && firstName.length() == 1) {
            firstName = firstName + ".";
        }

        String middle = getMiddle();

        if (middle != null && middle.length() == 1) {
            middle = middle + ".";
        }

        final Joiner joiner = Joiner.on(" ").skipNulls();
        final String partial = joiner.join(getSalutation(), firstName, middle, getLastName());
        final String suffix = Strings.emptyToNull(joiner.join(getSuffixes()));
        return joiner.join(partial, suffix);
    }

    public void setFullName(String fullName) {
        final String sanitized = fullName.replace("?", "").replace(" .", "").trim();
        if (Strings.isNullOrEmpty(sanitized)) {
            throw new IllegalArgumentException(fullName);
        }

        final List<String> tokens = Lists.newArrayList(sanitized.split(" "));

        //extract salutation, if any
        final String salutation = salutationFromToken(tokens.get(0));
        if (salutation != null) {
            salutation(salutation);
            tokens.remove(0);
        }

        //extract suffixes, if any
        ListIterator<String> suffixIt = tokens.listIterator(tokens.size());
        while (suffixIt.hasPrevious()) {
            final String suffix = suffixFromToken(suffixIt.previous());

            if (suffix != null) {
                suffix(0, suffix);
                suffixIt.remove();
            } else {
                break;
            }
        }

        //First? Middle? Last should be all that's left
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException(fullName);
        }

        //lastNameFromTokens will modify this list.
        lastName(lastNameFromTokens(tokens));

        if (tokens.isEmpty()) {
            return;
        }

        firstName(tokens.remove(0));

        if (!tokens.isEmpty()) {
            middle(Joiner.on(" ").join(tokens));
        }
    }

    public Name fullName(String fullName) {
        setFullName(fullName);
        return this;
    }

    private String lastNameFromTokens(List<String> tokens) {

        if (tokens == null || tokens.isEmpty()) {
            throw new IllegalArgumentException(Joiner.on(" ").skipNulls().join(tokens));
        }

        String lastName = tokens.remove(tokens.size() - 1);

        if (!tokens.isEmpty()) {

            final String compoundCandidate = tokens.get(tokens.size() - 1).replace(".", "").toLowerCase();
            if (COMPOUND_LAST_NAME_MAP.containsKey(compoundCandidate)) {
                lastName = COMPOUND_LAST_NAME_MAP.get(compoundCandidate) + " " + lastName;
                tokens.remove(tokens.size() - 1);
            }
        }

        return lastName;
    }

    private String salutationFromToken(String token) {

        if (token == null) {
            return null;
        }

        final String sanitized = token.replace(".", "").toLowerCase();
        return SALUTATION_MAP.get(token);
    }

    private String suffixFromToken(String token) {

        if (token == null) {
            return null;
        }

        final String sanitized = token.replace(".", "").toLowerCase();
        return SUFFIX_MAP.get(sanitized);
    }

    public String getSalutation() {
        return _salutation;
    }

    public void setSalutation(String salutation) {
        _salutation = salutation;
    }

    public Name salutation(String salutation) {
        setSalutation(salutation);
        return this;
    }

    public String getFirstName() {
        return _firstName;
    }

    protected String sanitizeInitials(String namePart) {

        String sanitized = namePart;

        if (sanitized != null) {
            sanitized = Strings.emptyToNull(namePart.replace(".", ""));
        }

        return sanitized;
    }

    public void setFirstName(String firstName) {

        if (StringUtils.equalsIgnoreCase(FNU, firstName)) {
            _firstName = null;
        } else {
            _firstName = sanitizeInitials(firstName);
        }
    }

    public Name firstName(String firstName) {
        setFirstName(firstName);
        return this;
    }

    public String getLastName() {
        return _lastName;
    }

    public void setLastName(String lastName) {
        _lastName = lastName;
    }

    public Name lastName(String lastName) {
        setLastName(lastName);
        return this;
    }

    public String getMiddle() {
        return _middle;
    }

    public void setMiddle(String middle) {

        if (StringUtils.equalsIgnoreCase(NMI, middle)) {
            _middle = null;
        } else {
            _middle = sanitizeInitials(middle);
        }
    }

    public Name middle(String middle) {
        setMiddle(middle);
        return this;
    }

    public ImmutableList<String> getSuffixes() {
        return ImmutableList.copyOf(_suffixes);
    }

    public void setSuffixes(List<String> suffixes) {
        _suffixes.clear();
        _suffixes.addAll(suffixes);
    }

    public void addSuffix(String suffix) {
        _suffixes.add(suffix);
    }

    private void addSuffix(int index, String suffix) {
        _suffixes.add(index, suffix);
    }

    public Name suffixes(List<String> suffixes) {
        setSuffixes(suffixes);
        return this;
    }

    public Name suffix(String suffix) {
        addSuffix(suffix);
        return this;
    }

    private Name suffix(int index, String suffix) {
        addSuffix(index, suffix);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Name name = (Name) o;

        if (_firstName != null ? !_firstName.equals(name._firstName) : name._firstName != null) return false;
        if (_lastName != null ? !_lastName.equals(name._lastName) : name._lastName != null) return false;
        if (_middle != null ? !_middle.equals(name._middle) : name._middle != null) return false;
        if (_salutation != null ? !_salutation.equals(name._salutation) : name._salutation != null) return false;
        if (_suffixes != null ? !_suffixes.equals(name._suffixes) : name._suffixes != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _salutation != null ? _salutation.hashCode() : 0;
        result = 31 * result + (_firstName != null ? _firstName.hashCode() : 0);
        result = 31 * result + (_lastName != null ? _lastName.hashCode() : 0);
        result = 31 * result + (_middle != null ? _middle.hashCode() : 0);
        result = 31 * result + (_suffixes != null ? _suffixes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return getFullName();
    }

    @Override
    public int compareTo(Name rhs) {
        return getFullName().compareTo(rhs.getFullName());
    }

    public boolean softMatch(Name rhs) {
        return softMatch(rhs, true);
    }

    public boolean softMatch(Name rhs, boolean ignoreCase) {

        if (rhs == null) {
            return false;
        }

        if (this.equals(rhs)) {
            return true;
        }

        final String lhsLastName = Strings.nullToEmpty(getLastName());
        final String rhsLastName = Strings.nullToEmpty(rhs.getLastName());

        if ((ignoreCase && !StringUtils.equalsIgnoreCase(lhsLastName, rhsLastName)) ||
                !StringUtils.equals(lhsLastName, rhsLastName)) {
            return false;
        }

        final String lhsFirstName = Strings.nullToEmpty(getFirstName());
        final String rhsFirstName = Strings.nullToEmpty(rhs.getFirstName());

        if ((ignoreCase && !StringUtils.equalsIgnoreCase(lhsFirstName, rhsFirstName)) ||
                !StringUtils.equals(lhsFirstName, rhsFirstName)) {
            return false;
        }

        final String lhsMiddle = Strings.nullToEmpty(getMiddle());
        final String rhsMiddle = Strings.nullToEmpty(rhs.getMiddle());

        if ((ignoreCase && StringUtils.equalsIgnoreCase(lhsMiddle, rhsMiddle)) ||
                StringUtils.equals(lhsMiddle, rhsMiddle)) {
            return true;
        }

        if (Strings.isNullOrEmpty(lhsMiddle) || Strings.isNullOrEmpty(rhsMiddle)) {
            return true;
        }

        //Check for middle initial on one and full in other
        String mi = null;
        String fullMiddle = null;
        if (lhsMiddle.length() == 1) {
            mi = lhsMiddle;
            fullMiddle = rhsMiddle;
        } else if (rhsMiddle.length() == 1) {
            mi = rhsMiddle;
            fullMiddle = lhsMiddle;
        }

        if (mi != null && fullMiddle != null && fullMiddle.startsWith(mi)) {
            return true;
        }

        final String lhsMiddleCollapsed = lhsMiddle.replace(" ", "");
        final String rhsMiddleCollapsed = rhsMiddle.replace(" ", "");

        if ((ignoreCase && StringUtils.equalsIgnoreCase(lhsMiddleCollapsed, rhsMiddleCollapsed)) ||
                StringUtils.equals(lhsMiddleCollapsed, rhsMiddleCollapsed)) {
            return true;
        }

        return false;
    }
}
