from __future__ import annotations
from src.main.python.Components.FancyAttributes import IAttribute


class IVirtualView:
    def __init__(self):
        self._type: str = "IVirtualView"
        self._parent: IVirtualView | None = None
        self._children: list[IVirtualView] = []
        self._attributes: dict[str, IAttribute] = {}
        self._allowed_attributes: set[str] = set()

    def append_children(self, child: IVirtualView):
        child.set_parent(self)
        self._children.append(child)

    def get_children(self) -> list[IVirtualView]:
        return self._children

    def get_parent(self) -> IVirtualView | None:
        return self._parent

    def set_parent(self, parent: IVirtualView | None):
        self._parent = parent

    def __setattr__(self, atr_name: str, value: IAttribute):
        if not issubclass(type(value), IAttribute):
            raise TypeError(f"Expected {IAttribute} type, found {type(value)}")

        if atr_name not in self._allowed_attributes:
            raise AttributeError

        self._attributes[atr_name] = value

    def __getattribute__(self, item):
        return self._attributes[item].get_value()


class LayoutTreeBuilder:
    def __init__(self, root: IVirtualView):
        self._root = root
        self._current = root

    def step_up(self, view: IVirtualView):
        self._current.append_children(view)
        self._current = view

    def step_down(self):
        self._current = self._current.get_parent()

    def verify_graph(self, node: None | IVirtualView = None) -> bool:
        """
        Recursive check that checks if each child of a node claim that node as a parent.
        Also returns false if graph root has a parent, this condition aims to prevent situation
        where root is a part of its own subtree.
        :return: True if graph is valid False otherwise
        """
        n = self._root if node is None else node

        if n is self._root and n.get_parent() is not None:
            return False

        for child in n.get_children():
            if child.get_parent() is not n or not self.verify_graph(node=child):
                return False

        return True

    def get_current(self):
        return self._current
