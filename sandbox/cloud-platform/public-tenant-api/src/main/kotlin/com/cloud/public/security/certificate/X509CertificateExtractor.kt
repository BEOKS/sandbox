package com.cloud.public.security.certificate

import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.security.cert.X509Certificate

private val logger = KotlinLogging.logger {}

@Component
class X509CertificateExtractor {

    companion object {
        const val CERTIFICATE_ATTRIBUTE = "jakarta.servlet.request.X509Certificate"
        const val CERTIFICATE_HEADER = "X-Client-Cert"
    }

    fun extractCertificate(request: HttpServletRequest): X509Certificate? {
        // Try to get certificate from request attribute (mTLS)
        val certificates = request.getAttribute(CERTIFICATE_ATTRIBUTE) as? Array<*>

        if (certificates != null && certificates.isNotEmpty()) {
            val cert = certificates[0] as? X509Certificate
            if (cert != null) {
                logger.debug { "Certificate extracted from request attribute" }
                return cert
            }
        }

        // Try to get certificate from header (proxy scenario)
        val certHeader = request.getHeader(CERTIFICATE_HEADER)
        if (certHeader != null) {
            logger.debug { "Certificate found in header (proxy scenario)" }
            // TODO: Parse certificate from header if needed
            // This would require additional logic to decode the certificate
        }

        logger.debug { "No client certificate found in request" }
        return null
    }
}
