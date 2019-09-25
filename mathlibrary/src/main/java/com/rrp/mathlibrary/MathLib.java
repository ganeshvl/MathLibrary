package com.rrp.mathlibrary;

public class MathLib {

    private static MathLib mathLibrary;


    /**
     * A method to get instance of MathLib class
     * @return MathLib Object
     */
    public static MathLib getInstance(){
       if(mathLibrary == null){
           mathLibrary = new MathLib();
       }
        return mathLibrary;
    }

    /**
     * Method to add two positive numbers
     * @param numberOne
     * @param numberTwo
     * @return sum of given two numbers (numberOne + numberTwo)
     */
    public int addNumbers(int numberOne, int numberTwo){
        return numberOne + numberTwo;
    }


}
