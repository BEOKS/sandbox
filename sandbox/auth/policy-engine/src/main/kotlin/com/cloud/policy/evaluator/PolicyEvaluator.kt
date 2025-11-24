package com.cloud.policy.evaluator

import com.cloud.policy.model.EvaluationContext
import com.cloud.policy.model.PolicyDecision

interface PolicyEvaluator {
    /**
     * 정책을 평가하여 접근 허가 여부를 결정
     */
    fun evaluate(context: EvaluationContext): PolicyDecision

    /**
     * 특정 액션에 대한 접근 가능 여부를 확인
     */
    fun isAllowed(context: EvaluationContext): Boolean {
        return evaluate(context).allowed
    }
}
