import unittest
import math

from src.main.python.Timer import Timer, SingleEventTimer, RepeatingEventTimer


class TestTimer(unittest.TestCase):
    def setUp(self):
        self.timer = Timer()

    def tearDown(self):
        del self.timer

    def test_init_val_is_0(self):
        self.assertEqual(self.timer.get_time(), 0)

    def test_add_time(self):
        self.timer.add_time(5)
        self.assertEqual(self.timer.get_time(), 5)
        self.timer.add_time(37)
        self.assertEqual(self.timer.get_time(), 42)


class TestSingleEventTimer(unittest.TestCase):
    def setUp(self):
        self.timer = Timer()
        self._event = SingleEventTimer(self.timer, 50, 100)

    def tearDown(self):
        del self._event, self.timer

    def test_progress_before_start_is_0(self):
        self.timer.add_time(25)
        self.assertTrue(math.isclose(self._event.get_progress(), 0.))

    def test_progress_after_end_is_1(self):
        self.timer.add_time(125)
        self.assertTrue(math.isclose(self._event.get_progress(), 1.))

    def test_progress_in_middle(self):
        self.timer.add_time(75)
        self.assertTrue(math.isclose(self._event.get_progress(), .5))


class TestRepeatingEventTimer(unittest.TestCase):
    def setUp(self):
        self.timer = Timer()
        self._event = RepeatingEventTimer(self.timer, 50, 10)

    def tearDown(self):
        del self._event, self.timer

    def test_progress_before_start_is_0(self):
        self.assertEqual(self._event.get_progress(), 0)

    def test_progress_between_events_is_fraction(self):
        self.timer.add_time(15)
        self.assertAlmostEqual(self._event.get_progress(), 0.1, places=2)

    def test_progress_after_repeat_is_fraction(self):
        self.timer.add_time(70)
        self.assertAlmostEqual(self._event.get_progress(), 0.2, places=2)
