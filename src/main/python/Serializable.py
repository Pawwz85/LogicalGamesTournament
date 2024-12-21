from __future__ import annotations

import json
from dataclasses import dataclass, Field, fields
from abc import ABC, abstractmethod
from typing import Self, Callable, Type


class ISerializable(ABC):

    @abstractmethod
    def serialize(self) -> bytes:
        pass

    @staticmethod
    def from_bytes(b: bytes):
        raise NotImplementedError()


class IConstruableFromDict(ISerializable):
    def to_jsonable_dict(self) -> dict:
        raise NotImplementedError

    @staticmethod
    def get_object_hook() -> Callable[[dict], any]:
        raise NotImplementedError

    def serialize(self) -> bytes:
        return json.dumps(self.to_jsonable_dict()).encode()


class ObjectHooksManager:
    def __init__(self):
        self._hooks: dict[str, Callable[[dict], any]] = dict()

    def add_hook(self, cls: Type[IConstruableFromDict]):
        cls_name = cls.__name__
        self._hooks[cls_name] = cls.get_object_hook()

    def serialize(self, o: IConstruableFromDict) -> bytes:
        self.add_hook(o.__class__)
        result = {
             "inner": o.to_jsonable_dict(),
             "hook": o.__class__.__name__
         }
        string = json.dumps(result)
        return string.encode()

    def deserialize(self, b: bytes) -> any:
        try:
            half_product: dict = json.loads(str(b, "utf-8"))
            return self._hooks[half_product["hook"]](half_product["inner"])
        except KeyError:
            raise
        except TypeError:
            raise


#  Warning: Experimental api
def construable_from_dict(cls: type(dataclass())):
    """
        Implements IConstruableFromDict interface for a dataclass.

        Both generated "to_jsonable_dict" and "get_object_hook" will call those methods in Fields that implement
        IConstruableFromDict interface or generate and call those methods for dataclasses.
    """
    fields_ = fields(cls)

    def ObjectHook(d: dict) -> any:
        for f in fields_:
            if issubclass(f.type, IConstruableFromDict):
                d[f.name] = f.type.get_object_hook()(d[f.name])
            elif issubclass(f.type, type(dataclass())):
                d[f.name] = construable_from_dict(f.type).get_object_hook()(d[f.name])
        kwargs = {f.name: d[f.name] for f in fields_}
        return Wrapper(**kwargs)

    class Wrapper(IConstruableFromDict):
        def to_jsonable_dict(self) -> dict:
            result = {}
            for f in fields_:
                field_val = self.__getattribute__(f.name)
                if jsonify := field_val.__getattribute__("to_jsonable_dict"):
                    result[f.name] = jsonify()
                elif issubclass(f.type, type(dataclass())):
                    field_val.__setattr__("to_jsonable_dict", construable_from_dict(f.type).to_jsonable_dict)
                    result[f.name] = field_val.__getattribute__("to_jsonable_dict")()
                else:
                    result[field_val.f.name] = field_val
            return result

        @staticmethod
        def get_object_hook() -> Callable[[dict], any]:
            raise ObjectHook

    Wrapper.__init__ = cls.__init__
    Wrapper.__name__ = cls.__name__
    return Wrapper


# TODO: allow recursive serialisation, deserialization
def serializable(cls: type(dataclass())) -> type:
    fields_ = fields(cls)

    class Wrapper(ISerializable):
        def serialize(self) -> bytes:
            json_dict = {f.name: self.__getattribute__(f.name) for f in fields_ if f.init}
            tmp = json.dumps(json_dict)
            return tmp.encode("utf-8")

        @staticmethod
        def from_bytes(b: bytes):
            tmp = str(b, "utf-8")
            json_dict = json.loads(tmp)
            kwargs = {f.name: json_dict[f.name] for f in fields_ if f.init}
            return Wrapper(**kwargs)

    Wrapper.__init__ = cls.__init__
    return Wrapper

