package np0;
//	Number Place Solver Class
//
//	solve a Number Place problem with basic mdthods.
//
//(c) 2019  FUJIWARA Hirofumi, Knowledge Engineering Center, Time Intermedia, Inc.
// This code is licensed under MIT license (see LICENSE.txt for details)
//

class SolverException extends Exception {}	// Exception

public class Solver {
	private 	int[][]		board;
	private	boolean[][][]	candidate;

	//-------------------------------------------------------------

	public int solve( int[][] bd ) {
		int blanks;

		initialize();
		blanks = setProblem(bd);
		if( blanks < 0 )
			return -1;
		return solve();
	}

	public int solve() {
		try {
			checkLoop();
		} catch( SolverException e) {
			return -1;
		}
		return blankCount();
	}

	//-------------------------------------------------------------

	private void initialize() {
		board = new int[NP.SIZE][NP.SIZE];
		candidate = new boolean[NP.SIZE][NP.SIZE][NP.SIZE+1];
		for( int r=0; r<NP.SIZE; ++r )
			for(int c=0; c<NP.SIZE; ++c )
				for( int n=1; n<=NP.SIZE; ++n )
					candidate[r][c][n] = true;
	}

	private int setProblem( int[][] bd ) {		
		try{
			for(int r = 0; r < NP.SIZE; r++)
				for(int c = 0; c < NP.SIZE; c++)
					if( bd[r][c] != 0 )
						setValue( r, c, bd[r][c] );
		} catch(SolverException e) {
			return -1;
		}

		return blankCount();
	}

	private void checkLoop() throws SolverException {
		for(boolean changed=true; changed; ) {			
			changed = false;

			for( int r = 0; r < NP.SIZE; r+=NP.SUBSIZE ) {	// 3x3 Blocok Check
				for( int c = 0; c < NP.SIZE; c+=NP.SUBSIZE )
					if( checkBlock(c,r) )
						changed = true;
			}

			for( int r = 0; r < NP.SIZE; ++r ) {		// HLine check
				if( checkHline(r) )
					changed = true;
			}

			for( int c = 0; c < NP.SIZE; ++c ) {		// VLine check
				if( checkVline(c) )
					changed = true;
			}

			for(int r = 0; r < NP.SIZE; r++ )			// Cell check
				for(int c = 0; c < NP.SIZE; c++ )
					if( checkCell(c,r) )
						changed = true;
		}
	}

	//--------------------		set value	--------------------

	private void setValue( int r, int c, int v ) throws SolverException {
		if( !candidate[r][c][v] ) {
			throw new SolverException();
		}
		if( board[r][c]!=0 ) {
			if( board[r][c] != v ) {
				throw new SolverException();
			}
			return;
		} 

		board[r][c] = v;
		for( int n=1; n<=NP.SIZE; ++n )
			candidate[r][c][n] = false;

		int r0 = (r/NP.SUBSIZE)*NP.SUBSIZE;
		int c0 = (c/NP.SUBSIZE)*NP.SUBSIZE;
		for( int i=0; i<NP.SIZE; ++i ) {
			candidate[r][i][v] = false;
			candidate[i][c][v] = false;
			candidate[r0+(i/NP.SUBSIZE)][c0+i%NP.SUBSIZE][v] = false;
		}
	}

	//--------------------		get value	--------------------

	public int[][] getAnswer() {
		int[][] ans = new int[NP.SIZE][NP.SIZE];

		NP.copyBoard( board, ans );

		return ans;
	}

	public int getValue( int r, int c ) {
		return board[r][c];
	}

	public boolean[] getCandidate( int r, int c ) {
		return candidate[r][c];
	}

	//--------------------		print	--------------------

	public void printCandidate() {
		System.err.println("Solver.candidate:");
		for(int r = 0; r < NP.SIZE; r++) {
			for(int c = 0; c < NP.SIZE; c++) {
				for( int n=1; n<=NP.SIZE; ++n ) {
					System.err.print(candidate[r][c][n] ? (""+n) : "-" );
				}
				System.err.print(" ");
			}
			System.err.println();
		}
	}

	public void printBoard() {
		System.err.println("Solver.board:");
		NP.printBoard(System.err,board);
	}

	//--------------------	check box/line/cell & set	--------------------

	private boolean checkBlock( int c0, int r0 ) throws SolverException {
		boolean changed = false;

		for( int n=1; n<=NP.SIZE; ++n ) {
			boolean exist = false;
			int	cnt = 0;
			int	col = 0;
			int	row = 0;
			for( int r=r0; r<r0+NP.SUBSIZE; ++r )
				for( int c=c0; c<c0+NP.SUBSIZE; ++c ) {
					if( board[r][c] == n )
						exist = true;
					if( candidate[r][c][n] ) {
						++cnt;
						col = c;
						row = r;
					}
				}

			if( !exist ) {
				if( cnt == 1 ) {
					setValue( row, col, n );
					changed = true;
				} else if( cnt == 0 ) {
					throw new SolverException();
				}				
			}
		}

		return changed;		
	}

	private boolean checkHline( int r ) throws SolverException {
		boolean changed = false;

		for( int n=1; n<=NP.SIZE; ++n ) {
			boolean exist = false;
			int	cnt = 0;
			int	col = 0;
			for( int c=0; c<NP.SIZE; ++c ) {
				if( board[r][c] == n )
					exist = true;
				if( candidate[r][c][n] ) {
					++cnt;
					col = c;
				}
			}

			if( !exist ) {
				if( cnt == 1 ) {
					setValue( r, col, n );
					changed = true;
				} else if( cnt == 0 ) {
					throw new SolverException();
				}
			}
		}

		return changed;		
	}

	private boolean checkVline( int c ) throws SolverException {
		boolean changed = false;

		for( int n=1; n<=NP.SIZE; ++n ) {
			boolean exist = false;
			int	cnt = 0;
			int	row = 0;
			for( int r=0; r<NP.SIZE; ++r ) {
				if( board[r][c] == n )
					exist = true;
				if( candidate[r][c][n] ) {
					++cnt;
					row = r;
				}
			}

			if( !exist ) {
				if( cnt == 1 ) {
					setValue( row, c, n );
					changed = true;
				} else if( cnt == 0 ) {
					throw new SolverException();
				}
			}
		}

		return changed;		
	}

	private boolean checkCell( int c, int r ) throws SolverException {
		if( board[r][c] != 0 )
			return false;

		int	cnt = 0;
		int	v = 0;
		for( int n=1; n<=NP.SIZE; ++n ) {
			if( candidate[r][c][n] ) {
				++cnt;
				v = n;
			}
		}

		if( cnt == 1 ) {
			setValue( r, c, v );
		} else if( cnt == 0 ) {
			throw new SolverException();
		}	

		return cnt == 1;
	}

	//--------------------	blank count	--------------------

	private int blankCount() {
		int cnt = 0;

		for( int r=0; r<NP.SIZE; ++r )
			for(int c=0; c<NP.SIZE; ++c )
				if( board[r][c] == 0 )
					++cnt;

		return cnt;
	}
}
