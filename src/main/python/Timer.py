from abc import ABC, abstractmethod


class Timer:

    def __init__(self):
        self._now: int = 0

    def get_time(self):
        return self._now

    def add_time(self, delta_time: int):
        self._now += delta_time

    def __int__(self):
        return self._now


class IEventTimer(ABC):
    @abstractmethod
    def get_progress(self):
        pass


class SingleEventTimer(IEventTimer):
    def __init__(self, timer: Timer, start: int, end: int):
        self._timer = timer
        self._start = start
        self._end = end
        self._duration = end - start

    def get_progress(self) -> float:
        now = self._timer.get_time()
        if now < self._start:
            return 0
        if now > self._end:
            return 1
        return (now - self._start) / self._duration


class RepeatingEventTimer(IEventTimer):
    def __init__(self, timer: Timer, duration: int, start: int):
        self._timer = timer
        self._duration = duration
        self._start = start

    def get_progress(self):
        now = self._timer.get_time()
        if now < self._start:
            return 0

        return ((now - self._start) % self._duration)/self._duration

