package org.example.example

import org.example.entity.PersistentEntity
import org.example.entity.annotation.OneToMany

class Company(
    var name: String,
    @OneToMany(false)
    var employees : List<Employee>,
): PersistentEntity() {
}