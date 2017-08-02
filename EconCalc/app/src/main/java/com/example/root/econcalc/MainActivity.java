package com.example.root.econcalc;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.SimpleTimeZone;

import android.media.audiofx.EnvironmentalReverb;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.graphics.*;

import static java.lang.System.exit;

public class MainActivity extends AppCompatActivity {
    protected EditText filePath;
    protected TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filePath = (EditText)findViewById(R.id.PathInput);
        output = (TextView)findViewById(R.id.MatrixOutput);
        Button calc = (Button)findViewById(R.id.CalculateButton);
        Button file = (Button)findViewById(R.id.FileButton);
        Button image = (Button)findViewById(R.id.ImageButton);

        //Waits for calculate button to be pressed.
        calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String path = filePath.getText().toString();
            EconCalc.processCSV(path, output);
            }
        });

        //Waits for file button to be pressed.
        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdCard.getAbsolutePath() + "/EconCalc/");
                    dir.mkdir();

                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                    File newFile = new File(dir, timeStamp + "_productionMatrix.txt");
                    FileOutputStream outStream = new FileOutputStream(newFile);
                    String data = output.getText().toString();
                    outStream.write(data.getBytes());
                    outStream.close();
                }

                catch (Exception e)
                {
                    exit(0);
                }
            }
        });

        //Waits for image button to be pressed.
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdCard.getAbsolutePath() + "/EconCalc/");
                    dir.mkdir();

                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                    File newFile = new File(dir, timeStamp + "_productionMatrix.png");
                    FileOutputStream outStream = new FileOutputStream(newFile);

                    Bitmap b = Bitmap.createBitmap(output.getWidth(), output.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas c = new Canvas(b);
                    output.draw(c);

                    b.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                }

                catch (Exception e) {
                    exit(0);
                }
            }
        });
    }

    public static class EconCalc
    {
        public static void processCSV(String fileName, TextView output) {
            try {
                //Opens the file
                Scanner inputFile = new Scanner(new File(fileName));
                inputFile.useDelimiter(",|\n");

                //Takes in a value that defines the size of a square matrix.
                int matrixSize = Integer.parseInt(inputFile.next());

                //Creates arrays to store values from file.
                double[][] inputMatrix = new double[matrixSize][matrixSize];
                double[] demandMatrix = new double[matrixSize];
                String[] names = new String[matrixSize];

                //Begins parsing of file, line by line.
                for (int i = 0; i < matrixSize; i++) {
                    names[i] = inputFile.next();

                    for (int j = 0; j < matrixSize; j++) {
                        inputMatrix[i][j] = Double.parseDouble(inputFile.next());
                    }

                    demandMatrix[i] = Double.parseDouble(inputFile.next());
                }

                inputFile.close();

                //Subtracts the an identity matrix by the input matrix.
                double[][] subMatrix = subtract(inputMatrix, matrixSize);

                //Gets the inverted input matrix.
                double[][] inverseMatrix = invert(subMatrix, matrixSize);

                //Gets the production matrix.
                double[] productionMatrix = multiply(inverseMatrix, demandMatrix, matrixSize);

                //Clears the output box, if not already empty
                output.setText("");

                //Prints the production matrix.
                DecimalFormat df = new DecimalFormat("#.##");
                for (int i = 0; i < matrixSize; i++) {
                    output.append(names[i] + ": " + df.format(productionMatrix[i]) + "\n");
                }
            }
            catch (FileNotFoundException ex)
            {
                exit(0);
            }
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
                    inputMatrix[index[i]][j] = pivotJ;

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
}
