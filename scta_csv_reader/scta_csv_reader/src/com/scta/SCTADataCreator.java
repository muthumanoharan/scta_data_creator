package com.scta;

import com.scta.constants.SCTADataCreatorConstants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class SCTADataCreator {
    /**
     * Custom Code to read CSV
     */
    public static final String delimiter = SCTADataCreatorConstants.DELIMITER_COMMA;
    private static Object formattedStartDate;
    private static Object formattedTime;
    public static String Subject;
    public static String StartDate;
    public static String StartTime;
    public static String EndTime;
    public static String Categories;
    public static String OpportunityID;

    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';

    public static List<String> parseLine(String cvsLine) {
        return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators) {
        return parseLine(cvsLine, separators, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

        List<String> result = new ArrayList<>();

        //if empty, return!
        if (cvsLine == null && cvsLine.isEmpty()) {
            return result;
        }

        if (customQuote == ' ') {
            customQuote = DEFAULT_QUOTE;
        }

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {

                    //Fixed : allow "" in custom quote enclosed
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }

                }
            } else {
                if (ch == customQuote) {

                    inQuotes = true;

                    //Fixed : allow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }

                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separators) {

                    result.add(curVal.toString());

                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch == '\r') {
                    //ignore LF characters
                    continue;
                } else if (ch == '\n') {
                    //the end, break!
                    break;
                } else {
                    curVal.append(ch);
                }
            }

        }

        result.add(curVal.toString());

        return result;
    }

    public static String getDiffTime(String StartTime, String EndTime) throws ParseException {
        String timediffString;
        SimpleDateFormat format = new SimpleDateFormat(SCTADataCreatorConstants.TIME_FORMAT);
        Date date1;
        date1 = format.parse(StartTime);
        Date date2 = format.parse(EndTime);
        long difference1 = ((date2.getTime() - date1.getTime()) / 1000) / 60;
        long difference = date2.getTime() - date1.getTime();
        long diffSeconds = difference / 1000 % 60;
        long diffMinutes = difference / (60 * 1000) % 60;
        long diffHours = difference / (60 * 60 * 1000) % 24;
        long diffDays = difference / (24 * 60 * 60 * 1000);
        /*System.out.print(diffDays + " days, ");
                System.out.print(diffHours + " hours, ");
                System.out.print(diffMinutes + " minutes, ");
                System.out.print(diffSeconds + " seconds.");*/
        if (difference1 == 60) {
            timediffString = "1.0";
            //System.out.println(timediffString);
        } else if (difference1 == 30) {
            timediffString = "0.5";
            //System.out.println(timediffString);
        } else {
            timediffString = "0";
            //System.out.println(timediffString);
        }

        return timediffString;
    }

    public static void readCSVWriteToTextFile(String email_ID, String csvFile) {
        FileWriter writer;
        try {
            writer = new FileWriter(SCTADataCreatorConstants.OUTPUT_FILE_NAME, true);
            System.out.println("Email:" + email_ID + "File Location:" + csvFile);
            Scanner scanner = new Scanner(new File(csvFile));
            if (scanner.hasNext() == true) {
                //System.out.println("scanner.nextLine() ---->" +scanner.nextLine());
                scanner.nextLine();
            } else {
                System.out.println("Error: File is empty");
                //return null;
            }
            while (scanner.hasNext()) {
                List<String> line = parseLine(scanner.nextLine());
                /*System.out.println(line.get(0));
                System.out.println(line.get(1));
                System.out.println(line.get(2));
                System.out.println(line.get(3));
                System.out.println(line.get(4));
                System.out.println(line.get(5));
                System.out.println(line.get(6));*/

                Subject = line.get(0);
                StartDate = line.get(1);
                StartTime = line.get(2);
                EndTime = line.get(4);
                Categories = line.get(6);
                //System.out.println(StartTime);
                //System.out.println(EndTime);
                if (Categories.isEmpty()) {
                    //System.out.println("It's empty Categories"+Categories);
                } else if (Categories.contains(SCTADataCreatorConstants.CATEGORIES_HOLIDAY)) {

                } else {
                    String formattedSubject = SCTADataCreatorConstants.SUBJECT_CLOUD + Subject;
                    //System.out.println(formattedSubject);

                    String originalStringFormat = SCTADataCreatorConstants.DATE_ORIGINAL_FORMAT;
                    String desiredStringFormat = SCTADataCreatorConstants.DATE_DESIRED_FORMAT;
                    SimpleDateFormat readingFormat = new SimpleDateFormat(originalStringFormat);
                    SimpleDateFormat outputFormat = new SimpleDateFormat(desiredStringFormat);
                    try {
                        Date date = readingFormat.parse(StartDate);
                        formattedStartDate = outputFormat.format(date);
                        //System.out.println(","+formattedStartDate);
                    } catch (ParseException e) {

                        e.printStackTrace();
                    }


                    try {
                        formattedTime = getDiffTime(StartTime, EndTime);
                        //System.out.println(formattedTime);
                    } catch (ParseException e) {
                    }

                    String formattedCategories;
                    if (Categories.contains(SCTADataCreatorConstants.CATEGORIES_WRONG_CUSTOMERMEETING)) {
                        formattedCategories = SCTADataCreatorConstants.CATEGORIES_CUSTOMERMEETING;
                        //System.out.println(formattedCategories);
                    } else if (Categories == "") {
                        formattedCategories = "";
                        //System.out.println(Categories);
                    } else {
                        formattedCategories = Categories;
                        //System.out.println(Categories);
                    }

                    if (Categories.contains(SCTADataCreatorConstants.CATEGORIES_PLANNINGTERRITORY) ||
                        Categories.contains(SCTADataCreatorConstants.CATEGORIES_TRAININGPERSONALDEVELOPMENT) ||
                        Categories.contains(SCTADataCreatorConstants.CATEGORIES_EVENTSEMINAR) ||
                        Categories.contains(SCTADataCreatorConstants.CATEGORIES_INTERNALMEETING) ||
                        Categories.contains(SCTADataCreatorConstants.CATEGORIES_TIMEOFFANY) ||
                        Categories.contains(SCTADataCreatorConstants.CATEGORIES_TRAININGDELIVERY) ||
                        Categories.contains(SCTADataCreatorConstants.CATEGORIES_TRAVEL)) {
                        OpportunityID = "";
                    } else {
                        OpportunityID = SCTADataCreatorConstants.DUMMY_OPP_ID;
                    }
                    /*System.out.println("MUTHUKRISHNAN.MANOHARAN@ORACLE.COM," + formattedCategories + "," +
                                       formattedStartDate + "," + formattedTime + ",," + OpportunityID + ",,,," +
                                       formattedSubject);*/
                    writer.write(email_ID + "," + formattedCategories + "," + formattedStartDate + "," + formattedTime +
                                 ",," + OpportunityID + ",,,," + formattedSubject);
                    writer.write("\r\n");
                }
            }
            scanner.close();
            writer.close();
            JOptionPane jo = new JOptionPane();
            JOptionPane.showMessageDialog(jo, SCTADataCreatorConstants.SUCCESS);
        } catch (IOException e) {
            JOptionPane jo = new JOptionPane();
            JOptionPane.showMessageDialog(jo, SCTADataCreatorConstants.FAILED);
        }
    }

    /*public static void readCSVWriteToTextFileTesting(String email_ID, String csvFile) {
        FileWriter writer;
        try {
            writer = new FileWriter(SCTADataCreatorConstants.OUTPUT_FILE_NAME, true);
            System.out.println("Email:" + email_ID + "File Location:" + csvFile);
            Scanner scanner = new Scanner(new File(csvFile));
            if(scanner.hasNext()==true)
            {
            System.out.println("scanner.nextLine() ---->" +scanner.nextLine());
               scanner.nextLine();
            }
            else
            {
                System.out.println("Error: File is empty");
                //return null;
            }
            while (scanner.hasNext()) {
                //System.out.println("scanner.nextLine() ---->" +scanner.nextLine());
                List<String> line = parseLine(scanner.nextLine());
                //System.out.println(line.size());

                System.out.println(line.get(0));
                System.out.println(line.get(1));
                System.out.println(line.get(2));
                System.out.println(line.get(3));
                System.out.println(line.get(4));
                System.out.println(line.get(5));
                System.out.println(line.get(6));

                //for (int ln = line.size(); ln <= line.size(); ln++){
                /*if (line.get(0) != null){
                System.out.println(line.get(0));
                }
                if (line.get(1) != null){
                System.out.println(line.get(1));
                }
                if (line.get(2) != null){
                System.out.println(line.get(2));
                }
                if (line.get(3) != null){
                System.out.println(line.get(3));
                }
                if (line.get(4) != null){
                System.out.println(line.get(4));
                }
                if (line.get(5).isEmpty()){

                }else{
                    System.out.println(line.get(5));
                }
                if (line.get(6) != null){
                System.out.println(line.get(6));
                }
                //}
                Subject = line.get(0);
                StartDate = line.get(1);
                StartTime = line.get(2);
                EndTime = line.get(4);
                Categories = line.get(6);
                //System.out.println(StartTime);
                //System.out.println(EndTime);
                if (Categories.isEmpty()) {
                    //System.out.println("It's empty Categories"+Categories);
                } else if (Categories.contains(SCTADataCreatorConstants.CATEGORIES_HOLIDAY)) {

                } else {
                    String formattedSubject = SCTADataCreatorConstants.SUBJECT_CLOUD + Subject;
                    //System.out.println(formattedSubject);

                    String originalStringFormat = SCTADataCreatorConstants.DATE_ORIGINAL_FORMAT;
                    String desiredStringFormat = SCTADataCreatorConstants.DATE_DESIRED_FORMAT;
                    SimpleDateFormat readingFormat = new SimpleDateFormat(originalStringFormat);
                    SimpleDateFormat outputFormat = new SimpleDateFormat(desiredStringFormat);
                    try {
                        Date date = readingFormat.parse(StartDate);
                        formattedStartDate = outputFormat.format(date);
                        //System.out.println(","+formattedStartDate);
                    } catch (ParseException e) {

                        e.printStackTrace();
                    }


                    try {
                        formattedTime = getDiffTime(StartTime, EndTime);
                        //System.out.println(formattedTime);
                    } catch (ParseException e) {
                    }

                    String formattedCategories;
                    if (Categories.contains(SCTADataCreatorConstants.CATEGORIES_WRONG_CUSTOMERMEETING)) {
                        formattedCategories = SCTADataCreatorConstants.CATEGORIES_CUSTOMERMEETING;
                        //System.out.println(formattedCategories);
                    } else if (Categories == "") {
                        formattedCategories = "";
                        //System.out.println(Categories);
                    } else {
                        formattedCategories = Categories;
                        //System.out.println(Categories);
                    }

                    if (Categories.contains(SCTADataCreatorConstants.CATEGORIES_PLANNINGTERRITORY) ||
                        Categories.contains(SCTADataCreatorConstants.CATEGORIES_TRAININGPERSONALDEVELOPMENT) ||
                        Categories.contains(SCTADataCreatorConstants.CATEGORIES_EVENTSEMINAR) ||
                        Categories.contains(SCTADataCreatorConstants.CATEGORIES_INTERNALMEETING) ||
                        Categories.contains(SCTADataCreatorConstants.CATEGORIES_TIMEOFFANY) ||
                        Categories.contains(SCTADataCreatorConstants.CATEGORIES_TRAININGDELIVERY) ||
                        Categories.contains(SCTADataCreatorConstants.CATEGORIES_TRAVEL)) {
                        OpportunityID = "";
                    } else {
                        OpportunityID = SCTADataCreatorConstants.DUMMY_OPP_ID;
                    }
                    System.out.println("MUTHUKRISHNAN.MANOHARAN@ORACLE.COM," + formattedCategories + "," +
                                       formattedStartDate + "," + formattedTime + ",," + OpportunityID + ",,,," +
                                       formattedSubject);
                    writer.write(email_ID + "," + formattedCategories + "," + formattedStartDate + "," + formattedTime +
                                 ",," + OpportunityID + ",,,," + formattedSubject);
                    writer.write("\r\n");
                }
            }
            scanner.close();
            writer.close();
            JOptionPane jo = new JOptionPane();
            JOptionPane.showMessageDialog(jo, SCTADataCreatorConstants.SUCCESS);
        } catch (IOException e) {
            JOptionPane jo = new JOptionPane();
            JOptionPane.showMessageDialog(jo, SCTADataCreatorConstants.FAILED);
        }
    }*/
}
