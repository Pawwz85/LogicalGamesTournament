from __future__ import annotations
from typing import Self, Callable


class ObservableState:
    """
    ObservableState is a class that manages a list of observers and their associated callback functions. It allows for
    the registration and unregistration of observers, as well as notifying all registered observers when the state object
    changes. This design pattern is known as the Observer Pattern or Publish-Subscribe pattern.

    ObservableState is initialized with an empty dictionary to store the observers and their callback functions. A
    sequence variable is used to generate unique observer IDs. The `_generate_observer_id` method increments this
    variable and returns the new value.

    The `notify_observers` method is called whenever the state object changes, iterating through all of the registered
    callbacks and passing them the updated state object.

    The `register_callback_observer` method adds a new callback function to the dictionary of observers. It returns the
    newly-generated observer ID associated with this registration.

    The `unregister_observer` method removes an existing observer from the list of observers and their associated
    callback functions. The handle parameter is the unique identifier assigned during the registration process,
    allowing for easy removal of a specific observer.

    """
    def __init__(self):
        self._observers: dict[int, Callable[[Self], any]] = {}
        self._observer_id_sequence = 0

    def _generate_observer_id(self):
        self._observer_id_sequence += 1
        return self._observer_id_sequence

    def notify_observers(self):
        for callback in self._observers.values():
            callback(self)

    def register_callback_observer(self, callback: Callable[[ObservableState], any]) -> int:
        observer_id = self._generate_observer_id()
        self._observers[observer_id] = callback
        return observer_id

    def unregister_observer(self, handle: int):
        self._observers.pop(handle, None)


class Observer:
    """
    Observer is a class used to manage the registration and removal of observers from multiple ObservableState
    objects. It allows for convenient chaining between observable objects. The `watch` method adds an observer to a
    specific ObservableState object using the registered callback function and returns a unique handle identifier.
    The `unwatch` method removes observers from one or more ObservableState objects based on their associated handles
    or all handles if no parameters are provided.
    """
    def __init__(self):
        self._observer_handles: dict[ObservableState, set[int]] = {}

    def watch(self, state: ObservableState, callback: Callable[[ObservableState], any]) -> int:
        handle = state.register_callback_observer(callback)
        if state not in self._observer_handles.keys():
            self._observer_handles[state] = set()
        self._observer_handles[state].add(handle)
        return handle

    def unwatch(self, state: ObservableState | None = None, handle: int | None = None):
        if state is not None:
            keys: set[int] = self._observer_handles.pop(state, set())
            for k in keys:
                state.unregister_observer(k)

        if handle is not None:
            for o, set_ in self._observer_handles.items():
                set_.discard(handle)
                o.unregister_observer(handle)

        if state is None and handle is None:
            for s, set_ in self._observer_handles.items():
                for k in set_:
                    s.unregister_observer(k)
            self._observer_handles = {}
