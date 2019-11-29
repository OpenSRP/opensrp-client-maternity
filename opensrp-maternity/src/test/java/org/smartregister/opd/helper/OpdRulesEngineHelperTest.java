package org.smartregister.opd.helper;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.robolectric.util.ReflectionHelpers;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-17
 */

@RunWith(MockitoJUnitRunner.class)
public class OpdRulesEngineHelperTest {

    @Test
    public void processDefaultRulesShouldCallRulesEngineFire() {
        OpdRulesEngineHelper opdRulesEngineHelper = new OpdRulesEngineHelper();
        RulesEngine defaultRulesEngine = Mockito.mock(RulesEngine.class);

        ReflectionHelpers.setField(opdRulesEngineHelper, "defaultRulesEngine", defaultRulesEngine);

        Rules rules = Mockito.mock(Rules.class);
        Facts facts = Mockito.mock(Facts.class);

        opdRulesEngineHelper.processDefaultRules(rules, facts);

        Mockito.verify(defaultRulesEngine, Mockito.times(1))
                .fire(Mockito.eq(rules), Mockito.eq(facts));
    }

    @Test
    public void getRelevanceShouldPerformRelevanceCheck() {
        OpdRulesEngineHelper opdRulesEngineHelper = new OpdRulesEngineHelper();
        Facts facts = new Facts();
        facts.put("gender", "male");

        Assert.assertTrue(opdRulesEngineHelper.getRelevance(facts, "gender == 'male'"));
    }
}