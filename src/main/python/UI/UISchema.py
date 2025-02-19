import abc
import dataclasses

from src.main.python.Components.VirtualView import IVirtualView


violation_codes = {
    1: "Invalid attribute",
    2: "Invalid children type",
    3: "Recursive relation"
}


@dataclasses.dataclass
class UISchemaViolationReport:
    """
        A structure storing details about individual schema violation committed by a singular node

        :param violation_code: An integer representing an ID of violation
        :param culprit_node: A reference to a node that committed violation
        :param atr_or_children_type: Dependent on a violation code, either name of an attribute or children type
    """
    violation_code: int
    culprit_node: IVirtualView
    atr_or_children_type: str


@dataclasses.dataclass
class UISchemaVerificationReport:
    """
        A structure storing a result of UISchema verification
        :param is_valid: Boolean representing whenever schema validation was successful or not
        :param node_reports: A list pairs IVirtualView & str which lists all the nodes that have violated some
         constraints alongside the reasons of this violation
    """
    is_valid: bool
    node_reports: list[tuple[IVirtualView, str]]


class IUISchema(abc.ABC):
    """
        An interface defining methods that UI Schema objects should implement
    """

    @abc.abstractmethod
    def verify_constraints_no_recursive(self, node: IVirtualView) -> UISchemaVerificationReport:
        """
        Verify if attributes of given node have correct values and if its children are of correct type.

        :param node: Node to check.
        :return:
        """

    @abc.abstractmethod
    def verify_constrains_recursive(self, node: IVirtualView) -> UISchemaVerificationReport:
        """
        Verify if attributes of given node and all of its descendants have correct values and if its children are of
        correct type

        :param node: Root of tree or subtree to verify
        :return:
        """

    @abc.abstractmethod
    def generate_parser(self):
        """
        Generates a UI parser object with complies with is capable of parsing ui code.

        :return:
        """
