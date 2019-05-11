package com.chenxiaoxiong.asrules.utils;

import java.util.Arrays;
import java.util.Comparator;

public class AssociateRules implements Comparable<AssociateRules>{
    private String[] antecedent;
    private String consequent;
    private Double confidence;

    public String[] getAntecedent() {
        return antecedent;
    }

    public void setAntecedent(String[] antecedent) {
        this.antecedent = antecedent;
    }

    public String getConsequent() {
        return consequent;
    }

    public void setConsequent(String consequent) {
        this.consequent = consequent;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    @Override
    public int compareTo(AssociateRules o) {
        return this.confidence.compareTo(o.confidence);
    }

    @Override
    public String toString() {
        return "AssociateRules[antecedent=" + Arrays.toString(antecedent) + ", consequent=" + this.consequent + ", confidence=" + this.confidence + "]";
    }
}
