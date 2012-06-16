package com.thompson234.bg95.model;

import com.google.common.base.Strings;

public class AircraftNumber {

    private String _serialNumber;

    public AircraftNumber(String number) {

        if (Strings.isNullOrEmpty(number)) {
            throw new IllegalArgumentException();
        }

        if (number.contains("-")) {
            _serialNumber = number;
        } else {
            final StringBuilder builder = new StringBuilder("4");  //All AC manufactured in 40s?
            builder.append(number);
            builder.insert(2, "-");
            _serialNumber = builder.toString();
        }
    }

    public String getSerialNumber() {
        return _serialNumber;
    }

    public String getShorthand() {
        return getSerialNumber().substring(1).replaceAll("-", "");
    }

    public String getSearchForm() {

        final String sn = getSerialNumber();
        return (sn.length() > 3) ? sn.substring(sn.length() - 4) : sn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AircraftNumber that = (AircraftNumber) o;

        if (_serialNumber != null ? !_serialNumber.equals(that._serialNumber) : that._serialNumber != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return _serialNumber != null ? _serialNumber.hashCode() : 0;
    }

    public String toString() {
        return getSerialNumber();
    }
}
