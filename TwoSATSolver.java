package sat;

import immutable.ImList;
import sat.formula.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeMap;

public class TwoSATSolver{

    private static Map<Literal, ArrayList<Literal>> graph = new HashMap<Literal, ArrayList<Literal>>();
    private static Map<Literal, ArrayList<Literal>> graphInv = new HashMap<Literal, ArrayList<Literal>>();
    private static Map<Literal,Boolean> visited = new HashMap<Literal,Boolean>();
    private static Map<Literal,Boolean> visitedInv = new HashMap<Literal,Boolean>();
    private static ArrayList<Literal> variableSet = new ArrayList<Literal>();
    private static Stack<Literal> stack = new Stack<Literal>();

    private static Map<Integer, ArrayList<Literal>> graphScc = new HashMap<Integer, ArrayList<Literal>>();
    private static int sccCounter = 0;
    public static boolean solvable = false;
    public static Map<Literal, Boolean> solution = new HashMap<Literal, Boolean>();


    public static void solve(Formula formula) {
        solve(formula.getClauses());
    }

    private static void solve(ImList<Clause> clauses) {
        formGraph(clauses);

        for (Literal l : graph.keySet()){
			if(!visited.get(l)){
				dfsFirst(l);
			}
		}
		while(!stack.isEmpty()){
			Literal a = stack.peek();
			stack.pop();
			if (!visitedInv.get(a)){
                dfsSecond(a);
                sccCounter++;
			}
        }

        checkSatisfiability();
        if (solvable){getSolutions();}
    }

    private static void getSolutions(){
        Map<String, Boolean> sortedsolution = new TreeMap<String, Boolean>();
        for (Literal l: solution.keySet()){
            sortedsolution.put(l.toString(), solution.get(l));
        }
        for (String s: sortedsolution.keySet()){
            if (sortedsolution.get(s)){System.out.print("1 ");}
            else{System.out.print("0 ");}
        }
        System.out.println("");
    }
    private static void checkSatisfiability(){
        Map<Literal, Integer> sccSplit = new HashMap<Literal, Integer>();
        Map<Integer, Boolean> sccTruth = new HashMap<Integer, Boolean>();

        for (int num: graphScc.keySet()){
            for (Literal l: graphScc.get(num)){
                sccSplit.put(l, num);
            }
        }
        for (Literal variable: variableSet){
            if (sccSplit.get(variable).equals(sccSplit.get(variable.getNegation()))){
                System.out.println("FORMULA UNSATISFIABLE");
                return;
            }
        }
        solvable = true;
        if(solvable){
            System.out.println("FORMULA SATISFIABLE");
            int i = sccCounter - 1;
            while (!sccTruth.containsKey(i)) {
                for (Literal lit : graphScc.get(i)) {
                    if (lit instanceof PosLiteral){solution.put(lit, true);}
                    else if (lit instanceof NegLiteral){solution.put(lit.getNegation(), false);}
                    sccTruth.put(sccSplit.get(lit.getNegation()), false);
                }
                i--;
            }
        }
    }
    private static void formSccGraph(Literal l, int sccCounter){
        if (graphScc.containsKey(sccCounter)){graphScc.get(sccCounter).add(l);}
        else{
            ArrayList<Literal> scc = new ArrayList<Literal>();
            scc.add(l);
            graphScc.put(sccCounter,scc);
        }
    }
    private static void formGraph(ImList<Clause> clauses){
        for (Clause clause: clauses){
            Literal a = clause.getLiterals().first(); // First Literal of the clause
            Literal b = clause.getLiterals().rest().first(); // Second Literal of the clause
            if ((!variableSet.contains(a)) && (!variableSet.contains(a.getNegation()))){
                if (a instanceof PosLiteral){variableSet.add(a);}
                else{variableSet.add(a.getNegation());}
            }
            if ((!variableSet.contains(b)) && (!variableSet.contains(b.getNegation()))){
                if (b instanceof PosLiteral){variableSet.add(b);}
                else{variableSet.add(b.getNegation());}
            }
            addEdge(a.getNegation(), b, graph);
            addEdge(b.getNegation(), a, graph);
            addEdge(b, a.getNegation(), graphInv);
            addEdge(a, b.getNegation(), graphInv);
            visited.put(a, false);
            visited.put(a.getNegation(), false);
            visitedInv.put(a, false);
            visitedInv.put(a.getNegation(), false);
            visited.put(b, false);
            visited.put(b.getNegation(), false);
            visitedInv.put(b, false);
            visitedInv.put(b.getNegation(), false);
        }
    }
    private static void dfsFirst(Literal l){
        if (visited.get(l)){return;}
        visited.replace(l,false,true);
        if(graph.get(l)==null){
			stack.push(l);
			return;
		}
		for (int i = 0; i < graph.get(l).size(); i++){
			dfsFirst(graph.get(l).get(i));
		}
		stack.push(l);
    }
    private static void dfsSecond(Literal l){
        if (visitedInv.get(l)){return;}
        visitedInv.replace(l,false,true);
        if (graphInv.get(l) == null){
			formSccGraph(l, sccCounter);
			return;
		}
		for (int i = 0; i < graphInv.get(l).size(); i++){
			dfsSecond(graphInv.get(l).get(i));
		}
		formSccGraph(l, sccCounter);
	}

    private static void addEdge(Literal a, Literal b, Map<Literal,ArrayList<Literal>> graph){
        if (graph.containsKey(a)){
            if (!graph.get(a).contains(b)){graph.get(a).add(b);}
        }
        else{
            ArrayList<Literal> newList = new ArrayList<Literal>();
            newList.add(b);
            graph.put(a, newList);
        }
    }
}
