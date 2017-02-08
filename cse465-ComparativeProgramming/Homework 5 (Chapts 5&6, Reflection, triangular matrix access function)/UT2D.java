class UT2D{	
	public static int UTMapping(int N, int r, int c) {
		/*
		 1) check r <= c (Mrc = 0 if r > c)
		 2) check c is in bounds of matrix
		 3) find normal position
		 4) subtract number of zeroes
		*/
		return r <= c && c < N ? r*N+c - (r*r+r)/2 : -1;
	}

	public static void main(String[] args) {
		System.out.println(UTMapping(4, 0, 0));
		System.out.println(UTMapping(4, 2, 3));
		System.out.println(UTMapping(4, 0, 3));
		System.out.println(UTMapping(4, 3, 3));
		System.out.println(UTMapping(4, 3, 2));

		System.out.println(UTMapping(1000, 0, 0));
		System.out.println(UTMapping(1000, 999, 999));
		System.out.println(UTMapping(1000,45, 999));
		System.out.println(UTMapping(40000, 0, 0));
		System.out.println(UTMapping(40000, 1000, 1000));
		System.out.println(UTMapping(40000, 39999, 39999));
	}
}
