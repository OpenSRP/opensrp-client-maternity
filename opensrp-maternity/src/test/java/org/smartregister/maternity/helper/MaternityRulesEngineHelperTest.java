package org.smartregister.maternity.helper;

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
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

@RunWith(MockitoJUnitRunner.class)
public class MaternityRulesEngineHelperTest {

    @Test
    public void processDefaultRulesShouldCallRulesEngineFire() {
        MaternityRulesEngineHelper maternityRulesEngineHelper = new MaternityRulesEngineHelper();
        RulesEngine defaultRulesEngine = Mockito.mock(RulesEngine.class);

        ReflectionHelpers.setField(maternityRulesEngineHelper, "defaultRulesEngine", defaultRulesEngine);

        Rules rules = Mockito.mock(Rules.class);
        Facts facts = Mockito.mock(Facts.class);

        maternityRulesEngineHelper.processDefaultRules(rules, facts);

        Mockito.verify(defaultRulesEngine, Mockito.times(1))
                .fire(Mockito.eq(rules), Mockito.eq(facts));
    }

    @Test
    public void getRelevanceShouldPerformRelevanceCheck() {
        MaternityRulesEngineHelper maternityRulesEngineHelper = new MaternityRulesEngineHelper();
        Facts facts = new Facts();
        facts.put("gender", "male");

        Assert.assertTrue(maternityRulesEngineHelper.getRelevance(facts, "gender == 'male'"));
    }
}