package org.example.valdiations;

import org.example.exceptions.BadRequest;

public class NegativeCheckValidation implements ValdiationStrategy{
    @Override
    public Boolean validate(int n) throws RuntimeException{
        if(n < 0) throw new BadRequest("Negative Number");

        return true;
    }
}
