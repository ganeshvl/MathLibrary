// Java Program for the binary
// representation of a given number
class BinaryRepresentationOfaNumber
{
    static void bin(int n)
    {
        /* step 1 */
        if (n > 1)
            bin(n/2);

        /* step 2 */
        System.out.print(n % 2);
    }

    //Driver code
    public static void main(String[] args)
    {
        bin(12);
        System.out.println();
        bin(12);
    }
}

// This code is contributed
// by ChitraNayal
