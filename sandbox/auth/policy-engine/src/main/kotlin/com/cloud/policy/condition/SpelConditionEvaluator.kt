package com.cloud.policy.condition

import com.cloud.policy.model.EvaluationContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class SpelConditionEvaluator(
    private val objectMapper: ObjectMapper = ObjectMapper()
) : ConditionEvaluator {

    private val parser = SpelExpressionParser()

    override fun evaluate(condition: String, context: EvaluationContext): Boolean {
        return try {
            // JSON 조건을 파싱
            val conditionMap: Map<String, Any> = objectMapper.readValue(condition)

            // SpEL 평가 컨텍스트 생성
            val spelContext = StandardEvaluationContext().apply {
                setVariable("subject", context.subject)
                setVariable("resource", context.resource)
                setVariable("action", context.action)
                setVariable("environment", context.environment)
            }

            // 모든 조건을 AND로 평가
            conditionMap.all { (key, value) ->
                evaluateConditionEntry(key, value, spelContext)
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to evaluate condition: $condition" }
            false
        }
    }

    private fun evaluateConditionEntry(
        key: String,
        value: Any,
        spelContext: StandardEvaluationContext
    ): Boolean {
        return when (key) {
            "StringEquals" -> evaluateStringEquals(value, spelContext)
            "StringLike" -> evaluateStringLike(value, spelContext)
            "NumericEquals" -> evaluateNumericEquals(value, spelContext)
            "NumericGreaterThan" -> evaluateNumericGreaterThan(value, spelContext)
            "NumericLessThan" -> evaluateNumericLessThan(value, spelContext)
            "Bool" -> evaluateBool(value, spelContext)
            "IpAddress" -> evaluateIpAddress(value, spelContext)
            else -> {
                logger.warn { "Unknown condition operator: $key" }
                false
            }
        }
    }

    private fun evaluateStringEquals(value: Any, spelContext: StandardEvaluationContext): Boolean {
        val conditions = value as? Map<*, *> ?: return false
        return conditions.all { (variable, expected) ->
            val expression = parser.parseExpression(variable as String)
            val actual = expression.getValue(spelContext)?.toString()
            actual == expected.toString()
        }
    }

    private fun evaluateStringLike(value: Any, spelContext: StandardEvaluationContext): Boolean {
        val conditions = value as? Map<*, *> ?: return false
        return conditions.all { (variable, pattern) ->
            val expression = parser.parseExpression(variable as String)
            val actual = expression.getValue(spelContext)?.toString()
            val regex = (pattern as String).replace("*", ".*").toRegex()
            actual != null && regex.matches(actual)
        }
    }

    private fun evaluateNumericEquals(value: Any, spelContext: StandardEvaluationContext): Boolean {
        val conditions = value as? Map<*, *> ?: return false
        return conditions.all { (variable, expected) ->
            val expression = parser.parseExpression(variable as String)
            val actual = expression.getValue(spelContext) as? Number
            actual?.toDouble() == (expected as Number).toDouble()
        }
    }

    private fun evaluateNumericGreaterThan(value: Any, spelContext: StandardEvaluationContext): Boolean {
        val conditions = value as? Map<*, *> ?: return false
        return conditions.all { (variable, threshold) ->
            val expression = parser.parseExpression(variable as String)
            val actual = expression.getValue(spelContext) as? Number
            actual != null && actual.toDouble() > (threshold as Number).toDouble()
        }
    }

    private fun evaluateNumericLessThan(value: Any, spelContext: StandardEvaluationContext): Boolean {
        val conditions = value as? Map<*, *> ?: return false
        return conditions.all { (variable, threshold) ->
            val expression = parser.parseExpression(variable as String)
            val actual = expression.getValue(spelContext) as? Number
            actual != null && actual.toDouble() < (threshold as Number).toDouble()
        }
    }

    private fun evaluateBool(value: Any, spelContext: StandardEvaluationContext): Boolean {
        val conditions = value as? Map<*, *> ?: return false
        return conditions.all { (variable, expected) ->
            val expression = parser.parseExpression(variable as String)
            val actual = expression.getValue(spelContext) as? Boolean
            actual == expected as Boolean
        }
    }

    private fun evaluateIpAddress(value: Any, spelContext: StandardEvaluationContext): Boolean {
        // IP 주소 범위 체크 (CIDR 및 IP 대역 매칭 지원)
        val conditions = value as? Map<*, *> ?: return false
        return conditions.all { (variable, cidrOrPattern) ->
            val expression = parser.parseExpression(variable as String)
            val actual = expression.getValue(spelContext)?.toString() ?: return@all false
            val pattern = cidrOrPattern.toString()
            
            // CIDR 형식 (예: 192.168.1.0/24) 또는 IP 대역 패턴 (예: 192.*.*.*)
            when {
                pattern.contains("/") -> matchesCidr(actual, pattern)
                pattern.contains("*") -> matchesIpPattern(actual, pattern)
                else -> actual == pattern
            }
        }
    }
    
    private fun matchesCidr(ip: String, cidr: String): Boolean {
        return try {
            val parts = cidr.split("/")
            if (parts.size != 2) return false
            
            val cidrIp = parts[0]
            val prefixLength = parts[1].toInt()
            
            val ipBytes = ipToLong(ip)
            val cidrBytes = ipToLong(cidrIp)
            val mask = (0xFFFFFFFFL shl (32 - prefixLength)) and 0xFFFFFFFFL
            
            (ipBytes and mask) == (cidrBytes and mask)
        } catch (e: Exception) {
            false
        }
    }
    
    private fun matchesIpPattern(ip: String, pattern: String): Boolean {
        val regex = pattern
            .replace(".", "\\.")
            .replace("*", "\\d+")
            .toRegex()
        return regex.matches(ip)
    }
    
    private fun ipToLong(ip: String): Long {
        val parts = ip.split(".").map { it.toLong() }
        return (parts[0] shl 24) + (parts[1] shl 16) + (parts[2] shl 8) + parts[3]
    }
}
