public class BlockMult
{
    public static int MAX = 4;



    static int[][] multiplyBlock(int A[][], int B[][])
    {
        int C[][]= new int[MAX][MAX];
        C[0][0]=A[0][0]*B[0][0]+A[0][1]*B[1][0];
        C[0][1]=A[0][0]*B[0][1]+A[0][1]*B[1][1];
        C[1][0]=A[1][0]*B[0][0]+A[1][1]*B[1][0];
        C[1][1]=A[1][0]*B[0][1]+A[1][1]*B[1][1];
        return C;
    }
    static int[][] addBlock(int A[][], int B[][])
    {
        int C[][]= new int[MAX][MAX];
        for (int i=0;i<C.length;i++)
        {
            for (int j=0;j<C.length;j++)
            {
                C[i][j]=A[i][j]+B[i][j];
            }
        }
        return C;
    }

    static int[][] multiplyMatrixBlock( int A[][], int B[][])
    {
        int bSize=2;
        int[][] A1 = new int[MAX][MAX];
        int[][] A2 = new int[MAX][MAX];
        int[][] A3 = new int[MAX][MAX];
        int[][] B1 = new int[MAX][MAX];
        int[][] B2 = new int[MAX][MAX];
        int[][] B3 = new int[MAX][MAX];
        int[][] C1 = new int[MAX][MAX];
        int[][] C2 = new int[MAX][MAX];
        int[][] C3 = new int[MAX][MAX];
        int[][] D1 = new int[MAX][MAX];
        int[][] D2 = new int[MAX][MAX];
        int[][] D3 = new int[MAX][MAX];
        int[][] res= new int[MAX][MAX];
        for (int i = 0; i < bSize; i++)
        {
            for (int j = 0; j < bSize; j++)
            {
                A1[i][j]=A[i][j];
                A2[i][j]=B[i][j];
            }
        }
        for (int i = 0; i < bSize; i++)
        {
            for (int j = bSize; j < MAX; j++)
            {
                B1[i][j-bSize]=A[i][j];
                B2[i][j-bSize]=B[i][j];
            }
        }
        for (int i = bSize; i < MAX; i++)
        {
            for (int j = 0; j < bSize; j++)
            {
                C1[i-bSize][j]=A[i][j];
                C2[i-bSize][j]=B[i][j];
            }
        }
        for (int i = bSize; i < MAX; i++)
        {
            for (int j = bSize; j < MAX; j++)
            {
                D1[i-bSize][j-bSize]=A[i][j];
                D2[i-bSize][j-bSize]=B[i][j];
            }
        }
        A3=addBlock(multiplyBlock(A1,A2),multiplyBlock(B1,C2));
        B3=addBlock(multiplyBlock(A1,B2),multiplyBlock(B1,D2));
        C3=addBlock(multiplyBlock(C1,A2),multiplyBlock(D1,C2));
        D3=addBlock(multiplyBlock(C1,B2),multiplyBlock(D1,D2));
        for (int i = 0; i < bSize; i++)
        {
            for (int j = 0; j < bSize; j++)
            {
                res[i][j]=A3[i][j];
            }
        }
        for (int i = 0; i < bSize; i++)
        {
            for (int j = bSize; j < MAX; j++)
            {
                res[i][j]=B3[i][j-bSize];
            }
        }
        for (int i = bSize; i < MAX; i++)
        {
            for (int j = 0; j < bSize; j++)
            {
                res[i][j]=C3[i-bSize][j];
            }
        }
        for (int i = bSize; i < MAX; i++)
        {
            for (int j = bSize; j < MAX; j++)
            {
                res[i][j]=D3[i-bSize][j-bSize];
            }
        }
        for (int i=0; i<MAX; i++)
        {
            for (int j=0; j<MAX;j++)
            {
                System.out.print(res[i][j]+" ");
            }
            System.out.println("");
        }
        return res;
    }

    // driver program
    public static void main (String[] args)
    {
        int A[][] = { {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}};

        int B[][] = { {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}};

        System.out.println(multiplyMatrixBlock( A, B));
    }
}