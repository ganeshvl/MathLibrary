import java.util.Scanner;

public class Solution {

    // Complete the plusMinus function below.
    static void plusMinus(int[] arr) {

        if (arr == null) {
            return;
        }
        int n = arr.length;
        int positiveCount = 0, negetiveCount = 0, zeroCount = 0;
        for (int i = 0; i < n; i++) {

            if (arr[i] < 0) {
                negetiveCount++;
            } else if (arr[i] > 0) {
                positiveCount++;
            } else if (arr[i] == 0) {
                zeroCount++;
            }
        }
        float a = 0f, b = 0f, c = 0f;
        a = (float) positiveCount / n;
        b = (float) negetiveCount / n;
        c = (float) zeroCount / n;
        System.out.printf("%.6f \n", a);
        System.out.printf("%.6f \n", b);
        System.out.printf("%.6f", c);

    }

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        int[] arr = {1, 2, 3, 4, -1, 0, -2, -3};

        plusMinus(arr);

    }
}
