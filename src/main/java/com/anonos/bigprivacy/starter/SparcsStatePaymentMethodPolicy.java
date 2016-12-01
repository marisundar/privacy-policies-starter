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
public class SparcsStatePaymentMethodPolicy implements PrivacyPolicy {

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
        return "822c3773-11dd-47ba-abee-d6327d3c707f";
    }

    @Override
    public String getName() {
        return "SPARCS Payment Method: State Y/N";
    }

    @Override
    public String getDescription() {
        return "Examines payment source fields and determines if any contain Medicare or Medicaid";
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
            return "StatePaymentMethod";
        }

        @Override
        public String getResult(final Object input, final List<Field> fields, final List<Object> row) {
            // Find fields whose names start with "source_of_payment_" and end with an
            // integer; record their indexes to use later.
            final List<Integer> paymentSourceFieldIndexes = new ArrayList<>();
            for (int i = 0; i < fields.size(); ++i) {
                if (fields.get(i).getName().matches("^source_of_payment_\\d+$")) {
                    paymentSourceFieldIndexes.add(i);
                }
            }

            // Examine the value at each payment source field.  If any are
            // state-run programs, set the state payment method flag and don't
            // check any of the other fields.
            boolean statePaymentMethod = false;
            for (final int index : paymentSourceFieldIndexes) {
                final String paymentSource = (String)row.get(index);
                if (paymentSource.equalsIgnoreCase("Medicaid")
                 || paymentSource.equalsIgnoreCase("Medicare")) {
                    statePaymentMethod = true;
                    break;
                }
            }

            // If we found a state payment method, return "Y".  Otherwise, return "N".
            return statePaymentMethod ? "Y" : "N";
        }
    }
}
