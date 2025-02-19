import typing

from src.main.python.Components.FancyAttributes import ConcreteAttribute
import src.main.python.Components.VirtualView as vv


class _CurrentComponentGetter:
    """
        _ComponentFactory is a function-like object which encapsulates logic of retrieving the current view object from
        the layout tree. Its main use case is to provide a way to get current component's view from the inside
        _ComponentFactory call.

        :param builder:A layout tree builder of the application view. Acts as context of _CurrentComponentGetter call.
         The builder parameter of _CurrentComponentGetter and _ComponentFactory objects must match.
    """
    def __init__(self, builder: vv.LayoutTreeBuilder):
        self._builder = builder

    def __call__(self):
        return self._builder.get_current()


class _ComponentFactory:
    """
        _ComponentFactory is a function-like object which encapsulates logic of adding a view to the layout tree.

        :param builder: A layout tree builder of the application view. Acts as context of _ComponentFactory call. All
         the other _ComponentFactory called by component_init() function, should be created using the same builder
         parameter.

        :param view_factory: A functon responsible for creating an IVirtualView object to be inserted into
        application layout tree.

        :param component_init: A function called after the component's view was generated. It can be used to call
        other _ComponentFactory objects in order to append them as a children of current component in an application
        layout tree.
    """
    def __init__(self,
                 builder: vv.LayoutTreeBuilder,
                 view_factory: typing.Callable[[], vv.IVirtualView],
                 component_init: typing.Callable[[tuple[any, ...], dict], any]
                 ):
        self._builder = builder
        self._container_factory = view_factory
        self._init = component_init

    def __call__(self, *args, **kwargs):
        self._builder.step_up(self._container_factory())
        self._init(*args, **kwargs)
        self._builder.step_down()


root = vv.IVirtualView()
builder = vv.LayoutTreeBuilder(root)

get_parent_view = _CurrentComponentGetter(builder)
Text = _ComponentFactory
