package com.acelych.atoz.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataHandler
{
    private static final int FULL_TYPE = 0;
    private static final int ONLY_CHOICE = 1;
    private static final int ONLY_SPELL = 2;
    private static final int ERROR = 3;

    public static final Random rand = new Random();

    private static List<Word> result = new ArrayList<>();
    private static List<Word> type01List = new ArrayList<>();
    private static List<Word> type02List = new ArrayList<>();

    public static int checkWordCount(List<Word> list, int quantity)
    {
        int countA = 0, countB = 0, countC = 0, countD = 0, countE = 0;

        for (Word temp : list)
        {
            switch (temp.proficiency)
            {
                case 0: countE++; break;
                case 1: countD++; break;
                case 2: countC++; break;
                case 3: countB++; break;
                case 4: countA++; break;
            }
        }

        if ((countE + countD + countC) >= 10 * quantity && (countC + countB + countA) >= 5 * quantity) // all enough
            return FULL_TYPE;
        else if ((countE + countD + countC) >= 10 * quantity) // only type01
            return ONLY_CHOICE;
        else if ((countC + countB + countA) >= 10 * quantity) // only type02
            return ONLY_SPELL;
        else
            return ERROR;
    }

    public static List<Word> generateTestList(List<Word> list, int quantity, int type)
    {
        result.clear();
        shuntList(list, type);

        switch (type)
        {
            case FULL_TYPE:
                out: while (result.size() < 10 * quantity)
                {
                    Word temp = type01List.get(Math.abs(rand.nextInt()) % type01List.size()); // extract word from 01
                    if (isWordAbandon(temp.proficiency, true))
                        continue;
                    for (Word reCheck : result)
                    {
                        if (reCheck.name.equals(temp.name))
                            continue out;
                    }
                    result.add(temp);
                }
                List<Word> secondStep = new ArrayList<>();
                out: while (secondStep.size() < 5 * quantity)
                {
                    Word temp = type02List.get(Math.abs(rand.nextInt()) % type02List.size()); // extract word from 02
                    if (isWordAbandon(temp.proficiency, false))
                        continue;
                    for (Word reCheck : secondStep)
                    {
                        if (reCheck.name.equals(temp.name))
                            continue out;
                    }
                    secondStep.add(temp);
                }
                result.addAll(secondStep);
                break;
            case ONLY_CHOICE:
                out: while (result.size() < 10 * quantity)
                {
                    Word temp = type01List.get(Math.abs(rand.nextInt()) % type01List.size()); // extract word from 01
                    if (isWordAbandon(temp.proficiency, true))
                        continue;
                    for (Word reCheck : result)
                    {
                        if (reCheck.name.equals(temp.name))
                            continue out;
                    }
                    result.add(temp);
                }
                break;
            case ONLY_SPELL:
                out: while (result.size() < 10 * quantity)
                {
                    Word temp = type02List.get(Math.abs(rand.nextInt()) % type02List.size()); // extract word from 02
                    if (isWordAbandon(temp.proficiency, false))
                        continue;
                    for (Word reCheck : result)
                    {
                        if (reCheck.name.equals(temp.name))
                            continue out;
                    }
                    result.add(temp);
                }
                break;
        }

        return result;
    }

    private static void shuntList(List<Word> list, int type)
    {
        if (type == FULL_TYPE || type == ONLY_CHOICE)
            type01List.clear();
        if (type == FULL_TYPE || type == ONLY_SPELL)
            type02List.clear();

        for (Word temp : list)
        {
            int tempProf = temp.proficiency;
            if (tempProf >= 0 && tempProf <= 2 && (type == FULL_TYPE || type == ONLY_CHOICE))
                type01List.add(temp);
            if (tempProf >= 2 && tempProf <= 4 && (type == FULL_TYPE || type == ONLY_SPELL))
                type02List.add(temp);
        }
    }

    private static boolean isWordAbandon(int proficiency, boolean isType01)
    {
        if (isType01)
        {
            switch (proficiency)
            {
                case 0:
                {
                    if (rand.nextInt() % 10 < 8) // 80%
                        return false;
                } break;
                case 1:
                {
                    if (rand.nextInt() % 10 < 6) // 60%
                        return false;
                } break;
                case 2:
                {
                    if (rand.nextInt() % 10 < 4) // 40%
                        return false;
                } break;
            }
        }
        else // isType02
        {
            switch (proficiency)
            {
                case 2:
                {
                    if (rand.nextInt() % 10 < 8) // 80%
                        return false;
                } break;
                case 3:
                {
                    if (rand.nextInt() % 10 < 6) // 60%
                        return false;
                } break;
                case 4:
                {
                    if (rand.nextInt() % 10 < 4) // 40%
                        return false;
                } break;
            }
        }
        return true;
    }
}
