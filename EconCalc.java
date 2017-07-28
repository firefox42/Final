import java.util.Scanner;

public class EconCalc
{
   public static void main(String[] args)
   {
      //Takes in a value that defines the size of a square matrix.
      int matrixSize;
      Scanner userInput = new Scanner(System.in);

      System.out.println("Enter the size of the matrix: ");
      matrixSize = userInput.nextInt();

      //Stores the input matrix, the size of which depends on previous variable.
      double[][] inputMatrix = new double[matrixSize][matrixSize];

      System.out.println("Enter the elements of the input matrix: ");

      for(int i = 0; i < matrixSize; i++)
      {
         for(int j = 0; j < matrixSize; j++)
         {
            inputMatrix[i][j] = userInput.nextDouble();
         }

      }

      //Stores the demand matrix.
      double[] demandMatrix = new double[matrixSize];

      System.out.println("Enter the elements of the demand matrix: ");

      for(int i = 0; i < matrixSize; i++)
      {
         demandMatrix[i] = userInput.nextDouble();
      }

      //Subtracts the an identity matrix by the input matrix.
      double[][] subMatrix = subtract(inputMatrix, matrixSize);

      //Gets the inverted input matrix.
      double[][] inverseMatrix = invert(subMatrix, matrixSize);

      //Gets the producton matrix.
      double[] productionMatrix = multiply(inverseMatrix, demandMatrix, matrixSize);

      //Prints the production matrix.
      System.out.println("The production matrix is: ");
      for (int i = 0; i < matrixSize; i++)
      {
         System.out.println(productionMatrix[i]);
      }

      userInput.close();
   }

   //Subtracts the an identity matrix by the input matrix.
   public static double[][] subtract(double[][] inputMatrix, int matrixSize)
   {
      double[][] identity = new double[matrixSize][matrixSize];

      //Sets the diagonal of the idenity matirx to all ones.
      for (int i = 0; i < matrixSize; i++)
      {
         identity[i][i] = 1; 
      }

      //Subrtracts each element of the identity matrix by the same element in
      //the input matrix.
      for (int i = 0; i < matrixSize; i++)
      {
         for (int j = 0; j < matrixSize; j++)
         {
            identity[i][j] -= inputMatrix[i][j];
         }

      }

      return identity;
   }

   //Inverts the input matrix.
   public static double[][] invert(double[][] inputMatrix, int matrixSize)
   {
      double[][] tempMatrix = new double[matrixSize][matrixSize];
      double[][] returnMatrix = new double[matrixSize][matrixSize];
      int index[] = new int[matrixSize];

      //Initializes the temporary matrix with all 1's.
      for (int i = 0; i < matrixSize; ++i) 
      {
         tempMatrix[i][i] = 1;
      }

      //Preforms gaussian elmination on the input matrix.
      gaussian(inputMatrix, index, matrixSize);

      //Updates the temporary matrix.
      for (int i=0; i < matrixSize - 1; ++i)
      {
         for (int j = i + 1; j < matrixSize; ++j)
         {
            for (int k = 0; k < matrixSize; ++k)
            {
               tempMatrix[index[j]][k] -= inputMatrix[index[j]][i]
                  * tempMatrix[index[i]][k];
            }

         }

      }

      //Gets the inverted matrix by backwards substitution.
      for (int i = 0; i < matrixSize; ++i) 
      {
         returnMatrix[matrixSize - 1][i] = tempMatrix[index[matrixSize - 1]][i]
            / inputMatrix[index[matrixSize - 1]][matrixSize - 1];

         for (int j = matrixSize - 2; j >= 0; --j) 
         {
            returnMatrix[j][i] = tempMatrix[index[j]][i];

            for (int k = j + 1; k < matrixSize; ++k) 
            {
               returnMatrix[j][i] -= inputMatrix[index[j]][k]
                  * returnMatrix[k][i];
            }

            returnMatrix[j][i] /= inputMatrix[index[j]][j];

         }

      }

      //Returns the inverted matrix.
      return returnMatrix;
   }

   //Preforms gaussian elimination on the input matrix, storing the pivots in index.
   public static void gaussian(double[][] inputMatrix, int[] index, int matrixSize)
   {
      double[] tempMatrix = new double[matrixSize];

      //Initializes the index array.
      for (int i = 0; i < matrixSize; ++i) 
      {   
         index[i] = i;
      }

      //Finds the values necessary to scale each row, if necessary.
      for (int i = 0; i < matrixSize; ++i) 
      {
         double valueOne = 0;

         for (int j = 0; j < matrixSize; ++j) 
         {

            double valueTwo = Math.abs(inputMatrix[i][j]);

            if (valueTwo > valueOne) 
            {
               valueOne = valueTwo;
            }

         }

         tempMatrix[i] = valueOne;

      }

      //Locates the pivots.
      int k = 0;

      for (int j = 0; j < matrixSize - 1; ++j) 
      {
         double pivotOne = 0;

         for (int i = j; i < matrixSize; ++i) 
         {
            double pivotTwo = Math.abs(inputMatrix[index[i]][j]);

            pivotTwo /= tempMatrix[index[i]];

            if (pivotTwo > pivotOne) 
            {

               pivotOne = pivotTwo;
               k = i;
            }

         }

         //Swaps the rows
         int swap = index[j];

         index[j] = index[k];

         index[k] = swap;

         for (int i = j + 1; i < matrixSize; ++i)  
         {
            double pivotJ = inputMatrix[index[i]][j] / inputMatrix[index[j]][j];

            // Record pivoting ratios below the diagonal
            inputMatrix[index[i]][j] = pivotJ;

            // Modify other elements accordingly
            for (int l = j + 1; l < matrixSize; ++l)
            {
               inputMatrix[index[i]][l] -= pivotJ * inputMatrix[index[j]][l];
            }

         }

      }

   }

   //Multiplies a square matrix by column matrix of the same height.
   public static double[] multiply(double[][] inputMatrix, double[] demandMatrix, int matrixSize)
   {
      double[] productionMatrix = new double[matrixSize];
      
      for (int i = 0; i < matrixSize; i++)
      {
         for (int j = 0; j < matrixSize; j++)
         {
            productionMatrix[i] += inputMatrix[i][j] * demandMatrix[j];    
         }

      }

      return productionMatrix;
   }

}
