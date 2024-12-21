from abc import ABC
from typing import Type

from Serializable import ISerializable
from abc import ABC, abstractmethod


class IState(ISerializable, ABC):
    pass


class IMove(ISerializable, ABC):
    pass


class IGameDefinition(ABC):

    @abstractmethod
    def get_move_type(self) -> Type[IMove]:
        raise NotImplementedError

    @abstractmethod
    def get_state_type(self) -> Type[IState]:
        raise NotImplementedError

    @abstractmethod
    def make_move(self, state: IState, move: IMove) -> IState:
        """
        Applies user defined move to user defined state.

        :param state: User defined state
        :param move: User defined move
        :return: State derived from given state by applying move to given state.
        """

    @abstractmethod
    def is_move_legal(self, state: IState, move: IMove) -> bool:
        """
        Checks if it is legal to apply given move to given state.

        :param state: User defined state
        :param move: User defined move
        :return: True, if move is legal, False otherwise
        """

    @abstractmethod
    def is_acceptable(self, state: IState) -> bool:
        """
        Checks if given state is acceptable as a solution.

        :param state: State to check
        :return: True, if and only if state is an acceptable solution.
        """
