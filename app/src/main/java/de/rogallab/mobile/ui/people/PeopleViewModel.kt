package de.rogallab.mobile.ui.people

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import de.rogallab.mobile.domain.model.Person
import de.rogallab.mobile.domain.utilities.UUIDEmpty
import de.rogallab.mobile.domain.utilities.logDebug
import java.util.UUID

class PeopleViewModel : ViewModel() {

   private var _id: UUID = UUID.randomUUID()

   // State = Observables (DataBinding)
   private var _firstName: String by mutableStateOf(value = "")
   val firstName
      get() = _firstName
   fun onFirstNameChange(value: String) {
      if(value != _firstName )  _firstName = value }

   private var _lastName: String by mutableStateOf(value = "")
   val lastName
      get() = _lastName
   fun onLastNameChange(value: String) {
      if(value != _lastName )  _lastName = value
   }

   private var _email: String? by mutableStateOf(value = null)
   val email
      get() = _email
   fun onEmailChange(value: String) {
      if(value != _email )  _email = value
   }

   private var _phone: String? by mutableStateOf(value = null)
   val phone
      get() = _phone
   fun onPhoneChange(value: String) {
      if(value != _phone )  _phone = value
   }

   private var _imagePath: String? by mutableStateOf(value = null)
   val imagePath
      get() = _imagePath
   fun onImagePathChange(value: String?) {
      if(value != _imagePath )  _imagePath = value
   }

   // mutableList with observer
   val people: SnapshotStateList<Person> = mutableStateListOf()

   // lifecycle ViewModel
   override fun onCleared() {
      logDebug(tag, "onCleared()")
      super.onCleared()
   }

   fun readById(personId: UUID) {
      val person = people.first { it.id == personId }
      setStateFromPerson(person)
      logDebug(tag, "readbyId() ${person.asString()}")
   }

   fun add() {
      val person = getPersonFromState()
      logDebug(tag, "add() ${person.asString()}")
      if(people.firstOrNull{ it.id == person.id } == null) {
         // no person found with same id
         people.add(person)
      }
   }

   fun update() {
      val updatedPerson = getPersonFromState()
      val person = people.first { it.id == updatedPerson.id }
      people.remove(person)
      people.add(updatedPerson)
      logDebug(tag, "update() ${updatedPerson.asString()}")
   }

   fun setStateFromPerson(person: Person?) {
      _firstName = person?.firstName ?: ""
      _lastName  = person?.lastName ?: ""
      _email     = person?.email
      _phone     = person?.phone
      _imagePath = person?.imagePath
      _id        = person?.id ?: UUIDEmpty
   }

   fun getPersonFromState(): Person =
      Person(_firstName, _lastName, _email, _phone, _imagePath, _id)

   fun clearState() {
      logDebug(tag, "clearState")
      _firstName = ""
      _lastName  = ""
      _email     = null
      _phone     = null
      _imagePath = null
      _id        = UUID.randomUUID()
   }

   companion object {
      private val tag:String = "ok>PeopleViewModel    ."
   }
}