import dataclasses
import unittest

from src.main.python.Serializable import serializable


class MyTestCase(unittest.TestCase):

    @serializable
    @dataclasses.dataclass
    class ExampleDataclass1:
        t: int
        s: str
        t2: list[int]
        t3: dict

        def kebab(self):
            pass

    def test_something(self):
        e1 = self.ExampleDataclass1(1, "2", [1, 2, 3], {"kebab": 5, "cola": 3})
        print(e1.serialize())
        e2 = self.ExampleDataclass1.from_bytes(e1.serialize())
        print(e2.serialize())


if __name__ == '__main__':
    unittest.main()
