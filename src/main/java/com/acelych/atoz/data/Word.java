package com.acelych.atoz.data;

import java.text.DateFormat;
import java.util.Date;

public class Word
{
    public final String name;
    public final String translatedName;
    public final String registrationTime;
    public int proficiency;
    public float score;
    public boolean isCET4;
    public boolean isCET6;

    /**
     * constructor that default to set exam scope to false
     * @param name name of the word
     * @param translatedName meaning of the word in Chinese
     * @param proficiency proficiency of the word that user can reach
     */
    public Word(String name, String translatedName, int proficiency)
    {
        this(name, translatedName, proficiency, false, false);
    }

    /**
     * constructor that used for user to create new word
     * @param name name of the word
     * @param translatedName meaning of the word in Chinese
     * @param proficiency proficiency of the word that user can reach
     * @param isCET4 word that within the scope of CET4 exam
     * @param isCET6 word that within the scope of CET6 exam
     */
    public Word(String name, String translatedName, int proficiency, boolean isCET4, boolean isCET6)
    {
        this(name, translatedName, DateFormat.getDateInstance().format(new Date()), proficiency, 20 * proficiency, isCET4, isCET6);
    }

    /**
     * main constructor of this class, for reader to read file
     * @param name name of the word
     * @param translatedName meaning of the word in Chinese
     * @param registrationTime as the name describes
     * @param proficiency proficiency of the word that user can reach
     * @param isCET4 word that within the scope of CET4 exam
     * @param isCET6 word that within the scope of CET6 exam
     */
    public Word(String name, String translatedName, String registrationTime, int proficiency, float score, boolean isCET4, boolean isCET6)
    {
        this.name = name;
        this.translatedName = translatedName;
        this.registrationTime = registrationTime;

        this.isCET4 = isCET4;
        this.isCET6 = isCET6;

        if (proficiency < 0)
            this.proficiency = 0;
        else if (proficiency > 4)
            this.proficiency = 4;
        else
            this.proficiency = proficiency;

        this.score = score;
    }

    public void changeScore(float variable)
    {
        score += variable;

        if (score < 0)
            score = 0;
        else if (score > 100)
            score = 100;

        if (score < 20)
            proficiency = 0;
        else if (score < 40)
            proficiency = 1;
        else if (score < 60)
            proficiency = 2;
        else if (score < 80)
            proficiency = 3;
        else
            proficiency = 4;
    }
}
