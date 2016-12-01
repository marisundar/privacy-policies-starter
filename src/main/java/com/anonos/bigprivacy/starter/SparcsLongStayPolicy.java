package com.anonos.bigprivacy.starter;

import com.anonos.bigprivacy.privacypolicyapi.*;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * State Payment Method A-DDID policy.
 */
@LoadablePrivacyPolicy
public class SparcsLongStayPolicy implements PrivacyPolicy {

    /**
     * Rule storage.
     */
    private static final ThreadLocal<List<PrivacyPolicyRule>> rules = new ThreadLocal<List<PrivacyPolicyRule>>() {
        @Override
        protected List<PrivacyPolicyRule> initialValue() {
            final List<PrivacyPolicyRule> rules = new ArrayList<>();
            rules.add(new StatePaymentMethodRule());
            return rules;
        }
    };

    @Override
    public String getId() {
        return "4a3e9b64-6253-4fc5-bf64-60536c8b3c86";
    }

    @Override
    public String getName() {
        return "SPARCS Generalize Length of Stay";
    }

    @Override
    public String getDescription() {
        return "Examines stay length and generalizes it to 'Long' or 'Short'";
    }

    @Override
    public Set<DataType> getInputTypes() {
        return Sets.newHashSet(DataType.TEXT);
    }

    @Override
    public List<PrivacyPolicyRule> getRules() {
        return rules.get();
    }

    protected static class StatePaymentMethodRule implements PrivacyPolicyRule {
        @Override
        public String getName() {
            return "StayLength";
        }

        @Override
        public String getResult(final Object input, final List<Field> fields, final List<Object> row) throws RuleExecutionException {
            if (input == null)
                return null;

            // Convert the input value from a string to an integer
            final int stayLength;
            try {
                stayLength = Integer.parseInt((String)input);
            }
            catch (final NumberFormatException e) {
                // The input value isn't an integer so we can't use it
                return null;
            }

            return stayLength >= 4 ? "Long" : "Short";
        }
    }
}
