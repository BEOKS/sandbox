package com.cloud.private.domain.storage

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/s3/buckets")
class S3Controller(
    private val s3Service: S3Service
) {

    @PostMapping
    fun createBucket(@RequestBody request: CreateBucketRequest): ResponseEntity<S3Bucket> {
        val bucket = s3Service.createBucket(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(bucket)
    }

    @GetMapping
    fun listBuckets(): ResponseEntity<List<S3Bucket>> {
        val buckets = s3Service.listBuckets()
        return ResponseEntity.ok(buckets)
    }

    @GetMapping("/{id}")
    fun getBucket(@PathVariable id: Long): ResponseEntity<S3Bucket> {
        val bucket = s3Service.getBucket(id)
        return ResponseEntity.ok(bucket)
    }

    @DeleteMapping("/{id}")
    fun deleteBucket(@PathVariable id: Long): ResponseEntity<Void> {
        s3Service.deleteBucket(id)
        return ResponseEntity.noContent().build()
    }
}
