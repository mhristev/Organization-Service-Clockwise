package com.example.demo

import com.clockwise.orgservice.OrgserviceApplication
import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<OrgserviceApplication>().with(TestcontainersConfiguration::class).run(*args)
}
