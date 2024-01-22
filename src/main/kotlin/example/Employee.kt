package org.example.example

import org.example.entity.PersistentEntity

class Employee(
    var name: String,
    var age: Int
): PersistentEntity() {
}