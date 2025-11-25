package com.cloud.domain.resource

import com.cloud.domain.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "resources")
@Inheritance(strategy = InheritanceType.JOINED)
abstract class Resource(
    @Column(nullable = false, unique = true, length = 100)
    var resourceId: String,

    @Column(nullable = false, length = 200)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    var type: ResourceType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    var status: ResourceStatus = ResourceStatus.CREATING,

    @Column(nullable = false, length = 100)
    var ownerId: String,

    @Column(length = 1000)
    var description: String? = null,

    @OneToMany(mappedBy = "resource", cascade = [CascadeType.ALL], orphanRemoval = true)
    var tags: MutableSet<ResourceTag> = mutableSetOf()
) : BaseEntity() {

    fun addTag(key: String, value: String) {
        tags.add(ResourceTag(key = key, value = value, resource = this))
    }

    fun removeTag(key: String) {
        tags.removeIf { it.key == key }
    }

    fun getTag(key: String): String? {
        return tags.find { it.key == key }?.value
    }

    fun hasTag(key: String, value: String): Boolean {
        return tags.any { it.key == key && it.value == value }
    }
}
