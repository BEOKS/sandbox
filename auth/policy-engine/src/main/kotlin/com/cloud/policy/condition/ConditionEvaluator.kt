package com.cloud.policy.condition

import com.cloud.policy.model.EvaluationContext

interface ConditionEvaluator {
    /**
     * 조건식을 평가
     */
    fun evaluate(condition: String, context: EvaluationContext): Boolean
}
