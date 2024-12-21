from IGameDefinition import IMove, IState, IGameDefinition
from src.main.python.Observer import ObservableState


class IllegalMoveError(BaseException):
    pass


class GameFrame(ObservableState):
    def __init__(self,
                 initial_state: IState,
                 game_definition: IGameDefinition,
                 ):
        super().__init__()
        self._current_state = initial_state
        self._position_stack: list[IState] = []
        self._move_stack: list[IMove] = []
        self._game_def = game_definition

    def make_move(self, move: IMove):
        if not self._game_def.is_move_legal(self._current_state, move):
            raise IllegalMoveError
        self._move_stack.append(move)
        self._position_stack.append(self._current_state)
        self._current_state = self._game_def.make_move(self._current_state, move)
        self.notify_observers()

    def unmake_last_move(self) -> IMove:
        if len(self._move_stack) == 0:
            raise
        last_move: IMove = self._move_stack.pop()
        self._current_state = self._position_stack.pop()
        self.notify_observers()
        return last_move

    def get_move_history(self) -> list[IMove]:
        return self._move_stack.copy()

    def get_current_state(self) -> IState:
        return self._current_state
