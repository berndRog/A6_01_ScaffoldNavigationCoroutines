package de.rogallab.mobile.domain

import de.rogallab.mobile.domain.entities.Person

interface IPeopleRepository {

    fun getAll(): ResultData<List<Person>>
    fun getWhere(predicate: (Person) -> Boolean): ResultData<List<Person>>
    fun findById(id: String): ResultData<Person?>
    fun findBy(predicate: (Person) -> Boolean): ResultData<Person?>

    fun create(person: Person): ResultData<Unit>
    fun update(person: Person): ResultData<Unit>
    fun remove(person: Person): ResultData<Unit>

}