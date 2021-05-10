package np0;
//	Number Place Generator Class
//
//	generate a Number Place problem with very easy method.
//
// (c) 2019  FUJIWARA Hirofumi, Knowledge Engineering Center, Time Intermedia, Inc.
// This code is licensed under MIT license (see LICENSE.txt for details)
//

public class Generator {
	static  int	XCOUNT = 2;

	Solver solver = new Solver();
	boolean[][] pattern;		// hint pattern, given from caller
	int hintcount;
	int[][] hintarray;
	int[][] xcells;
	int[][] problem;			// current problem
	int     blankcount;			// current blank cell count
	int[][] backup;				// current problem --> backup
	
	public boolean generate( boolean[][] pat ) {
		for( int i=0; i<400; ++i ) {
			System.err.print("*");
			if( generateOnce( pat ) ) {
				System.err.println( "SUCCESS  TRY "+i);
				return true;
			}
		}
		
		return false;	
	}
	
	public boolean generateOnce( boolean[][] pat ) {
		pattern = pat;
		backup = new int[NP.SIZE][NP.SIZE];
		xcells = new int[XCOUNT][2];
		initialSetting();
				
		for( int i=0; i<200; ++i ) {
			if( blankcount == 0 )		// SUCCESS!!
				break;

			NP.copyBoard( problem, backup );

			// select XCOUNT cells and changed them
			selectXCells();
			clearXCells();
			
			// change some cells value on problem
			int blk = changeXCells();
			
			// if new problem is better, update current blankcount, and continue
			// else restore problem
			if( blk >= 0 && blk < blankcount ) {
				blankcount = blk;	// update blankcount
				i = 0;
			} else {			// restore from backup
				NP.copyBoard( backup, problem );
			}
		}
					
		return blankcount==0;
	}
	
	private void initialSetting() {		
		int[][] solution = Solution.getANewSolution();
		
		problem = makeInitialProblem( pattern, solution );

		blankcount = solver.solve(problem);

		hintcount = countTrue(pattern);	
		hintarray = getHintArray();
	}

	private int countTrue( boolean pt[][] ) {
		int cnt = 0;
		for(int r = 0; r < NP.SIZE; r++)
			for(int c = 0; c < NP.SIZE; c++)
				if( pt[r][c] )
					++cnt;
		return cnt;
	}
	
	private int[][] getHintArray() {
		int[][] hintpos = new int[hintcount][2];
		int idx = 0;
		for(int r = 0; r < NP.SIZE; r++)
			for(int c = 0; c < NP.SIZE; c++)
				if( pattern[r][c] ) {
					hintpos[idx][0] = r;
					hintpos[idx][1] = c;
					++idx;
				}
		
		return hintpos;
	}
	
	private void selectXCells() {
		int cnt = 0;
		while( cnt < XCOUNT ) {
			int offr = NP.random.nextInt(hintcount);
			int[] p = hintarray[offr];
			boolean match = false;
			for( int i=0; i<cnt; ++i )
				match |= xcells[i]==p;
			if( !match ) {
				xcells[cnt] = p;
				++cnt;
			}
		}
	}
	
	private void clearXCells() {
		for( int i=0; i<XCOUNT; ++i ) {
			problem[xcells[i][0]][xcells[i][1]] = 0;
		}
	}
	
	private int changeXCells() {
		int blk = solver.solve(problem);

		for( int i=0; i<XCOUNT; ++i ) {
			int r = xcells[i][0];
			int c = xcells[i][1];
			int val = solver.getValue(r,c);
			if( val > 0 ) {
				problem[r][c] = val;
				continue;
			}
			boolean[] cans = solver.getCandidate(r,c);
			val = selectCandidate(cans);
			if( val < 0 )
				return	-1;
			problem[r][c] = val;

			blk = solver.solve(problem);			
			if( blk < 0 )
				return -1;
		}
		
		return blk;
	}
	
	private int	selectCandidate( boolean[] cans ) {
		int r = NP.random.nextInt(NP.SIZE);
		for( int i=0; i<NP.SIZE; ++i ) {
			int v = (r+i) % NP.SIZE + 1;
			if( cans[v] )
				return	v;
		}
		return	-1;
	}

	private int[][] makeInitialProblem( boolean[][] pattern, int[][] solution ) {
		int[][] prob = new int[NP.SIZE][NP.SIZE];
		
		for( int r=0; r<NP.SIZE; ++r )
			for( int c=0; c<NP.SIZE; ++c )
				prob[r][c] = pattern[r][c] ? solution[r][c] : 0;

		return prob;		
	}
	
	public int[][] getProblem() {
		return problem;
	}
}
