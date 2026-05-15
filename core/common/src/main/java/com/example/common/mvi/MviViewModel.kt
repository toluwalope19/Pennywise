package com.example.common.mvi



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class MviViewModel<State, Event, Effect>(
    initialState: State
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    protected val currentState: State
        get() = _state.value

    fun onEvent(event: Event) {
        handleEvent(event)
    }

    protected abstract fun handleEvent(event: Event)

    protected fun setState(reducer: State.() -> State) {
        _state.value = currentState.reducer()
    }

    protected fun setEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}