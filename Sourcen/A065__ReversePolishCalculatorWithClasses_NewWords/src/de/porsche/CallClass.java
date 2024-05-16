package de.porsche;

import java.util.HashMap;
import java.util.Stack;

public interface CallClass {
    int run(String [] tokens, int tokenPtr, Stack<Integer> stack, HashMap<String, Integer> registers, HashMap<String, String[]> userWords);
}