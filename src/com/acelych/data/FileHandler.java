package com.acelych.data;

import java.io.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileHandler
{
    private static final File FILE_WORD_LIST = new File("./word_list.atz");
    private static final File FILE_CONFIG   = new File("./config.cfg");

    public static boolean checkFile()
    {
        try
        {
            if (!FILE_WORD_LIST.exists())
                return FILE_WORD_LIST.createNewFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static List<Word> readFile()
    {
        List<Word> wordList = new ArrayList<>();

        boolean isReadFinished = false;
        StringBuilder contentTemp = new StringBuilder("");
        List<String> wordBuilder = new ArrayList<>();

        Reader reader;
        try
        {
            reader = new InputStreamReader(new FileInputStream(FILE_WORD_LIST));
            int tempChar;
            while ((tempChar = reader.read()) != -1 || !isReadFinished)
            {
                if (((char) tempChar) != '\r' && ((char) tempChar) != '\n' && tempChar != -1)
                {
                    if (((char) tempChar) != ' ')
                    {
                        contentTemp.append(((char) tempChar));
                    }
                    else
                    {
                        wordBuilder.add(contentTemp.toString());
                        contentTemp.delete(0, contentTemp.length());
                    }
                }
                else if (((char) tempChar) == '\r')
                    continue;
                else
                {
                    wordBuilder.add(contentTemp.toString());
                    contentTemp.delete(0, contentTemp.length());

                    if (wordBuilder.size() == 7)
                        wordList.add(new Word(
                                wordBuilder.get(0), wordBuilder.get(1), wordBuilder.get(2),
                                Integer.valueOf(wordBuilder.get(3)),
                                Float.parseFloat(wordBuilder.get(4)),
                                Boolean.parseBoolean(wordBuilder.get(5)),
                                Boolean.parseBoolean(wordBuilder.get(6))));
                    wordBuilder.clear();
                }

                if (tempChar == -1)
                    isReadFinished = true;
            }
            reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return wordList;
    }

    public static void writeFile(Word word)
    {
        String content = "\n"
                + word.name + " "
                + word.translatedName + " "
                + word.registrationTime + " "
                + word.proficiency + " "
                + word.score + " "
                + word.isCET4 + " "
                + word.isCET6;

        try
        {
            FileWriter writer = new FileWriter(FILE_WORD_LIST, true);
            writer.write(content);
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void overrideListToFile(List<Word> list)
    {
        try
        {
            FileWriter writer = new FileWriter(FILE_WORD_LIST);
            writer.write("");
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        for (Word temp : list)
            writeFile(temp);
    }

    /**
     * check if file exist and add today's date info
     */
    public static void initConfig()
    {
        String date = DateFormat.getDateInstance().format(new Date());

        FileWriter writer;
        try
        {
            if (!FILE_CONFIG.exists())
            {
                writer = new FileWriter(FILE_CONFIG, true);
                writer.write(date + " " + false);
                writer.close();
            }
            else // file exist
            {
                writer = new FileWriter(FILE_CONFIG, true);
                String lastDate = getLastDate();
                if (!date.equals(lastDate))
                {
                    if (!lastDate.equals(""))
                    {
                        int days = getTimeDistance(lastDate, date);
                        System.out.println("Days: " + days);
                        List<Word> tempList = readFile();
                        List<Word> newList = new ArrayList<>();
                        for (Word temp : tempList)
                        {
                            System.out.print("Word:" + temp.name + " score:" + temp.score + " prof:" + temp.proficiency + " → ");
                            switch (temp.proficiency)
                            {
                                case 0: temp.changeScore(-1.0F * days); break;
                                case 1: temp.changeScore(-0.6F * days); break;
                                case 2: temp.changeScore(-0.4F * days); break;
                                case 3: temp.changeScore(-0.2F * days); break;
                                case 4: temp.changeScore(-0.1F * days); break;
                            }
                            System.out.println("score:" + temp.score + " prof:" + temp.proficiency);
                            newList.add(temp);
                        }
                        overrideListToFile(newList);
                        writer.write("\n" + date + " " + false);
                        writer.close();
                    }
                    else
                    {
                        writer.write(date + " " + false);
                        writer.close();
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void setTodayTestDone()
    {
        int c = -1;
        String trueStr = "true";
        RandomAccessFile pointer;

        try
        {
            pointer = new RandomAccessFile(FILE_CONFIG, "rw");
            pointer.seek(pointer.length() - 1);

            while (true)
            {
                c = pointer.read();
                if (c == ' ')
                    break;
                pointer.seek(pointer.getFilePointer() - 2);
            }
            pointer.setLength(pointer.getFilePointer());
            for (int i = 0; i < trueStr.length(); i++)
                pointer.writeByte(trueStr.charAt(i));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static boolean isTodayTestDone()
    {
        int c = -1;
        RandomAccessFile pointer;
        StringBuilder temp = new StringBuilder();;

        try
        {
            pointer = new RandomAccessFile(FILE_CONFIG, "r");
            pointer.seek(pointer.length() - 1);

            while (true)
            {
                c = pointer.read();
                if (c == ' ')
                    break;
                pointer.seek(pointer.getFilePointer() - 2);
            }
            while ((c = pointer.read()) != -1)
                temp.append((char) c);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return Boolean.parseBoolean(temp.toString());
    }

    //--------Getter--------//

    private static String getLastDate()
    {
        StringBuilder contentTemp = new StringBuilder("");
        Reader reader;
        try
        {
            reader = new InputStreamReader(new FileInputStream(FILE_CONFIG));
            int tempChar;
            boolean isFinished = false;
            while ((tempChar = reader.read()) != -1)
            {
                if (((char) tempChar) != '\r' && ((char) tempChar) != '\n')
                {
                    if ((char) tempChar != ' ')
                    {
                        if (!isFinished)
                            contentTemp.append((char) tempChar);
                    }
                    else
                        isFinished = true;
                }
                else
                {
                    contentTemp.delete(0 ,contentTemp.length());
                    isFinished = false;
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return contentTemp.toString();
    }

    private static int getTimeDistance(String dateBeforeStr, String dateNowStr)
    {
        SpecificDate dateBefore = getTimeDivided(dateBeforeStr);
        SpecificDate dateNow    = getTimeDivided(dateNowStr);

        if (dateBefore.year == dateNow.year)
            return getDayCountOfYear(dateNow) - getDayCountOfYear(dateBefore);
        else
        {
            int result = 0;

            for (int i = dateBefore.year + 1; i != dateNow.year; i++) // years between
            {
                result += 365;
                if (isLeapYear(i))
                    result++;
            }

            result += isLeapYear(dateBefore.year) ? 366 - getDayCountOfYear(dateBefore) : 365 - getDayCountOfYear(dateBefore);
            result += getDayCountOfYear(dateNow);

            return result;
        }
    }

    private static SpecificDate getTimeDivided(String date)
    {
        int order = 0;
        StringBuilder temp = new StringBuilder();
        SpecificDate dividedDate = new SpecificDate();

        for (int i = 0; i < date.length(); i++)
        {
            if (date.charAt(i) != '-')
                temp.append(date.charAt(i));
            else
            {
                switch (order++)
                {
                    case 0: dividedDate.year = Integer.valueOf(temp.toString()); break;
                    case 1: dividedDate.month = Integer.valueOf(temp.toString()); break;
                }
                temp.delete(0, temp.length());
            }
        }
        dividedDate.day = Integer.valueOf(temp.toString());

        return dividedDate;
    }

    private static int getDayCountOfYear(SpecificDate date)
    {
        int result = date.day;

        int month = date.month;
        for (int i = month - 1; i != 0; i--)
        {
            if (i == 1 || i == 3 || i == 5 || i == 7 || i == 8 || i == 10 || i == 12)
                result += 31;
            else if (i == 2)
            {
                if (isLeapYear(date.year))
                    result += 29;
                else
                    result += 28;
            }
            else
                result += 30;
        }

        return result;
    }

    private static boolean isLeapYear(int year)
    {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }

    //------ASSET-----//

    private static class SpecificDate
    {
        int year;
        int month;
        int day;
    }
}
