package com.chenxiaoxiong.asrules.utils;

import io.lettuce.core.RedisConnectionException;
import redis.clients.jedis.Jedis;

import java.util.*;

public class Cedis implements CedisIntr {
    private Jedis jedis;

    public Cedis() {
        jedis = new Jedis("localhost", 6379);
        if (!jedis.ping().equals("PONG")) throw new RedisConnectionException("CONNECT FAILED");
    }

    @Override
    public AssociateRules[] findAssociateRulesByAntecedentAndConsequent(String[] antecedentInput, String consequentInput, float similarityRatio, Type type) {
        // 根据不同的输入类型读取不同的数据
        Set<String> keys;
        switch (type) {
            case KY_GPA:
                keys = jedis.keys("ky_gpa_rule*");
                break;
            case KY_SCORE:
                keys = jedis.keys("ky_score_rule*");
                break;
            case RD_GPA:
                keys = jedis.keys("rd_gpa_rule*");
                break;
            case RD_SCORE:
                keys = jedis.keys("rd_score_rule*");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        String _antecedentInput = arrToStr(antecedentInput, ",");
        Levenshtein levenshtein = new Levenshtein();

        TreeMap<Float, AssociateRules> associateRulesTreeMap = new TreeMap<>();

        // 将所有结果等于输入结果的规则写入到集合中
        Iterator<String> keysIterator = keys.iterator();
        while (keysIterator.hasNext()) {
            String key = keysIterator.next();
            String antecedentStr = jedis.hget(key, "antecedent");
            String confidenceStr = jedis.hget(key, "confidence");
            String consequentStr = jedis.hget(key, "consequent");

            if (consequentStr.equals(consequentInput)) {
                AssociateRules associateRule = new AssociateRules();
                associateRule.setAntecedent(strConvertToArr(antecedentStr, ","));
                associateRule.setConfidence(Double.valueOf(confidenceStr));
                associateRule.setConsequent(consequentStr);
                float _similarityRatio = levenshtein.getSimilarityRatio(arrToStr(antecedentInput, ","), antecedentStr);
                associateRulesTreeMap.put(_similarityRatio, associateRule);
            }
        }

        // 过滤出和输入值匹配度高的

        return middleConvert(associateRulesTreeMap, similarityRatio, type);
    }

    private String[] strConvertToArr(String str, String divider) {
        return str.split(divider);
    }

    private String arrToStr(String[] arr, String divider) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s: arr) {
            stringBuilder.append(s);
            stringBuilder.append(divider);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    // 在所有规则集合中取出最高和最低置信度的规则
    private AssociateRules[] middleConvert(TreeMap<Float, AssociateRules> middleResult, float similarityRatio, Type type) {
        Iterator<Map.Entry<Float, AssociateRules>> entryIterator = middleResult.entrySet().iterator();
        ArrayList<AssociateRules> associateRulesList = new ArrayList<>();

        if (type == Type.KY_GPA || type == Type.KY_SCORE) {
            TreeSet<AssociateRules> normalSchoolSet = new TreeSet<>();
            TreeSet<AssociateRules> fanmousSchoolSet = new TreeSet<>();

            while (entryIterator.hasNext()) {
                Map.Entry<Float, AssociateRules> entry = entryIterator.next();
                AssociateRules associateRules = entry.getValue();
                float _similarityRatio = entry.getKey();
                if (_similarityRatio >= similarityRatio) { // 如果满足输入相似度阈值
                    if (associateRules.getConsequent().equals("名校")) {
                        fanmousSchoolSet.add(associateRules);
                    } else {
                        normalSchoolSet.add(associateRules);
                    }
                }
            }
            if (normalSchoolSet.size() > 2) {
                associateRulesList.add(normalSchoolSet.first());
                associateRulesList.add(normalSchoolSet.last());
            } else {
                associateRulesList.addAll(normalSchoolSet);
            }
            if (fanmousSchoolSet.size() > 2) {
                associateRulesList.add(fanmousSchoolSet.first());
                associateRulesList.add(fanmousSchoolSet.last());
            } else {
                associateRulesList.addAll(fanmousSchoolSet);
            }
        } else {
            TreeSet<AssociateRules> rdSet = new TreeSet<>();
            while (entryIterator.hasNext()) {
                Map.Entry<Float, AssociateRules> entry = entryIterator.next();
                AssociateRules associateRules = entry.getValue();
                float _similarityRatio = entry.getKey();
                if (_similarityRatio >= similarityRatio) { // 如果满足输入相似度阈值
                    rdSet.add(associateRules);
                }
            }
            if (rdSet.size() > 2) {
                associateRulesList.add(rdSet.first());
                associateRulesList.add(rdSet.last());
            } else {
                associateRulesList.addAll(rdSet);
            }

        }
        AssociateRules[] result = new AssociateRules[associateRulesList.size()];
        for (int i=0; i<result.length; i++) {
            result[i] = associateRulesList.get(i);
        }
        return result;
    }

}
