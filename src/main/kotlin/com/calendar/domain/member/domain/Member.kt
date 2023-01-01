package com.calendar.domain.member.domain

import jakarta.persistence.*

@Entity
class Member constructor(
        @Column(columnDefinition = "varchar(100)")
        var id: String,

        @Column(columnDefinition = "varchar(100)")
        var name: String,

        @Column(columnDefinition = "varchar(100)")
        val password: String,

        @Column(columnDefinition = "varchar(100)")
        var phoneNumber: String,

        @Column(columnDefinition = "varchar(2000)")
        var address: String,

        @Column(columnDefinition = "varchar(100)")
        var addressDetail: String,

        @Column(columnDefinition = "varchar(100)")
        var email: String,

        @Column(columnDefinition = "varchar(20)")
        @Enumerated(EnumType.STRING)
        val role: Role,

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val key: Long? = null
){

}