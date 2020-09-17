package com.acelych.data;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class MediaHandler
{
    public static BufferedImage imageLogo, imageTitle, imageAdd, imageAddOn, imageList, imageListOn, imageTest, imageTestOn,
            imageA, imageB, imageC, imageD, imageE,
            imageAscending, imageDescending, imageDown, imageDownHL, imageFilter, imageSearch, imageSearchHL, imageCET4, imageCET4On, imageCET6, imageCET6On,
            imageTrue, imageExclamation,
            imageLabelA, imageLabelB, imageLabelC, imageLabelD, imageLabelE,
            imageLabel1, imageLabel2, imageLabel3, imageLabel4,
            imageContinue, imageContinueHL, imageCancel, imageCancelHL,
            imageCheckBox, imageCheckBoxOn;
    public static ImageIcon imageIconA, imageIconB, imageIconC, imageIconD, imageIconE, imageIconTrue, imageIconCheckBox, imageIconCheckBoxOn;

    static
    {
        try
        {
            imageLogo = ImageIO.read(getImage("image_logo.png"));
            imageTitle = ImageIO.read(getImage("image_title.png"));
            imageAdd = ImageIO.read(getImage("image_add.png"));
            imageAddOn = ImageIO.read(getImage("image_add_on.png"));
            imageList = ImageIO.read(getImage("image_list.png"));
            imageListOn = ImageIO.read(getImage("image_list_on.png"));
            imageTest = ImageIO.read(getImage("image_test.png"));
            imageTestOn = ImageIO.read(getImage("image_test_on.png"));
            imageA = ImageIO.read(getImage("image_A.png"));
            imageB = ImageIO.read(getImage("image_B.png"));
            imageC = ImageIO.read(getImage("image_C.png"));
            imageD = ImageIO.read(getImage("image_D.png"));
            imageE = ImageIO.read(getImage("image_E.png"));
            imageAscending = ImageIO.read(getImage("image_ascending.png"));
            imageDescending = ImageIO.read(getImage("image_descending.png"));
            imageDown = ImageIO.read(getImage("image_down.png"));
            imageDownHL = ImageIO.read(getImage("image_down_hl.png"));
            imageFilter = ImageIO.read(getImage("image_filter.png"));
            imageSearch = ImageIO.read(getImage("image_search.png"));
            imageSearchHL = ImageIO.read(getImage("image_search_hl.png"));
            imageCET4 = ImageIO.read(getImage("image_CET4.png"));
            imageCET4On = ImageIO.read(getImage("image_CET4_on.png"));
            imageCET6 = ImageIO.read(getImage("image_CET6.png"));
            imageCET6On = ImageIO.read(getImage("image_CET6_on.png"));
            imageTrue = ImageIO.read(getImage("image_true.png"));
            imageExclamation = ImageIO.read(getImage("image_exclamation.png"));
            imageLabelA = ImageIO.read(getImage("image_label_A.png"));
            imageLabelB = ImageIO.read(getImage("image_label_B.png"));
            imageLabelC = ImageIO.read(getImage("image_label_C.png"));
            imageLabelD = ImageIO.read(getImage("image_label_D.png"));
            imageLabelE = ImageIO.read(getImage("image_label_E.png"));
            imageLabel1 = ImageIO.read(getImage("image_label_1.png"));
            imageLabel2 = ImageIO.read(getImage("image_label_2.png"));
            imageLabel3 = ImageIO.read(getImage("image_label_3.png"));
            imageLabel4 = ImageIO.read(getImage("image_label_4.png"));
            imageContinue = ImageIO.read(getImage("image_continue.png"));
            imageContinueHL = ImageIO.read(getImage("image_continue_hl.png"));
            imageCancel = ImageIO.read(getImage("image_cancel.png"));
            imageCancelHL = ImageIO.read(getImage("image_cancel_hl.png"));
            imageCheckBox = ImageIO.read(getImage("image_checkBox.png"));
            imageCheckBoxOn = ImageIO.read(getImage("image_checkBox_on.png"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        imageIconA = new ImageIcon(imageA);
        imageIconB = new ImageIcon(imageB);
        imageIconC = new ImageIcon(imageC);
        imageIconD = new ImageIcon(imageD);
        imageIconE = new ImageIcon(imageE);
        imageIconTrue = new ImageIcon(imageTrue);
        imageIconCheckBox = new ImageIcon(imageCheckBox);
        imageIconCheckBoxOn = new ImageIcon(imageCheckBoxOn);
    }

    private static URL getImage(String name)
    {
        return Thread.currentThread().getContextClassLoader().getResource("images/" + name);
    }
}
