package org.example;

import org.example.valdiations.NegativeCheckValidation;
import org.example.valdiations.ValdiationStrategy;

public class Main {


    n = 10

    n= 5

    NegativeCheckValidation negativeCheckValidation;
    7
    

    static int iterativeFactorial(int n){
        if(n == 0 || n== 1) return 1;

        int res =1;
        for(int i=2; i<=n; i++){
            res *= i;
        }

        return res;
    }

    public static void main(String[] args) {
        ValdiationStrategy strategy  = new NegativeCheckValidation();
        int n = 5;
        strategy.validate(n);

        System.out.println(iterativeFactorial(n));
    }
}
