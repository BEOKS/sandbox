package com.cloud.public.security.certificate

import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.security.cert.X509Certificate
import java.time.Instant
import java.util.*

private val logger = KotlinLogging.logger {}

@Component
class CertificateValidator {

    fun validate(certificate: X509Certificate): Boolean {
        try {
            // Check validity period
            certificate.checkValidity(Date.from(Instant.now()))

            // Check if certificate is expired
            if (certificate.notAfter.before(Date())) {
                logger.warn { "Certificate is expired: ${certificate.subjectX500Principal}" }
                return false
            }

            // Additional validation can be added here
            // - Check issuer
            // - Check revocation status (OCSP/CRL)
            // - Check key usage

            logger.debug { "Certificate validation successful: ${certificate.subjectX500Principal}" }
            return true

        } catch (e: Exception) {
            logger.error(e) { "Certificate validation failed: ${certificate.subjectX500Principal}" }
            return false
        }
    }

    fun extractTenantId(certificate: X509Certificate): String {
        // Extract tenant ID from certificate
        // Option 1: From Organization (O) field
        val subject = certificate.subjectX500Principal.name
        val orgMatch = Regex("O=([^,]+)").find(subject)

        if (orgMatch != null) {
            val organization = orgMatch.groupValues[1]
            logger.debug { "Extracted organization from certificate: $organization" }
            return organization.replace(" ", "_").lowercase()
        }

        // Option 2: From Common Name (CN) field
        val cnMatch = Regex("CN=([^,]+)").find(subject)
        if (cnMatch != null) {
            val commonName = cnMatch.groupValues[1]
            logger.debug { "Extracted CN from certificate: $commonName" }
            return commonName.replace(" ", "_").lowercase()
        }

        throw IllegalArgumentException("Could not extract tenant ID from certificate")
    }

    fun extractUsername(certificate: X509Certificate): String {
        val subject = certificate.subjectX500Principal.name

        // Extract username from CN
        val cnMatch = Regex("CN=([^,]+)").find(subject)
        return cnMatch?.groupValues?.get(1) ?: "unknown"
    }

    fun checkRevocation(certificate: X509Certificate): Boolean {
        // TODO: Implement OCSP or CRL check
        // For now, return true (not revoked)
        logger.debug { "Certificate revocation check not implemented yet" }
        return true
    }
}
