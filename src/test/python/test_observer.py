import random
import unittest
from unittest.mock import Mock

from src.main.python.Observer import ObservableState, Observer


class ObservableNumber(ObservableState):
    def __init__(self, value: int = 0):
        self._number = value
        super().__init__()

    @property
    def number(self) -> int:
        return self._number

    def get(self) -> int:
        return self.number

    def set(self, value: int):
        if self.number == value:
            return

        # save old number for notifying observers
        old_number = self.number

        self._number = value
        self.notify_observers()

        return old_number

    def add(self, delta: int):
        self.set(self.number + delta)

    def increment(self):
        self.add(1)


class TestObservableState(unittest.TestCase):
    def setUp(self):
        self.nr = ObservableNumber()

    def test_register_observer(self):
        mock = Mock()
        _ = self.nr.register_callback_observer(lambda nr: mock(nr.get()))
        self.nr.set(2)
        mock.assert_called_with(2)

    def test_unregister_observer(self):
        mock = Mock()
        handle = self.nr.register_callback_observer(lambda nr: mock(nr.get()))
        self.nr.unregister_observer(handle)
        self.nr.set(2)
        mock.assert_not_called()

    def test_multiple_handles(self, n: int = 100):
        mocks = [Mock() for _ in range(n)]
        rem = [random.randint(0, len(mocks) - 1) for _ in range(n//5)]
        handles = [self.nr.register_callback_observer(lambda nr, m=m: m(nr.get())) for m in mocks]

        for i in rem:
            self.nr.unregister_observer(handles[i])
        self.nr.set(2)
        for i, m in enumerate(mocks):
            if i in rem:
                m.assert_not_called()
            else:
                m.assert_called_with(2)


class TestObserver(unittest.TestCase):
    def setUp(self):
        self.number = ObservableNumber()
        self.observer = Observer()

    def test_watch(self):
        mock = Mock()
        handle = self.observer.watch(self.number, lambda x: mock(x.get()))
        self.assertIsNotNone(handle)
        self.number.set(42)
        mock.assert_called_with(42)

    def test_unwatch(self):
        mock = Mock()
        handle = self.observer.watch(self.number, lambda x: mock(x.get()))
        self.observer.unwatch(state=self.number)
        self.number.set(42)
        mock.assert_not_called()

    def test_multi_watches(self):
        mock1 = Mock()
        mock2 = Mock()
        handle1 = self.observer.watch(self.number, lambda x: mock1(x.get()))
        handle2 = self.observer.watch(self.number, lambda x: mock2(x.get()))
        self.assertIsNotNone(handle1)
        self.assertIsNotNone(handle2)
        self.number.set(42)
        mock1.assert_called_with(42)
        mock2.assert_called_with(42)

    def test_unwatch_single(self):
        mock1 = Mock()
        mock2 = Mock()
        handle1 = self.observer.watch(self.number, lambda x: mock1(x.get()))
        handle2 = self.observer.watch(self.number, lambda x: mock2(x.get()))
        self.observer.unwatch(handle=handle1)
        self.number.set(42)
        mock1.assert_not_called()
        mock2.assert_called_with(42)

    def test_unwatch_all(self):
        mock1 = Mock()
        mock2 = Mock()
        handle1 = self.observer.watch(self.number, lambda x: mock1(x.get()))
        handle2 = self.observer.watch(self.number, lambda x: mock2(x.get()))
        self.observer.unwatch()
        self.number.set(42)
        mock1.assert_not_called()
        mock2.assert_not_called()
