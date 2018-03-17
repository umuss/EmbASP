from languages.pddl.Plan import Plan
from parsers.pddl.PDDLSolversParser import PDDLSolversParser

class SPDPlan(Plan):
    """Represent a solution to a SPD problem"""
    
    def __init__(self, plan, error):
        super(SPDPlan, self).__init__(plan, error)
        
    def _parse(self):
        if self._errors or not self._output:
            return

        self._errors += PDDLSolversParser.parseSolverPlanningDomains(self, self._output, True)
