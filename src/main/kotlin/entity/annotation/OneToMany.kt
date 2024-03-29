package org.example.entity.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class OneToMany(
    val cascadeDelete : Boolean = false
) {
}