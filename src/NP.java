package np0;
//	NP(Number Place) main class
//
//	This class has the main method.
//
//(c) 2019  FUJIWARA Hirofumi, Knowledge Engineering Center, Time Intermedia, Inc.
// This code is licensed under MIT license (see LICENSE.txt for details)
//
import java.util.Random;

import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

class Problem {
	int[][] 		problem;
	String  		id;
	int     		blanks;
	int[][] 		answer;
	boolean[][] 	pattern;
}

public class NP {
	public static	int 	SIZE = 9;
	public	static int 	SUBSIZE = 3;
	
	public static Scanner 	datainput;
	public static PrintStream  dataoutput=null;
	
	public  static Random  random = new Random(System.currentTimeMillis());
	
	public String readProblemTitle() {
		return datainput.hasNextLine() ? datainput.nextLine() : null;	
	}

	public int[][] readProblemBody() {
		int[][] bd = new int[SIZE][SIZE];

		for( int r=0; r<SIZE; ++r ) {
			String[] tokens = datainput.nextLine().split("\\s+");
			for( int c=0; c<SIZE; ++c ) {
				String str = tokens[c];
				bd[r][c] = str.equals("-") ? 0 : Integer.parseInt(str);
			}
		}

		return	bd;
	}

	public boolean[][] readPatternBody() {
		boolean[][] bd = new boolean[SIZE][SIZE];

		for( int r=0; r<SIZE; ++r ) {
			String[] tokens = datainput.nextLine().split("\\s+");
			for( int c=0; c<SIZE; ++c )
				bd[r][c] = !tokens[c].equals("-");
		}

		return	bd;
	}

	public int coutHint( boolean[][] bd) {
		int	cnt = 0;
		for( int r=0; r<SIZE; ++r )
			for( int c=0; c<SIZE; ++c )
				if( bd[r][c] )
					++cnt;

		return	cnt;
	}

	public static void printBoard(PrintStream ps, int[][] bd) {
		for(int r = 0; r < SIZE; r++) {
			for(int c = 0; c < SIZE; c++) {
				if( bd[r][c] == 0 )
					ps.print("- ");
				else
					ps.print(""+bd[r][c]+" ");
			}
			ps.println();
		}
	}

	public static void printBoard(PrintStream ps, boolean[][] bd) {
		for(int r = 0; r < SIZE; r++) {
			for(int c = 0; c < SIZE; c++)
				ps.print(bd[r][c] ? "X " : "- ");
			ps.println();
		}
	}

	public static void copyBoard( int[][]fr, int[][]to ) {
		for( int r=0; r<SIZE; ++r )
			for( int c=0; c<SIZE; ++c )
				to[r][c] = fr[r][c];
	}

	private void solveNP() {
		Solver solver = new Solver();
		ArrayList<Problem> problems = new ArrayList<>();
		
		try {
			while( datainput.hasNextLine()) {
				Problem pr = new Problem();
				pr.id = readProblemTitle();
				pr.problem = readProblemBody();
				problems.add(pr);
			}
		} catch( Exception ex ) {}

		long startTime = System.nanoTime();

		for( Problem pb : problems ) {
			pb.blanks = solver.solve(pb.problem);
			if( pb.blanks >= 0 )
				pb.answer = solver.getAnswer();
		}

		long endTime = System.nanoTime();

		int success = 0;
		for( Problem pb : problems ) {
			if( dataoutput!=null)
				dataoutput.println(pb.id);
			System.err.println(pb.id);

			if( pb.blanks < 0 ) {
				if( dataoutput!=null)
					dataoutput.println("ERROR");
				System.err.println("ERROR");
			} else {
				if( dataoutput!=null) {
					dataoutput.println(pb.blanks);
					printBoard(dataoutput,pb.answer);
				}
				printBoard(System.err,pb.problem);
				System.err.println(pb.blanks);
				printBoard(System.err,pb.answer);
				System.err.println();
			}
			if( pb.blanks==0 )
				++success;
		}

		int probSize = problems.size();
		System.err.println("Total "+probSize+"    Success "+success);		

		long timediff = endTime - startTime;
		System.err.println("total time : " + timediff + " nano sec,    "
					+"average : " + timediff/probSize/1000 + " micro sec" );
	}

	public void generateNP() {
		Generator generator = new Generator();
		ArrayList<Problem> problems = new ArrayList<>();

		try {
			while( datainput.hasNextLine()) {
				Problem pb = new Problem();
				pb.id = readProblemTitle();
				pb.pattern = readPatternBody();
				problems.add(pb);
			}
		} catch( Exception ex ) {}

		long startTime = System.nanoTime();

		int failureCount=0, successCount=0, n=0;
		for( Problem pb : problems ) {
			boolean [][] pattern = pb.pattern;
			System.err.println("No."+(++n)+"   H "+ coutHint(pattern));
			if( dataoutput!=null)
				dataoutput.println("No."+(n)+"   H "+ coutHint(pattern));
			printBoard(System.err, pattern);

			if( generator.generate(pattern) ) {
				pb.problem = generator.getProblem();
				if( dataoutput!=null)
					printBoard(dataoutput,pb.problem);
				printBoard(System.err,pb.problem);
				++successCount;
			} else {
				if( dataoutput!=null)
					dataoutput.println("FAILURE");
				System.err.println("FAILURE");
				++failureCount;
			}
			System.err.println();
		}

		long endTime = System.nanoTime();
		long timediff = endTime - startTime;
		int probSize = problems.size();
		System.err.println("total "+(successCount+failureCount) + "  failure "+failureCount);
		System.err.println("total time : " + timediff/1000000 + " mili sec,    average : " 
				+ timediff/probSize/1000000 + " mili sec" );
	}

	private static void printErrorMessage() {
		System.err.println("===== arguments input error =====");
		System.err.println("java -jar NP.jar  -s  problem_file    [answer_file]");
		System.err.println("java -jar NP.jar  -g  pattern_file    [problem_file]");
	}
	
	//  -------------------- main() ------------------------

	public static void main(String[] args) {
		NP np = new NP();

		if( args.length < 2 ) {
			printErrorMessage();
			return;
		}
		
		try {
			datainput = new Scanner(new File(args[1]));
			if( args.length >= 3 )
				dataoutput = new PrintStream(new FileOutputStream(args[2]));
		} catch(Exception e) {
			System.err.println(e);
			return;
		}
		
		switch( args[0] ) {
		case "-s":
			np.solveNP();
			break;
		case "-g":
			np.generateNP();
			break;
		default:
			printErrorMessage();
		}
	}
}
