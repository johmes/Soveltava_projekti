package com.example.malko;

import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class AddTest {

    @Test
    public void inputChekkaus() {

        ArrayList<String> expectedInputit = new ArrayList<String>();
        ArrayList<String> outputit = new ArrayList<String>();
        String juomanNimi, yhteystiedot, kaupunginosa, kategoria, amount,productAdmin;
        String juomanNimiExpected, yhteystiedotExpected, kaupunginosaExpected,
                kategoriaExpected, amountExpected,productAdminExpected;
        String juomanNimiOutput, yhteystiedotOutput, kaupunginosaOutput,
                kategoriaOutput, amountOutput,productAdminOutput;
        juomanNimi= "Koff";
        juomanNimiExpected = "Koff";
        yhteystiedot="0400123456";
        yhteystiedotExpected="0400123456";
        kaupunginosa="Keskusta";
        kaupunginosaExpected="Keskusta";
        kategoria="Olut";
        kategoriaExpected="Olut";
        amount="6";
        amountExpected="6";
        productAdmin="ar6f54jklng90";
        productAdminExpected="ar6f54jklng90";

        expectedInputit.add(juomanNimiExpected);
        expectedInputit.add(yhteystiedotExpected);
        expectedInputit.add(kaupunginosaExpected);
        expectedInputit.add(kategoriaExpected);
        expectedInputit.add(amountExpected);
        expectedInputit.add(productAdminExpected);


        Add add = new Add();
        outputit = Add.inputChekkaus(juomanNimi,yhteystiedot,kaupunginosa,
                kategoria,amount,productAdmin);


        assertEquals(expectedInputit,outputit);

    }



}