package com.apm29.beanmodule;

import com.apm29.beanmodule.Init.Meta;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);

//        ArrayList<Meta> list=new ArrayList<>();
//        list.add(new Meta(200,"success"));
//        list.add(new Meta(200,"success"));
//        list.add(new Meta(200,"success"));
//        list.add(new Meta(200,"success"));
//        list.add(new Meta(100,"fail"));
//        list.add(new Meta(200,"success"));
//        list.add(new Meta(200,"success"));
//        list.add(new Meta(200,"success"));
//        Iterator<Meta> iterator = list.iterator();
//        while (iterator.hasNext()){
//            Meta bean = iterator.next();
//            if (bean.getCode()==100){
//                iterator.remove();
//            }
//        }
//        System.out.println(list);
    }
}