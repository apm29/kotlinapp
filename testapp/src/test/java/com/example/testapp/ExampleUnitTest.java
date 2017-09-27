package com.example.testapp;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        int t1=0b1000;
        int t2=0b0100;
        int t3=0b0010;
        int t4=t1|t2;
        System.out.println(t4&t3);
    }
}