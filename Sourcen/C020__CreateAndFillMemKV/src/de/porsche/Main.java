package de.porsche;

import com.kwerlingit.MemKV;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Main {
    public static void main(String[] args) {

        MemKV memKV = new NewMemKVDB().get();
    }
}