package com.calendar.domain.member.domain

enum class Role(
        val key: String? = null,
        val title: String? = null
) {
    USER("ROLE_USER", "일반사용자"),
    ADMIN("ROLE_ADMIN", "관리자")
}