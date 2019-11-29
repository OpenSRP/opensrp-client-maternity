package org.smartregister.opd.helper;

import android.support.annotation.NonNull;

import com.vijay.jsonwizard.rules.RuleConstant;
import com.vijay.jsonwizard.rules.RulesEngineHelper;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.RulesEngineParameters;
import org.jeasy.rules.mvel.MVELRule;

import java.util.UUID;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-30
 */
public class OpdRulesEngineHelper extends RulesEngineHelper {

    private RulesEngine defaultRulesEngine;

    public OpdRulesEngineHelper() {
        RulesEngineParameters parameters = new RulesEngineParameters().skipOnFirstAppliedRule(true);
        this.defaultRulesEngine = new DefaultRulesEngine(parameters);
    }

    protected void processDefaultRules(@NonNull Rules rules, @NonNull Facts facts) {
        defaultRulesEngine.fire(rules, facts);
    }

    public boolean getRelevance(@NonNull Facts relevanceFacts, @NonNull String rule) {
        relevanceFacts.put("helper", this);
        relevanceFacts.put(RuleConstant.IS_RELEVANT, false);

        Rules rules = new Rules();
        Rule mvelRule = new MVELRule().name(UUID.randomUUID().toString()).when(rule).then("isRelevant = true;");
        rules.register(mvelRule);

        processDefaultRules(rules, relevanceFacts);

        return relevanceFacts.get(RuleConstant.IS_RELEVANT);
    }
}