package com.chenxiaoxiong.asrules.Controller;

import com.chenxiaoxiong.asrules.utils.AssociateRules;
import com.chenxiaoxiong.asrules.utils.Cedis;
import com.chenxiaoxiong.asrules.utils.Type;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class AsRulesControlerRest {
    @PostMapping("/getAsRules")
    public AssociateRules[] getAsRules(HttpServletRequest request) {
        String antecedentInput = request.getParameter("antecedentInput");
        String consequentInput = request.getParameter("consequentInput");
        String similarityRatio = request.getParameter("similarityRatio");
        String type = request.getParameter("type");

        Cedis cedis = new Cedis();
        return cedis.findAssociateRulesByAntecedentAndConsequent(antecedentInput.split(","), consequentInput, Float.valueOf(similarityRatio), Type.valueOf(type));
    }
}
