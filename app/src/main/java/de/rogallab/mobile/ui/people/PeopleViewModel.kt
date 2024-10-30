package de.rogallab.mobile.ui.people

import android.app.Application
import de.rogallab.mobile.AppApplication
import de.rogallab.mobile.data.local.datastore.DataStore
import de.rogallab.mobile.data.repositories.PeopleRepository
import de.rogallab.mobile.domain.ResultData
import de.rogallab.mobile.domain.entities.Person
import de.rogallab.mobile.domain.utilities.as8
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.domain.utilities.logError
import de.rogallab.mobile.domain.utilities.logInfo
import de.rogallab.mobile.ui.base.BaseViewModel
import de.rogallab.mobile.ui.errors.ErrorParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PeopleViewModel(
   application: Application
) : BaseViewModel(TAG) {

   // we must fix this by using a dependency injection framework
   private val _context = application.applicationContext
   private val _dataStore = DataStore(_context)
   private val _repository = PeopleRepository(_dataStore)

   // get error messages from the string resources
   private val _validator = AppApplication.personValidator
   private var removedPerson: Person? = null

   // ===============================
   // S T A T E   C H A N G E S
   // ===============================
   // Data Binding PeopleListScreen <=> PersonViewModel
   private val _peopleUiStateFlow = MutableStateFlow(PeopleUiState())
   val peopleUiStateFlow = _peopleUiStateFlow.asStateFlow()

   // transform intent into an action
   fun onProcessPeopleIntent(intent: PeopleIntent) {
      logInfo(TAG, "onProcessIntent: $intent")
      when (intent) {
         is PeopleIntent.FetchPeople -> fetch()
      }
   }

   // read all people from repository
   private fun fetch() {
      logDebug(TAG, "fetchPeople")
      when (val resultData = _repository.getAll()) {
         is ResultData.Success -> {
            _peopleUiStateFlow.update { it: PeopleUiState ->
               it.copy(people = resultData.data.toList())
            }
            logDebug(TAG, "fetchPeople() people.size: ${peopleUiStateFlow.value.people.size}")
         }
         is ResultData.Error -> {
            onErrorEvent(ErrorParams(throwable = resultData.throwable, navEvent = null))
         }
      }
   }

   // Data Binding PersonScreen <=> PersonViewModel
   private val _personUiStateFlow = MutableStateFlow(PersonUiState())
   val personUiStateFlow = _personUiStateFlow.asStateFlow()

   // transform intent into an action
   fun onProcessPersonIntent(intent: PersonIntent) {
      logInfo(TAG, "onProcessIntent: $intent")
      when (intent) {
         is PersonIntent.FirstNameChange -> onFirstNameChange(intent.firstName)
         is PersonIntent.LastNameChange -> onLastNameChange(intent.lastName)
         is PersonIntent.EmailChange -> onEmailChange(intent.email)
         is PersonIntent.PhoneChange -> onPhoneChange(intent.phone)

         is PersonIntent.Clear -> clearState()

         is PersonIntent.FetchById -> fetchById(intent.id)
         is PersonIntent.Create -> create()
         is PersonIntent.Update -> update()
         is PersonIntent.Remove -> remove(intent.person)
         is PersonIntent.UndoRemove -> undoRemove()
      }
   }

   private fun onFirstNameChange(firstName: String) {
      if (firstName == _personUiStateFlow.value.person.firstName) return
      _personUiStateFlow.update { it: PersonUiState ->
         it.copy(person = it.person.copy(firstName = firstName))
      }
   }
   private fun onLastNameChange(lastName: String) {
      if (lastName == _personUiStateFlow.value.person.lastName) return
      _personUiStateFlow.update { it: PersonUiState ->
         it.copy(person = it.person.copy(lastName = lastName))
      }
   }
   private fun onEmailChange(email: String?) {
      if (email == null || email == _personUiStateFlow.value.person.email) return
      _personUiStateFlow.update { it: PersonUiState ->
         it.copy(person = it.person.copy(email = email))
      }
   }
   private fun onPhoneChange(phone: String?) {
      if (phone == null || phone == _personUiStateFlow.value.person.phone) return
      _personUiStateFlow.update { it: PersonUiState ->
         it.copy(person = it.person.copy(phone = phone))
      }
   }

   private fun fetchById(personId: String) {
      logDebug(TAG, "fetchPersonById: $personId")
      when (val resultData = _repository.getById(personId)) {
         is ResultData.Success -> _personUiStateFlow.update { it: PersonUiState ->
            it.copy(person = resultData.data ?: Person())  // new UiState
         }
         is ResultData.Error ->
            onErrorEvent(ErrorParams(throwable = resultData.throwable, navEvent = null))
      }
   }
   private fun create() {
      logDebug(TAG, "createPerson: ${_personUiStateFlow.value.person.id.as8()}")
      when (val resultData = _repository.create(_personUiStateFlow.value.person)) {
         is ResultData.Success -> fetch()
         is ResultData.Error ->
            onErrorEvent(ErrorParams(throwable = resultData.throwable, navEvent = null))
      }
   }
   private fun update() {
      logDebug(TAG, "updatePerson: ${_personUiStateFlow.value.person.id.as8()}")
      when(val resultData = _repository.update(_personUiStateFlow.value.person)) {
         is ResultData.Success -> fetch()
         is ResultData.Error ->
            onErrorEvent(ErrorParams(throwable = resultData.throwable, navEvent = null))
      }
   }
   private fun remove(person: Person) {
      logDebug(TAG, "removePerson: ${person.id.as8()}")
      when(val resultData = _repository.remove(person)) {
         is ResultData.Success -> {
            removedPerson = person
            fetch()
         }
         is ResultData.Error ->
            onErrorEvent(ErrorParams(throwable = resultData.throwable, navEvent = null))         }
   }
   private fun undoRemove() {
      removedPerson?.let { person ->
         logDebug(TAG, "undoRemovePerson: ${person.id.as8()}")
         when(val resultData = _repository.create(person)) {
            is ResultData.Success -> {
               removedPerson = null
               fetch()
            }
            is ResultData.Error ->
               onErrorEvent(ErrorParams(throwable = resultData.throwable, navEvent = null))
         }
      }
   }

   private fun clearState() {
      _personUiStateFlow.update { it.copy(person = Person()) }
   }

   // =========================================
   // V A L I D A T E   I N P U T   F I E L D S
   // =========================================
   // validate all input fields after user finished input into the form
   fun validate(isInput: Boolean): Boolean {
      val person = _personUiStateFlow.value.person

      if(validateAndLogError(_validator.validateFirstName(person.firstName)) &&
         validateAndLogError(_validator.validateLastName(person.lastName)) &&
         validateAndLogError(_validator.validateEmail(person.email)) &&
         validateAndLogError(_validator.validatePhone(person.phone))
      ) {
         // write data to repository
         if (isInput) this.create()
         else         this.update()
         return true
      } else {
         return false
      }
   }

   private fun validateAndLogError(validationResult: Pair<Boolean, String>): Boolean {
      val (success, message) = validationResult
      if (!success) logError(TAG, message)
      return success
   }

   companion object {
      private const val TAG = "<-PeopleViewModel"
   }
}