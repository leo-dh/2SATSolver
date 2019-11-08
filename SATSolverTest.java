package sat;

/*
import static org.junit.Assert.*;

import org.junit.Test;
*/

import java.util.*;
import immutable.ImList;
import sat.env.*;
import sat.formula.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.List;


public class SATSolverTest {
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();
    Environment e = new Environment();




    public static void main(String[] args) {
        // String filepath = args[0];
    }

    public static void solver(String file){
        System.out.println("Start Parsing");
        Formula formula = parse(file);
        System.out.println("2SAT solver starts!!");
        long started1 = System.nanoTime();
        TwoSATSolver.solve(formula);
        long time1 = System.nanoTime();
        long timeTaken1 = time1 - started1;
        System.out.println("Time:" + timeTaken1/1000000.0 + "ms");
    }

    public static Formula parse(String filename){
        String line;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            Clause clause = new Clause();
            Formula formula = new Formula();
            while((line = reader.readLine()) != null){
                if (line.equals("") || line.charAt(0) == 'c' || line.charAt(0) == 'p'){
                    continue;
                }
                String[] stringLiterals = line.split("\\s+");
                for (String stringLiteral : stringLiterals){
                    if (stringLiteral.equals("0") == false) {
                        Literal literal = null;
                        if (stringLiteral.length() > 0){
                            if (stringLiteral.charAt(0) == '-') {
                                literal = NegLiteral.make(stringLiteral.substring(1));
                            } else {
                                literal = PosLiteral.make(stringLiteral);
                            }
                            clause = clause.add(literal);
                        }
                    }
                    else{
                        formula = formula.addClause(clause);
                        clause = new Clause();
                    }
                }
            }
            reader.close();
            return formula;
        }
        catch ( IOException x){
            return null;
        }
    }

    public void testSATSolver1(){
    	// (a v b)
        Environment e = SATSolver.solve(makeFm(makeCl(a,b))	);

/*
    	assertTrue( "one of the literals should be set to true",
    			Bool.TRUE == e.get(a.getVariable())
    			|| Bool.TRUE == e.get(b.getVariable())	);

*/
    }

    public void testSATSolver2(){
    	// (~a)
        Environment e = SATSolver.solve(makeFm(makeCl(na)));
/*
    	assertEquals( Bool.FALSE, e.get(na.getVariable()));
*/
    }

    private static Formula makeFm(Clause... e) {
        Formula f = new Formula();
        for (Clause c : e) {
            f = f.addClause(c);
        }
        return f;
    }

    private static Clause makeCl(Literal... e) {
        Clause c = new Clause();
        for (Literal l : e) {
            c = c.add(l);
        }
        return c;
    }



}
