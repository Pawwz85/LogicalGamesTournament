from abc import ABC, abstractmethod

from src.main.python.Timer import IEventTimer


class IAttribute(ABC):
    @abstractmethod
    def get_value(self):
        pass

    @abstractmethod
    def set_value(self, value: any):
        pass


class ConcreteAttribute:

    def __init__(self, val: any):
        self.__value = val

    def get_value(self):
        return self.__value

    def set_value(self, value: any):
        self.__value = value


class AttributeAnimatorABC(ABC):

    @abstractmethod
    def animate_value(self, val: any, animation_progress: float) -> any:
        pass

    def animate(self, attr: IAttribute, event_timer: IEventTimer) -> IAttribute:
        animator = self

        class Wrapper(IAttribute):
            def __init__(self):
                self.__wrappee = attr

            def get_value(self):
                return animator.animate_value(self.__wrappee.get_value(),
                                              event_timer.get_progress())

            def set_value(self, value: any):
                return self.__wrappee.set_value(value)

        Wrapper.__name__ = attr.__class__.__name__
        result = Wrapper()
        return result


class AttributeInterpolatorABC(ABC):

    @abstractmethod
    def interpolate_value(self, val1: any, val2: any, interpolation_progress: float) -> IAttribute:
        pass

    def interpolate(self, attr_begin: IAttribute, attr_end: IAttribute, event_timer: IEventTimer) -> IAttribute:
        interpolator = self

        class Wrapper(IAttribute):
            def __init__(self):
                self.__wrappee1 = attr_begin
                self.__wrappee2 = attr_end

            def get_value(self):
                return interpolator.interpolate_value(self.__wrappee1.get_value(),
                                                      self.__wrappee2.get_value(),
                                                      event_timer.get_progress())

            def set_value(self, value: any):
                return self.__wrappee2.set_value(value)

        Wrapper.__name__ = attr_end.__class__.__name__
        result = Wrapper()
        return result
