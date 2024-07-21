package de.rogallab.mobile.ui.people

import android.util.Patterns
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import de.rogallab.mobile.domain.model.Person
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.ui.errors.ErrorMessages
import de.rogallab.mobile.ui.errors.ResourceProvider
import de.rogallab.mobile.ui.navigation.NavEvent
import de.rogallab.mobile.ui.people.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class PeopleViewModel(
   private val _errorMessages:ErrorMessages
) : BaseViewModel(tag) {


   /*
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
   */


   // Data Binding PersonInputScreen <=> PersonViewModel
   private val _personUiStateFlow: MutableStateFlow<PersonUiState> = MutableStateFlow(PersonUiState())
   val personUiStateFlow: StateFlow<PersonUiState> = _personUiStateFlow.asStateFlow()

   fun onFirstNameChange(firstName: String) {
      if (firstName == _personUiStateFlow.value.person.firstName) return
      _personUiStateFlow.update { it: PersonUiState ->
         it.copy(person = it.person.copy(firstName = firstName))
      }
   }
   fun onLastNameChange(lastName: String) {
      if (lastName == _personUiStateFlow.value.person.lastName) return
      _personUiStateFlow.update { it: PersonUiState ->
         it.copy(person = it.person.copy(lastName = lastName))
      }
   }
   fun onEmailChange(email: String?) {
      if (email == null || email == _personUiStateFlow.value.person.email) return
      _personUiStateFlow.update { it: PersonUiState ->
         it.copy(person = it.person.copy(email = email))
      }
   }
   fun onPhoneChange(phone: String?) {
      if (phone == null || phone == _personUiStateFlow.value.person.phone) return
      _personUiStateFlow.update { it: PersonUiState ->
         it.copy(person = it.person.copy(phone = phone))
      }
   }


   // mutableList with observer
   val people: SnapshotStateList<Person> = mutableStateListOf()

   // lifecycle ViewModel
   override fun onCleared() {
      logDebug(tag, "onCleared()")
      super.onCleared()
   }


   fun validateTheSaveAndNavigate(

   ) {
      // input is ok        -> add and navigate up
      // detail is ok       -> update and navigate up
      // is the is an error -> show error and stay on screen
   }

   fun getById(personId: UUID) {
      val person = people.first { it.id == personId }
      _personUiStateFlow.update { personUiState ->
         personUiState.copy(person = person)
      }
      logDebug(tag, "readbyId() ${person.asString()}")
   }

   fun add() {
      val person = _personUiStateFlow.value.person
      logDebug(tag, "add() ${person.asString()}")
      if(people.firstOrNull{ it.id == person.id } == null) {
         // no person found with same id
         people.add(person)
      }
   }

   fun update() {
      val updatedPerson = _personUiStateFlow.value.person
      val person = people.first { it.id == updatedPerson.id }
      people.remove(person)
      people.add(updatedPerson)
      logDebug(tag, "update() ${updatedPerson.asString()}")
   }

   fun clearState() {
      logDebug(tag, "clearState")
      // create a new person
      _personUiStateFlow.update { personUiState ->
         personUiState.copy(person = Person())
      }
   }


   fun validate(
      isInput: Boolean
   ) {
      val charMin = _errorMessages.charMin
      val charMax = _errorMessages.charMax

      val person = _personUiStateFlow.value.person
      // firstName or lastName too short
      if (person.firstName.isEmpty() || person.firstName.length < charMin) {
         showOnError(message = _errorMessages.nameTooShort, navEvent = null)
      }
      else if (person.lastName.isEmpty() || person.lastName.length < charMin) {
         showOnError(message = _errorMessages.nameTooShort, navEvent = null)
      }
      else if (person.firstName.length > charMax) {
         showOnError(message = _errorMessages.nameTooLong, navEvent = null)
      }
      else if (person.lastName.length > charMax) {
         showOnError(message = _errorMessages.nameTooLong, navEvent = null)
      }
      else if (person.email != null &&
         !Patterns.EMAIL_ADDRESS.matcher(person.email).matches()) {
         showOnError(message = _errorMessages.emailInValid, navEvent = null)
      }
      else if (person.phone != null &&
         !Patterns.PHONE.matcher(person.phone).matches()) {
         showOnError(message = _errorMessages.phoneInValid, navEvent = null)
      }
      else {
         if (isInput) this.add()
         else         this.update()
         onNavigateTo(NavEvent.ToPeopleList)
      }
   }

   companion object {
      private const val tag = "[PeopleViewModel]"
   }
}