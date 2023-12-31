package com.priyanshu.springwebfluxbffapp.proxy.model;

import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class FilterDef {

    @NotNull
    private String name;

    private List<String> args = new LinkedList<>();

    public FilterDef(String text) {
        int eqIdx = text.indexOf('=');
        if (eqIdx <= 0) {
            throw new ValidationException("Unable to parse Predicate text '" + text + "'" + ", must be of the form name=value");
        }
        this.name=text.substring(0, eqIdx);

        String[] args = StringUtils.tokenizeToStringArray(text.substring(eqIdx + 1), ",");

        for (int i = 0; i < args.length; i++) {
            this.args.add(args[i]);
        }
    }

    public String getName() {
        return name;
    }

    public List<String> getArgs() {
        return args;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FilterDef that = (FilterDef) o;
        return Objects.equals(name, that.name) && Objects.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, args);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Filter{");
        sb.append("name='").append(name).append('\'');
        sb.append(", args=").append(args);
        sb.append('}');
        return sb.toString();
    }

}
