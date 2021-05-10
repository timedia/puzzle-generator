package np0;
//	Number Place Solution Class
//
//	solve a Number Place problem with basic mdthods.
//
//(c) 2019  FUJIWARA Hirofumi, Knowledge Engineering Center, Time Intermedia, Inc.
// This code is licensed under MIT license (see LICENSE.txt for details)
//
public class Solution {	
	static	int		REPLACE = 10;	
	
	static int[][] board = 	
	   {{6,9,5, 3,4,1, 8,7,2}, 
		{7,2,3, 9,8,5, 4,6,1},
		{8,4,1, 6,2,7, 5,3,9},
		{5,1,6, 8,3,2, 9,4,7},
		{9,3,7, 1,6,4, 2,5,8},
		{2,8,4, 7,5,9, 6,1,3},
		{1,7,2, 4,9,6, 3,8,5},
		{3,6,9, 5,1,8, 7,2,4},
		{4,5,8, 2,7,3, 1,9,6}};

	public static int[][]	getANewSolution() {		 		
		for( int i=0; i<REPLACE; ++i ) {
			int line1 = NP.random.nextInt(NP.SIZE);
			int line2 = line1+1;
			if( line2 % NP.SUBSIZE == 0 )
				line2 -= NP.SUBSIZE;
			if( i % 2 == 0 ) {	// vertical or horizontal exchange
				exchangeVline( line1, line2 );
			} else {
				exchangeHline( line1, line2 );
			}
		}
		return board;
	}
	
	private static void exchangeHline( int r1, int r2 ) {
		for( int c=0; c<NP.SIZE; ++c) {
			int w = board[r1][c];
			board[r1][c] = board[r2][c];
			board[r2][c] = w;
		}
	}
	
	private static void exchangeVline( int c1, int c2 ) {
		for( int r=0; r<NP.SIZE; ++r) {
			int w = board[r][c1];
			board[r][c1] = board[r][c2];
			board[r][c2] = w;
		}
	}

	// main for test -------------------------------------------------
	public static void main(String[] args) {
		for( int i=1;i<=10;++i) {
			int[][] bd = getANewSolution();
			System.out.println("No."+i);
			NP.printBoard(System.out,bd);
		}
	}
}
