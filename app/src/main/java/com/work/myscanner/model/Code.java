package com.work.myscanner.model;

import java.util.Objects;

public class Code {
    private String mDefinition;
    private int mLetter;
    private int mNumber;

    public Code() {
    }

    public Code(String definition, int letter, int number) {
        mDefinition = definition;
        mLetter = letter;
        mNumber = number;
    }

    public String getDefinition() {
        return mDefinition;
    }

    public void setDefinition(String definition) {
        mDefinition = definition;
    }

    public int getLetter() {
        return mLetter;
    }

    public void setLetter(int letter) {
        mLetter = letter;
    }

    public int getNumber() {
        return mNumber;
    }

    public void setNumber(int number) {
        mNumber = number;
    }

    @Override
    public String toString() {
        return "Code{" +
                "mDefinition='" + mDefinition + '\'' +
                ", mLetter=" + mLetter +
                ", mNumber=" + mNumber +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Code code = (Code) o;
        return Objects.equals(mDefinition, code.mDefinition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mDefinition);
    }
}
