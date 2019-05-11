package com.chenxiaoxiong.asrules.utils;

public interface CedisIntr {
    AssociateRules[] findAssociateRulesByAntecedentAndConsequent(String[] antecedentArr, String consequentInput, float similarityRatio, Type type);
}
