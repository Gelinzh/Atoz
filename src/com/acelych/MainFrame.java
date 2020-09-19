package com.acelych;

import com.acelych.data.DataHandler;
import com.acelych.data.FileHandler;
import com.acelych.data.MediaHandler;
import com.acelych.data.Word;
import com.acelych.widget.DialogFactory;
import com.acelych.widget.GlassPanel;
import com.acelych.widget.MainTable;
import com.acelych.widget.SeparatorFactory;
import com.acelych.ui.ComboBoxUI;
import com.acelych.ui.ScrollBarUI;
import com.acelych.ui.SliderUI;
import com.acelych.ui.TextFieldUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainFrame extends JFrame
{
    private static JPanel leftPanel, rightPanel;
    private static CardLayout rightLayout;

    private List<Word> wordList, wordListTemp = new ArrayList<>(), wordListTest = new ArrayList<>();
    private DefaultTableModel wordListModel;
    private MainTable worldListTable;

    private GlassPanel rightPanelGlass;
    private BufferedImage imageTemp, imageAddWord, imageWordList, imageDailyTest;
    private boolean isTimerSwitchEnable = false;
    private int direct = 0, page = 0, progress = 0;
    private String panelCurrent, panelTarget;
    private Timer timer;
    // Add Word
    private JPanel rightPanelAddWord;
    private JTextField textFieldName, textFieldTrans;
    private JLabel nameExclamation, transExclamation;
    private JSlider sliderProf;
    private JCheckBox checkBoxIsCET4, checkBoxIsCET6;
    // Word List
    private JPanel rightPanelWordList;
    private JCheckBox wordListArrangementOrder;
    private JComboBox<String> wordListArrangement;
    private JTextField wordListSearch;
    private JCheckBox wordListOnlyCET4, wordListOnlyCET6;
    // Daily Test
    private JPanel rightPanelDailyTest;
    private JSlider sliderQuantity;
    private JComboBox<String> dailyTestRange;
    private boolean isUpdateScore, isTesting = false, isOtherPanelShowing = false;
    private int type;
    private ListIterator<Word> wordIterator;
    // Choice Question
    private JPanel rightPanelChoice;
    private JLabel choiceTitle;
    private JCheckBox choiceA, choiceB, choiceC;
    private JButton choiceNext;
    private Word choiceTarget;
    private int randomKey;
    private boolean choiceIsAvailable;
    // Spelling Question
    private JPanel rightPanelSpell;
    private JLabel spellTitle, spellTip;
    private JButton spellNext;
    private JTextField spellField;
    private Word spellTarget;
    private String spellContent;
    private boolean spellFieldIsAvailable;
    //Test Summary
    private JPanel rightPanelSummary;
    private JLabel summaryCorrectCount, summaryAllCount, summaryAccuracy;
    private int summaryCorrectCountInt;


    private DialogFactory.OptionDialog dialogOnDelete, dialogOnRedoTest, dialogOnQuitTest;
    private DialogFactory.ConfirmationDialog dialogOnNotEnoughWords, dialogOnAddWordSucceed;

    public static Color
            GrayType00 = new Color(170, 170, 170),
            GrayType01 = new Color(220, 220, 220),
            GrayType02 = new Color(245, 245, 245),
            GrayType03 = new Color(250, 250, 250),
            RedType01  = new Color(242, 111, 93),
            GreenType01= new Color(175, 228, 61),
            BlackType01= new Color(51, 51, 51),
            BlackType02= new Color(120, 120, 120);

    public static Font
            consolas01 = new Font("Consolas", Font.PLAIN, 12),
            consolas02 = new Font("Consolas", Font.PLAIN, 15),
            consolas03 = new Font("Consolas", Font.BOLD, 15),
            yahei01 = new Font("Microsoft YaHei", Font.PLAIN, 12),
            yahei02 = new Font("Microsoft YaHei", Font.PLAIN, 15);

    private static ArrayList<String> pageList = new ArrayList<String>(){{
        add("addWord");
        add("wordList");
        add("dailyTest");
    }};

    private static final int FRAMES = 40;
    private static final int REFRESH_LENGTH = 15;

    MainFrame(String string)
    {
        super(string);

        Toolkit kit = Toolkit.getDefaultToolkit();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900, 500);
        this.setResizable(false);
        this.setLocation(kit.getScreenSize().width / 2 - this.getWidth() / 2, kit.getScreenSize().height / 2 - this.getHeight() / 2);

        //Init Resources
        initFile();

        //Panel Prepare
        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);
        SpringLayout leftLayout = new SpringLayout();
        rightLayout = new CardLayout();
        leftPanel = new JPanel(leftLayout);
        rightPanelGlass = new GlassPanel();
        rightPanel = new JPanel(rightLayout);
        contentPane.add(leftPanel);
        contentPane.add(rightPanelGlass);
        contentPane.add(rightPanel);
        leftPanel.setOpaque(true);
        leftPanel.setBackground(GrayType02);
        leftPanel.setBounds(0, 0, this.getWidth() / 3, this.getHeight());
        rightPanel.setOpaque(true);
        rightPanel.setBackground(GrayType03);
        rightPanel.setBounds(this.getWidth() / 3, 0, this.getWidth() * 2 / 3, this.getHeight());
        rightPanelGlass.setBounds(this.getWidth() / 3, 0, this.getWidth() * 2 / 3, this.getHeight());

        //Init Panels
        initLeftPanel(leftLayout);
        initRightPanel();

        //Init Other Widgets
        dialogOnDelete = DialogFactory.createOptionalDialog(this, rightPanel, "Are You Certain ?", "(Deletion is irreversible)");
        dialogOnRedoTest = DialogFactory.createOptionalDialog(this, rightPanel, "You've Done It Today !", "(Score resolving is disabled)");
        dialogOnQuitTest = DialogFactory.createOptionalDialog(this, rightPanel, "Are You Certain ?", "(Test isn't finished yet)");
        dialogOnNotEnoughWords = DialogFactory.createConfirmationDialog(this, rightPanel, "Not Enough of Words !");
        dialogOnAddWordSucceed = DialogFactory.createConfirmationDialog(this, rightPanel, "");
        imageTemp = new BufferedImage(rightPanel.getWidth(), rightPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
        imageAddWord = new BufferedImage(rightPanel.getWidth(), rightPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
        imageWordList = new BufferedImage(rightPanel.getWidth(), rightPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
        imageDailyTest = new BufferedImage(rightPanel.getWidth(), rightPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                if (isTimerSwitchEnable)
                {
                    switchMoveAtOnce();
                    if (--progress == 0)
                    {
                        rightLayout.show(rightPanel, panelTarget);
                        switch (panelTarget) //refresh image temp
                        {
                            case "addWord": rightPanel.paintComponents(imageAddWord.getGraphics()); break;
                            case "wordList": rightPanel.paintComponents(imageWordList.getGraphics()); break;
                            case "dailyTest": rightPanel.paintComponents(imageDailyTest.getGraphics()); break;
                        }
                        panelCurrent = panelTarget;
                        rightPanelGlass.isSwitchActive = false;
                        rightPanelGlass.reset();
                        rightPanelGlass.repaint();
                        isTimerSwitchEnable = false;
                    }
                }
            }
        }, 0, REFRESH_LENGTH);
    }

    private void initFile()
    {
        if (!FileHandler.checkFile())
            return;
        FileHandler.initConfig();

        //wordList
        wordList = FileHandler.readFile();
        wordListTemp.addAll(wordList);
        wordListModel = new DefaultTableModel();
        worldListTable = new MainTable(wordListModel);

        //worldListModel
        wordListModel.addColumn("NAME");
        wordListModel.addColumn("TRANS");
        wordListModel.addColumn("TIME");
        wordListModel.addColumn("PROF");
        wordListModel.addColumn("CET4");
        wordListModel.addColumn("CET6");
        applyListToModel(wordListTemp);

        //worldListTable
        worldListTable.getTableHeader().setDefaultRenderer(new HeaderRenderer());
        worldListTable.getColumnModel().getColumn(0).setCellRenderer(new RegularRenderer(consolas01));
        worldListTable.getColumnModel().getColumn(1).setCellRenderer(new RegularRenderer(yahei01));
        worldListTable.getColumnModel().getColumn(2).setCellRenderer(new RegularRenderer(consolas01));
        worldListTable.getColumnModel().getColumn(3).setCellRenderer(new ProficiencyRenderer());
        worldListTable.getColumnModel().getColumn(4).setCellRenderer(new BooleanRenderer());
        worldListTable.getColumnModel().getColumn(5).setCellRenderer(new BooleanRenderer());
        worldListTable.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent e)
            {
                if (e.getKeyCode() == 127) //delete key code
                onDeleteKeyReleased();
            }
        });
    }

    private void initLeftPanel(SpringLayout leftLayout)
    {
        //Realize
        JLabel labelLogo = new JLabel(new ImageIcon(MediaHandler.imageLogo));
        JLabel labelTitle = new JLabel(new ImageIcon(MediaHandler.imageTitle));
        JButton buttonAddNewWord = new JButton(new ImageIcon(MediaHandler.imageAdd));
        JButton buttonCheckWordList = new JButton(new ImageIcon(MediaHandler.imageList));
        JButton buttonFinishDailyTest = new JButton(new ImageIcon(MediaHandler.imageTest));

        leftPanel.add(labelLogo);
        leftPanel.add(labelTitle);
        leftPanel.add(buttonAddNewWord);
        leftPanel.add(buttonCheckWordList);
        leftPanel.add(buttonFinishDailyTest);

        labelLogo.setPreferredSize(new Dimension(MediaHandler.imageLogo.getWidth(), MediaHandler.imageLogo.getHeight()));
        labelTitle.setPreferredSize(new Dimension(MediaHandler.imageTitle.getWidth(), MediaHandler.imageTitle.getHeight()));

        buttonAddNewWord.setOpaque(false);
        buttonAddNewWord.setBorderPainted(false);
        buttonAddNewWord.setContentAreaFilled(false);
        buttonAddNewWord.setRolloverIcon(new ImageIcon(MediaHandler.imageAddOn));
        buttonAddNewWord.setPreferredSize(new Dimension(MediaHandler.imageAdd.getWidth(), MediaHandler.imageAdd.getHeight()));
        buttonAddNewWord.addActionListener(e -> onSwitchButtonClicked("addWord"));

        buttonCheckWordList.setOpaque(false);
        buttonCheckWordList.setBorderPainted(false);
        buttonCheckWordList.setContentAreaFilled(false);
        buttonCheckWordList.setRolloverIcon(new ImageIcon(MediaHandler.imageListOn));
        buttonCheckWordList.setPreferredSize(new Dimension(MediaHandler.imageList.getWidth(), MediaHandler.imageList.getHeight()));
        buttonCheckWordList.addActionListener(e -> onSwitchButtonClicked("wordList"));

        buttonFinishDailyTest.setOpaque(false);
        buttonFinishDailyTest.setBorderPainted(false);
        buttonFinishDailyTest.setContentAreaFilled(false);
        buttonFinishDailyTest.setRolloverIcon(new ImageIcon(MediaHandler.imageTestOn));
        buttonFinishDailyTest.setPreferredSize(new Dimension(MediaHandler.imageTest.getWidth(), MediaHandler.imageTest.getHeight()));
        buttonFinishDailyTest.addActionListener(e -> onSwitchButtonClicked("dailyTest"));

        //Layout
        SpringLayout.Constraints labelLogoC = leftLayout.getConstraints(labelLogo);
        labelLogoC.setX(Spring.constant((leftPanel.getWidth() - labelLogo.getPreferredSize().width) / 2));
        labelLogoC.setY(Spring.constant(leftPanel.getHeight() / 8));

        SpringLayout.Constraints labelTitleC = leftLayout.getConstraints(labelTitle);
        labelTitleC.setConstraint(SpringLayout.NORTH, Spring.sum(labelLogoC.getConstraint(SpringLayout.SOUTH), Spring.constant(10)));
        labelTitleC.setConstraint(SpringLayout.WEST, Spring.constant((leftPanel.getWidth() - labelTitle.getPreferredSize().width) / 2));

        SpringLayout.Constraints buttonAddNewWordC = leftLayout.getConstraints(buttonAddNewWord);
        SpringLayout.Constraints buttonCheckWordListC = leftLayout.getConstraints(buttonCheckWordList);
        SpringLayout.Constraints buttonFinishDailyTestC = leftLayout.getConstraints(buttonFinishDailyTest);

        buttonAddNewWordC.setConstraint(SpringLayout.NORTH, Spring.sum(labelTitleC.getConstraint(SpringLayout.SOUTH), Spring.constant(65)));
        buttonCheckWordListC.setConstraint(SpringLayout.NORTH, Spring.sum(buttonAddNewWordC.getConstraint(SpringLayout.SOUTH), Spring.constant(15)));
        buttonFinishDailyTestC.setConstraint(SpringLayout.NORTH, Spring.sum(buttonCheckWordListC.getConstraint(SpringLayout.SOUTH), Spring.constant(15)));
        buttonFinishDailyTestC.setConstraint(SpringLayout.WEST, Spring.constant((leftPanel.getWidth() - buttonFinishDailyTest.getPreferredSize().width) / 2 + 10));
        buttonAddNewWordC.setConstraint(SpringLayout.WEST, buttonFinishDailyTestC.getConstraint(SpringLayout.WEST));
        buttonCheckWordListC.setConstraint(SpringLayout.WEST, buttonFinishDailyTestC.getConstraint(SpringLayout.WEST));
    }

    private void initRightPanel()
    {
        initRightPanelAddWord();
        rightPanel.add(rightPanelAddWord, "addWord");
        initRightPanelWordList();
        rightPanel.add(rightPanelWordList, "wordList");
        initRightPanelDailyTest();
        rightPanel.add(rightPanelDailyTest, "dailyTest");
        initRightPanelQuestion();
        rightPanel.add(rightPanelChoice, "choice");
        rightPanel.add(rightPanelSpell, "spell");
        rightPanel.add(rightPanelSummary, "summary");
    }

    private void initRightPanelAddWord()
    {
        SpringLayout addWordLayout = new SpringLayout();
        rightPanelAddWord = new JPanel(addWordLayout);
        rightPanelAddWord.setBackground(GrayType03);
        rightPanelAddWord.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, GrayType01));
        rightPanelAddWord.setBounds(this.getWidth() / 3, 0, this.getWidth() * 2 / 3, this.getHeight());

        JLabel name = new JLabel("Word Name");
        JLabel trans = new JLabel("Translation");
        JLabel prof = new JLabel("Proficiency");
        JLabel labelA = new JLabel(new ImageIcon(MediaHandler.imageLabelA)),
                labelB = new JLabel(new ImageIcon(MediaHandler.imageLabelB)),
                labelC = new JLabel(new ImageIcon(MediaHandler.imageLabelC)),
                labelD = new JLabel(new ImageIcon(MediaHandler.imageLabelD)),
                labelE = new JLabel(new ImageIcon(MediaHandler.imageLabelE));
        textFieldName = new JTextField(12);
        textFieldTrans = new JTextField(12);
        nameExclamation = new JLabel(new ImageIcon(MediaHandler.imageExclamation));
        transExclamation = new JLabel(new ImageIcon(MediaHandler.imageExclamation));
        sliderProf = new JSlider(SwingConstants.HORIZONTAL, 0, 4, 0);
        checkBoxIsCET4 = new JCheckBox(new ImageIcon(MediaHandler.imageCET4));
        checkBoxIsCET6 = new JCheckBox(new ImageIcon(MediaHandler.imageCET6));
        JButton buttonContinue = new JButton(new ImageIcon(MediaHandler.imageContinue));

        name.setFont(consolas02);
        name.setPreferredSize(new Dimension(120, 30));
        name.setHorizontalAlignment(SwingConstants.RIGHT);
        trans.setFont(consolas02);
        trans.setPreferredSize(new Dimension(120, 30));
        trans.setHorizontalAlignment(SwingConstants.RIGHT);
        prof.setFont(consolas02);
        prof.setPreferredSize(new Dimension(120, 30));
        prof.setHorizontalAlignment(SwingConstants.RIGHT);
        textFieldName.setBorder(null);
        textFieldName.setFont(yahei02);
        textFieldName.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldName.setBackground(GrayType03);
        textFieldName.setUI(new TextFieldUI());
        textFieldName.setPreferredSize(new Dimension(80, 30));
        textFieldTrans.setBorder(null);
        textFieldTrans.setFont(yahei02);
        textFieldTrans.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldTrans.setBackground(GrayType03);
        textFieldTrans.setUI(new TextFieldUI());
        textFieldTrans.setPreferredSize(new Dimension(80, 30));
        sliderProf.setFont(consolas02);
        sliderProf.setBackground(GrayType03);
        sliderProf.setPreferredSize(new Dimension(168, 30));
        sliderProf.setUI(new SliderUI(sliderProf, true));
        sliderProf.addChangeListener(e ->
        {
            labelA.setVisible(false); labelB.setVisible(false); labelC.setVisible(false); labelD.setVisible(false); labelE.setVisible(false);

            switch (sliderProf.getValue())
            {
                case 0: labelE.setVisible(true); break;
                case 1: labelD.setVisible(true); break;
                case 2: labelC.setVisible(true); break;
                case 3: labelB.setVisible(true); break;
                case 4: labelA.setVisible(true); break;
            }
        });
        labelA.setVisible(false); labelB.setVisible(false); labelC.setVisible(false); labelD.setVisible(false);

        checkBoxIsCET4.setSelectedIcon(new ImageIcon(MediaHandler.imageCET4On));
        checkBoxIsCET4.setContentAreaFilled(false);
        checkBoxIsCET6.setSelectedIcon(new ImageIcon(MediaHandler.imageCET6On));
        checkBoxIsCET6.setContentAreaFilled(false);

        JLabel separator = SeparatorFactory.builder(SeparatorFactory.SHORT);

        buttonContinue.setBorderPainted(false);
        buttonContinue.setContentAreaFilled(false);
        buttonContinue.setFocusPainted(false);
        buttonContinue.setPressedIcon(new ImageIcon(MediaHandler.imageContinueHL));
        buttonContinue.addActionListener(e -> addWordManager());

        nameExclamation.setVisible(false);
        transExclamation.setVisible(false);

        rightPanelAddWord.add(name);
        rightPanelAddWord.add(trans);
        rightPanelAddWord.add(prof);
        rightPanelAddWord.add(textFieldName);
        rightPanelAddWord.add(textFieldTrans);
        rightPanelAddWord.add(nameExclamation);
        rightPanelAddWord.add(transExclamation);
        rightPanelAddWord.add(sliderProf);
        rightPanelAddWord.add(labelA);
        rightPanelAddWord.add(labelB);
        rightPanelAddWord.add(labelC);
        rightPanelAddWord.add(labelD);
        rightPanelAddWord.add(labelE);
        rightPanelAddWord.add(checkBoxIsCET4);
        rightPanelAddWord.add(checkBoxIsCET6);
        rightPanelAddWord.add(separator);
        rightPanelAddWord.add(buttonContinue);

        SpringLayout.Constraints nameC = addWordLayout.getConstraints(name);
        SpringLayout.Constraints transC = addWordLayout.getConstraints(trans);
        SpringLayout.Constraints profC = addWordLayout.getConstraints(prof);
        SpringLayout.Constraints textFieldNameC = addWordLayout.getConstraints(textFieldName);
        SpringLayout.Constraints textFieldTransC = addWordLayout.getConstraints(textFieldTrans);
        SpringLayout.Constraints sliderProfC = addWordLayout.getConstraints(sliderProf);
        SpringLayout.Constraints labelAC = addWordLayout.getConstraints(labelA);
        SpringLayout.Constraints labelBC = addWordLayout.getConstraints(labelB);
        SpringLayout.Constraints labelCC = addWordLayout.getConstraints(labelC);
        SpringLayout.Constraints labelDC = addWordLayout.getConstraints(labelD);
        SpringLayout.Constraints labelEC = addWordLayout.getConstraints(labelE);
        SpringLayout.Constraints isCET4C = addWordLayout.getConstraints(checkBoxIsCET4);
        SpringLayout.Constraints isCET6C = addWordLayout.getConstraints(checkBoxIsCET6);
        SpringLayout.Constraints separatorC = addWordLayout.getConstraints(separator);
        SpringLayout.Constraints buttonContinueC = addWordLayout.getConstraints(buttonContinue);

        nameC.setConstraint(SpringLayout.NORTH, Spring.constant(rightPanelAddWord.getHeight() / 4));
        nameC.setConstraint(SpringLayout.EAST, Spring.constant(rightPanelAddWord.getWidth() / 7 * 3));
        transC.setConstraint(SpringLayout.NORTH, Spring.sum(nameC.getConstraint(SpringLayout.SOUTH), Spring.constant(10)));
        transC.setConstraint(SpringLayout.EAST, nameC.getConstraint(SpringLayout.EAST));
        profC.setConstraint(SpringLayout.NORTH, Spring.sum(transC.getConstraint(SpringLayout.SOUTH), Spring.constant(10)));
        profC.setConstraint(SpringLayout.EAST, nameC.getConstraint(SpringLayout.EAST));
        textFieldNameC.setConstraint(SpringLayout.NORTH, Spring.sum(nameC.getConstraint(SpringLayout.NORTH), Spring.constant(0)));
        textFieldNameC.setConstraint(SpringLayout.WEST, Spring.sum(nameC.getConstraint(SpringLayout.EAST), Spring.constant(8)));
        textFieldTransC.setConstraint(SpringLayout.NORTH, Spring.sum(transC.getConstraint(SpringLayout.NORTH), Spring.constant(0)));
        textFieldTransC.setConstraint(SpringLayout.WEST, Spring.sum(transC.getConstraint(SpringLayout.EAST), Spring.constant(8)));
        sliderProfC.setConstraint(SpringLayout.NORTH, Spring.sum(profC.getConstraint(SpringLayout.NORTH), Spring.constant(4)));
        sliderProfC.setConstraint(SpringLayout.WEST, Spring.sum(profC.getConstraint(SpringLayout.EAST), Spring.constant(8)));
        labelAC.setX(Spring.constant(412)); labelAC.setY(Spring.constant(240));
        labelBC.setX(Spring.constant(374)); labelBC.setY(Spring.constant(240));
        labelCC.setX(Spring.constant(334)); labelCC.setY(Spring.constant(240));
        labelDC.setX(Spring.constant(295)); labelDC.setY(Spring.constant(240));
        labelEC.setX(Spring.constant(255)); labelEC.setY(Spring.constant(240));
        isCET4C.setConstraint(SpringLayout.NORTH, Spring.sum(profC.getConstraint(SpringLayout.SOUTH), Spring.constant(60)));
        isCET4C.setConstraint(SpringLayout.EAST, Spring.sum(nameC.getConstraint(SpringLayout.EAST), Spring.constant(-30)));
        separatorC.setConstraint(SpringLayout.NORTH, isCET4C.getConstraint(SpringLayout.NORTH));
        separatorC.setConstraint(SpringLayout.WEST, Spring.sum(isCET4C.getConstraint(SpringLayout.EAST), Spring.constant(3)));
        isCET6C.setConstraint(SpringLayout.NORTH, isCET4C.getConstraint(SpringLayout.NORTH));
        isCET6C.setConstraint(SpringLayout.WEST, Spring.sum(separatorC.getConstraint(SpringLayout.EAST), Spring.constant(3)));
        buttonContinueC.setConstraint(SpringLayout.NORTH, Spring.sum(isCET4C.getConstraint(SpringLayout.NORTH), Spring.constant(-6)));
        buttonContinueC.setConstraint(SpringLayout.EAST, Spring.sum(textFieldNameC.getConstraint(SpringLayout.EAST), Spring.constant(15)));

        addWordLayout.putConstraint(SpringLayout.VERTICAL_CENTER, nameExclamation, 0, SpringLayout.VERTICAL_CENTER, textFieldName);
        addWordLayout.putConstraint(SpringLayout.WEST, nameExclamation, 5, SpringLayout.EAST, textFieldName);
        addWordLayout.putConstraint(SpringLayout.VERTICAL_CENTER, transExclamation, 0, SpringLayout.VERTICAL_CENTER, textFieldTrans);
        addWordLayout.putConstraint(SpringLayout.WEST, transExclamation, 5, SpringLayout.EAST, textFieldTrans);
    }

    private void initRightPanelWordList()
    {
        int XLayout = 0;
        final int separatorSpacing = 3;
        final int widgetSpacing00 = 5;
        final int widgetSpacing01 = 20;
        final int widgetSpacing02 = 130; //130//120

        rightPanelWordList = new JPanel(null);
        rightPanelWordList.setBackground(GrayType02);
        rightPanelWordList.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, GrayType01));
        rightPanelWordList.setBounds(this.getWidth() / 3, 0, this.getWidth() * 2 / 3, this.getHeight());

        //---Arrange Tool Bar Part---//
        wordListArrangementOrder = new JCheckBox();
        wordListArrangementOrder.setIcon(new ImageIcon(MediaHandler.imageAscending));
        wordListArrangementOrder.setSelectedIcon(new ImageIcon(MediaHandler.imageDescending));
        wordListArrangementOrder.setOpaque(false);
        wordListArrangementOrder.setContentAreaFilled(false);
        wordListArrangementOrder.setHorizontalAlignment(SwingConstants.CENTER);
        wordListArrangementOrder.addActionListener(e -> triggerManager());
        rightPanelWordList.add(wordListArrangementOrder);
        wordListArrangementOrder.setBounds(XLayout += widgetSpacing00, 0, MediaHandler.imageAscending.getWidth() + 12, 30);
        XLayout += wordListArrangementOrder.getWidth();

        rightPanelWordList.add(SeparatorFactory.builder(SeparatorFactory.SHORT))
                .setBounds(XLayout + separatorSpacing, 5, SeparatorFactory.WIDTH, SeparatorFactory.SHORT); XLayout += 1 + separatorSpacing;

        wordListArrangement = new JComboBox<>();
        wordListArrangement.addItem("NAME");
        wordListArrangement.addItem("DATE");
        wordListArrangement.addItem("PROF");
        wordListArrangement.addItem("ORIG");
        //Decoration
        wordListArrangement.setBackground(GrayType02);
        wordListArrangement.setRenderer(new RegularRenderer(consolas01));
        wordListArrangement.setSelectedItem("ORIG");
        wordListArrangement.setUI(new ComboBoxUI());
        wordListArrangement.addActionListener(e -> triggerManager());
        rightPanelWordList.add(wordListArrangement);
        wordListArrangement.setBounds(XLayout += 12, 0, 100, 30);
        XLayout += wordListArrangement.getWidth();

        JLabel wordListFilter = new JLabel(new ImageIcon(MediaHandler.imageFilter));
        rightPanelWordList.add(wordListFilter);
        wordListFilter.setBounds(XLayout += widgetSpacing01, 0, MediaHandler.imageFilter.getWidth() + 12, 30);
        XLayout += wordListFilter.getWidth();

        rightPanelWordList.add(SeparatorFactory.builder(SeparatorFactory.SHORT))
                .setBounds(XLayout + separatorSpacing, 5, SeparatorFactory.WIDTH, SeparatorFactory.SHORT); XLayout += 1 + separatorSpacing;

        wordListSearch = new JTextField();
        wordListSearch.setBorder(null);
        wordListSearch.setFont(consolas01);
        wordListSearch.setBackground(GrayType02);
        wordListSearch.setUI(new TextFieldUI());
        wordListSearch.addActionListener(e -> triggerManager());
        rightPanelWordList.add(wordListSearch);
        wordListSearch.setBounds(XLayout += 12, 0, 100, 30);
        XLayout += wordListSearch.getWidth();

        JButton wordListSearchButton = new JButton(new ImageIcon(MediaHandler.imageSearch));
        wordListSearchButton.setOpaque(false);
        wordListSearchButton.setBorderPainted(false);
        wordListSearchButton.setFocusPainted(false);
        wordListSearchButton.setContentAreaFilled(false);
        wordListSearchButton.setPressedIcon(new ImageIcon(MediaHandler.imageSearchHL));
        wordListSearchButton.addActionListener(e -> triggerManager());
        rightPanelWordList.add(wordListSearchButton);
        wordListSearchButton.setBounds(XLayout, 0, MediaHandler.imageSearch.getWidth() + 12, 30);
        XLayout += wordListSearchButton.getWidth();

        wordListOnlyCET4 = new JCheckBox(new ImageIcon(MediaHandler.imageCET4));
        wordListOnlyCET4.setSelectedIcon(new ImageIcon(MediaHandler.imageCET4On));
        wordListOnlyCET4.setContentAreaFilled(false);
        wordListOnlyCET4.addActionListener(e -> triggerManager());
        rightPanelWordList.add(wordListOnlyCET4);
        wordListOnlyCET4.setBounds(XLayout += widgetSpacing02, 0, MediaHandler.imageCET4.getWidth() + 8, 30);
        XLayout += wordListOnlyCET4.getWidth();

        rightPanelWordList.add(SeparatorFactory.builder(SeparatorFactory.SHORT))
                .setBounds(XLayout + separatorSpacing, 5, SeparatorFactory.WIDTH, SeparatorFactory.SHORT); XLayout += 1 + separatorSpacing;

        wordListOnlyCET6 = new JCheckBox(new ImageIcon(MediaHandler.imageCET6));
        wordListOnlyCET6.setSelectedIcon(new ImageIcon(MediaHandler.imageCET6On));
        wordListOnlyCET6.setContentAreaFilled(false);
        wordListOnlyCET6.addActionListener(e -> triggerManager());
        rightPanelWordList.add(wordListOnlyCET6);
        wordListOnlyCET6.setBounds(XLayout + 5, 0, MediaHandler.imageCET6.getWidth() + 8, 30);

        //---ScrollPane Part---//
        JScrollPane wordListScroll = new JScrollPane(this.worldListTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        wordListScroll.setBorder(BorderFactory.createLineBorder(GrayType01));
        wordListScroll.getVerticalScrollBar().setUI(new ScrollBarUI());
        rightPanelWordList.add(wordListScroll);
        wordListScroll.setBounds(0, 30, rightPanel.getWidth() - 5, rightPanel.getHeight() - 58); //5,58//16,67
        wordListScroll.getViewport().setBackground(GrayType03);
    }

    private void initRightPanelDailyTest()
    {
        SpringLayout dailyTestLayout = new SpringLayout();
        rightPanelDailyTest = new JPanel(dailyTestLayout);
        rightPanelDailyTest.setBackground(GrayType03);
        rightPanelDailyTest.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, GrayType01));
        rightPanelDailyTest.setBounds(this.getWidth() / 3, 0, this.getWidth() * 2 / 3, this.getHeight());

        JLabel label_1 = new JLabel(new ImageIcon(MediaHandler.imageLabel1)),
                label_2 = new JLabel(new ImageIcon(MediaHandler.imageLabel2)),
                label_3 = new JLabel(new ImageIcon(MediaHandler.imageLabel3)),
                label_4 = new JLabel(new ImageIcon(MediaHandler.imageLabel4));
        JLabel dailyTestTitle = new JLabel("Start Test");
        JLabel dailyTestDescribe = new JLabel("if you are ready");
        sliderQuantity = new JSlider(SwingConstants.HORIZONTAL, 1, 4, 1);
        dailyTestRange = new JComboBox<>();
        JButton dailyTestContinue = new JButton(new ImageIcon(MediaHandler.imageContinue));

        dailyTestTitle.setFont(new Font("Consolas", Font.BOLD, 40));
        dailyTestDescribe.setFont(new Font("Consolas", Font.PLAIN, 18));
        sliderQuantity.setFont(consolas02);
        sliderQuantity.setBackground(GrayType03);
        sliderQuantity.setPreferredSize(new Dimension(168, 30));
        sliderQuantity.setPaintTicks(true);
        sliderQuantity.setUI(new SliderUI(sliderQuantity, false));
        sliderQuantity.addChangeListener(e ->
        {
            label_1.setVisible(false); label_2.setVisible(false); label_3.setVisible(false); label_4.setVisible(false);

            switch (sliderQuantity.getValue())
            {
                case 1: label_1.setVisible(true); break;
                case 2: label_2.setVisible(true); break;
                case 3: label_3.setVisible(true); break;
                case 4: label_4.setVisible(true); break;
            }
        });
        label_2.setVisible(false); label_3.setVisible(false); label_4.setVisible(false);

        dailyTestRange.addItem("Read Current");
        dailyTestRange.addItem("Read Full");
        dailyTestRange.setBackground(GrayType03);
        dailyTestRange.setPreferredSize(new Dimension(120, 30));
        dailyTestRange.setRenderer(new RegularRenderer(consolas01));
        dailyTestRange.setSelectedItem("Read Current");
        dailyTestRange.setUI(new ComboBoxUI());

        dailyTestContinue.setPressedIcon(new ImageIcon(MediaHandler.imageContinueHL));
        dailyTestContinue.setBorderPainted(false);
        dailyTestContinue.setContentAreaFilled(false);
        dailyTestContinue.setFocusPainted(false);
        dailyTestContinue.addActionListener(e -> dailyTestManager());

        rightPanelDailyTest.add(dailyTestTitle);
        rightPanelDailyTest.add(dailyTestDescribe);
        rightPanelDailyTest.add(sliderQuantity);
        rightPanelDailyTest.add(label_1);
        rightPanelDailyTest.add(label_2);
        rightPanelDailyTest.add(label_3);
        rightPanelDailyTest.add(label_4);
        rightPanelDailyTest.add(dailyTestRange);
        rightPanelDailyTest.add(dailyTestContinue);

        SpringLayout.Constraints dailyTestTitleC = dailyTestLayout.getConstraints(dailyTestTitle);
        SpringLayout.Constraints dailyTestDescribeC = dailyTestLayout.getConstraints(dailyTestDescribe);
        SpringLayout.Constraints sliderQuantityC = dailyTestLayout.getConstraints(sliderQuantity);
        SpringLayout.Constraints label_1C = dailyTestLayout.getConstraints(label_1);
        SpringLayout.Constraints label_2C = dailyTestLayout.getConstraints(label_2);
        SpringLayout.Constraints label_3C = dailyTestLayout.getConstraints(label_3);
        SpringLayout.Constraints label_4C = dailyTestLayout.getConstraints(label_4);
        SpringLayout.Constraints dailyTestRangeC = dailyTestLayout.getConstraints(dailyTestRange);
        SpringLayout.Constraints dailyTestContinueC = dailyTestLayout.getConstraints(dailyTestContinue);

        dailyTestTitleC.setConstraint(SpringLayout.NORTH, Spring.constant(rightPanelDailyTest.getHeight() / 3));
        dailyTestTitleC.setConstraint(SpringLayout.WEST, Spring.constant(rightPanelDailyTest.getWidth() / 8));
        dailyTestDescribeC.setConstraint(SpringLayout.NORTH, Spring.sum(dailyTestTitleC.getConstraint(SpringLayout.SOUTH), Spring.constant(5)));
        dailyTestDescribeC.setConstraint(SpringLayout.WEST, dailyTestTitleC.getConstraint(SpringLayout.WEST));
        sliderQuantityC.setConstraint(SpringLayout.NORTH, Spring.sum(dailyTestTitleC.getConstraint(SpringLayout.NORTH), Spring.constant(10)));
        sliderQuantityC.setConstraint(SpringLayout.EAST, Spring.constant(rightPanelDailyTest.getWidth() / 8 * 7));
        label_1C.setConstraint(SpringLayout.SOUTH, Spring.sum(sliderQuantityC.getConstraint(SpringLayout.NORTH), Spring.constant(-5)));
        label_1C.setConstraint(SpringLayout.WEST, Spring.sum(sliderQuantityC.getConstraint(SpringLayout.WEST), Spring.constant(-8)));
        label_2C.setConstraint(SpringLayout.SOUTH, label_1C.getConstraint(SpringLayout.SOUTH));
        label_2C.setConstraint(SpringLayout.WEST, Spring.sum(label_1C.getConstraint(SpringLayout.EAST), Spring.constant(24)));
        label_3C.setConstraint(SpringLayout.SOUTH, label_1C.getConstraint(SpringLayout.SOUTH));
        label_3C.setConstraint(SpringLayout.WEST, Spring.sum(label_2C.getConstraint(SpringLayout.EAST), Spring.constant(24)));
        label_4C.setConstraint(SpringLayout.SOUTH, label_1C.getConstraint(SpringLayout.SOUTH));
        label_4C.setConstraint(SpringLayout.WEST, Spring.sum(label_3C.getConstraint(SpringLayout.EAST), Spring.constant(24)));
        dailyTestRangeC.setConstraint(SpringLayout.NORTH, Spring.sum(sliderQuantityC.getConstraint(SpringLayout.SOUTH), Spring.constant(10)));
        dailyTestRangeC.setConstraint(SpringLayout.WEST, Spring.sum(sliderQuantityC.getConstraint(SpringLayout.WEST), Spring.constant(-5)));
        dailyTestContinueC.setConstraint(SpringLayout.NORTH, dailyTestRangeC.getConstraint(SpringLayout.NORTH));
        dailyTestContinueC.setConstraint(SpringLayout.WEST, Spring.sum(dailyTestRangeC.getConstraint(SpringLayout.EAST), Spring.constant(10)));
    }

    private void initRightPanelQuestion()
    {
        SpringLayout choiceLayout = new SpringLayout();
        rightPanelChoice = new JPanel(choiceLayout);
        rightPanelChoice.setBackground(GrayType03);
        rightPanelChoice.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, GrayType01));
        rightPanelChoice.setBounds(this.getWidth() / 3, 0, this.getWidth() * 2 / 3, this.getHeight());

        choiceTitle = new JLabel();
        choiceA = new JCheckBox(MediaHandler.imageIconCheckBox);
        choiceB = new JCheckBox(MediaHandler.imageIconCheckBox);
        choiceC = new JCheckBox(MediaHandler.imageIconCheckBox);
        JLabel choiceSep00 = SeparatorFactory.builder(SeparatorFactory.SHORT);
        JLabel choiceSep01 = SeparatorFactory.builder(SeparatorFactory.SHORT);
        JLabel choiceSep02 = SeparatorFactory.builder(SeparatorFactory.SHORT);
        JLabel choiceSep03 = SeparatorFactory.builder(SeparatorFactory.SHORT);
        choiceNext = new JButton(new ImageIcon(MediaHandler.imageSearch));

        choiceTitle.setFont(new Font("Microsoft YaHei", Font.PLAIN, 60));
        choiceA.setSelectedIcon(MediaHandler.imageIconCheckBoxOn);
        choiceA.setPreferredSize(new Dimension(119, choiceA.getPreferredSize().height));
        choiceA.setHorizontalAlignment(SwingConstants.CENTER);
        choiceA.setBackground(GrayType03);
        choiceA.setFocusPainted(false);
        choiceA.setFont(yahei01);
        choiceA.addActionListener(e -> onChoiceRadioButtonClicked(0));
        choiceB.setSelectedIcon(MediaHandler.imageIconCheckBoxOn);
        choiceB.setPreferredSize(new Dimension(119, choiceB.getPreferredSize().height));
        choiceB.setHorizontalAlignment(SwingConstants.CENTER);
        choiceB.setBackground(GrayType03);
        choiceB.setFocusPainted(false);
        choiceB.setFont(yahei01);
        choiceB.addActionListener(e -> onChoiceRadioButtonClicked(1));
        choiceC.setSelectedIcon(MediaHandler.imageIconCheckBoxOn);
        choiceC.setPreferredSize(new Dimension(119, choiceC.getPreferredSize().height));
        choiceC.setHorizontalAlignment(SwingConstants.CENTER);
        choiceC.setBackground(GrayType03);
        choiceC.setFocusPainted(false);
        choiceC.setFont(yahei01);
        choiceC.addActionListener(e -> onChoiceRadioButtonClicked(2));
        choiceNext.setPressedIcon(new ImageIcon(MediaHandler.imageSearchHL));
        choiceNext.setBorderPainted(false);
        choiceNext.setFocusPainted(false);
        choiceNext.setContentAreaFilled(false);
        choiceNext.setPreferredSize(new Dimension(MediaHandler.imageSearch.getWidth() + 12, choiceNext.getPreferredSize().height));
        choiceNext.addActionListener(e -> onNextButtonClicked(true));
        choiceNext.setEnabled(false);

        rightPanelChoice.add(choiceTitle);
        rightPanelChoice.add(choiceA);
        rightPanelChoice.add(choiceB);
        rightPanelChoice.add(choiceC);
        rightPanelChoice.add(choiceSep00);
        rightPanelChoice.add(choiceSep01);
        rightPanelChoice.add(choiceSep02);
        rightPanelChoice.add(choiceSep03);
        rightPanelChoice.add(choiceNext);

        choiceLayout.putConstraint(SpringLayout.SOUTH, choiceTitle, -50, SpringLayout.VERTICAL_CENTER, rightPanelChoice);
        choiceLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, choiceTitle, 0, SpringLayout.HORIZONTAL_CENTER, rightPanelChoice);
        choiceLayout.putConstraint(SpringLayout.NORTH, choiceSep01, 50, SpringLayout.VERTICAL_CENTER, rightPanelChoice);
        choiceLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, choiceSep01, -60, SpringLayout.HORIZONTAL_CENTER, rightPanelChoice);
        choiceLayout.putConstraint(SpringLayout.NORTH, choiceSep02, 0, SpringLayout.NORTH, choiceSep01);
        choiceLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, choiceSep02, 60, SpringLayout.HORIZONTAL_CENTER, rightPanelChoice);
        choiceLayout.putConstraint(SpringLayout.NORTH, choiceSep00, 0, SpringLayout.NORTH, choiceSep01);
        choiceLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, choiceSep00, -120, SpringLayout.HORIZONTAL_CENTER, choiceSep01);
        choiceLayout.putConstraint(SpringLayout.NORTH, choiceSep03, 0, SpringLayout.NORTH, choiceSep01);
        choiceLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, choiceSep03, 120, SpringLayout.HORIZONTAL_CENTER, choiceSep02);
        choiceLayout.putConstraint(SpringLayout.NORTH, choiceB, 0, SpringLayout.NORTH, choiceSep01);
        choiceLayout.putConstraint(SpringLayout.WEST, choiceB, 0, SpringLayout.EAST, choiceSep01);
        choiceLayout.putConstraint(SpringLayout.NORTH, choiceA, 0, SpringLayout.NORTH, choiceSep01);
        choiceLayout.putConstraint(SpringLayout.WEST, choiceA, 0, SpringLayout.EAST, choiceSep00);
        choiceLayout.putConstraint(SpringLayout.NORTH, choiceC, 0, SpringLayout.NORTH, choiceSep01);
        choiceLayout.putConstraint(SpringLayout.WEST, choiceC, 0, SpringLayout.EAST, choiceSep02);
        choiceLayout.putConstraint(SpringLayout.NORTH, choiceNext, 0, SpringLayout.NORTH, choiceSep01);
        choiceLayout.putConstraint(SpringLayout.WEST, choiceNext, 120, SpringLayout.EAST, choiceSep02);

        //------------------------------------//

        SpringLayout spellLayout = new SpringLayout();
        rightPanelSpell = new JPanel(spellLayout);
        rightPanelSpell.setBackground(GrayType03);
        rightPanelSpell.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, GrayType01));
        rightPanelSpell.setBounds(this.getWidth() / 3, 0, this.getWidth() * 2 / 3, this.getHeight());

        spellTitle = new JLabel("你好");
        spellTip   = new JLabel("H");
        spellField = new JTextField();
        JLabel spellSep00 = SeparatorFactory.builder(SeparatorFactory.SHORT);
        JLabel spellSep02 = SeparatorFactory.builder(SeparatorFactory.SHORT);
        JLabel spellSep01 = SeparatorFactory.builder(SeparatorFactory.SHORT);
        spellNext = new JButton(new ImageIcon(MediaHandler.imageSearch));

        spellTitle.setFont(new Font("Microsoft YaHei", Font.PLAIN, 60));
        spellTip.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
        spellTip.setForeground(BlackType02);
        spellField.setFont(consolas02);
        spellField.setBorder(null);
        spellField.setBackground(GrayType03);
        spellField.setUI(new TextFieldUI());
        spellField.setHorizontalAlignment(SwingConstants.CENTER);
        spellField.setPreferredSize(new Dimension(160, 30));
        spellField.addActionListener(e -> onSpellFieldTriggered());
        spellNext.setPressedIcon(new ImageIcon(MediaHandler.imageSearchHL));
        spellNext.setBorderPainted(false);
        spellNext.setFocusPainted(false);
        spellNext.setContentAreaFilled(false);
        spellNext.setPreferredSize(new Dimension(MediaHandler.imageSearch.getWidth() + 12, spellNext.getPreferredSize().height));
        spellNext.addActionListener(e -> onNextButtonClicked(false));
        spellNext.setEnabled(false);

        rightPanelSpell.add(spellTitle);
        rightPanelSpell.add(spellTip);
        rightPanelSpell.add(spellField);
        rightPanelSpell.add(spellSep00);
        rightPanelSpell.add(spellSep01);
        rightPanelSpell.add(spellSep02);
        rightPanelSpell.add(spellNext);

        spellLayout.putConstraint(SpringLayout.SOUTH, spellTitle, -50, SpringLayout.VERTICAL_CENTER, rightPanelSpell);
        spellLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, spellTitle, 0, SpringLayout.HORIZONTAL_CENTER, rightPanelSpell);
        spellLayout.putConstraint(SpringLayout.NORTH, spellField, 50, SpringLayout.VERTICAL_CENTER, rightPanelSpell);
        spellLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, spellField, 6, SpringLayout.HORIZONTAL_CENTER, rightPanelSpell);
        spellLayout.putConstraint(SpringLayout.VERTICAL_CENTER, spellSep01, 0, SpringLayout.VERTICAL_CENTER, spellField);
        spellLayout.putConstraint(SpringLayout.EAST, spellSep01, 0, SpringLayout.WEST, spellField);
        spellLayout.putConstraint(SpringLayout.VERTICAL_CENTER, spellTip, 0, SpringLayout.VERTICAL_CENTER, spellSep01);
        spellLayout.putConstraint(SpringLayout.EAST, spellTip, -5, SpringLayout.WEST, spellSep01);
        spellLayout.putConstraint(SpringLayout.VERTICAL_CENTER, spellSep00, 0, SpringLayout.VERTICAL_CENTER, spellTip);
        spellLayout.putConstraint(SpringLayout.EAST, spellSep00, -5, SpringLayout.WEST, spellTip);
        spellLayout.putConstraint(SpringLayout.VERTICAL_CENTER, spellSep02, 0, SpringLayout.VERTICAL_CENTER, spellField);
        spellLayout.putConstraint(SpringLayout.WEST, spellSep02, 0, SpringLayout.EAST, spellField);
        spellLayout.putConstraint(SpringLayout.VERTICAL_CENTER, spellNext, 0, SpringLayout.VERTICAL_CENTER, spellField);
        spellLayout.putConstraint(SpringLayout.WEST, spellNext, 0, SpringLayout.EAST, spellSep02);

        //---------------------------//

        SpringLayout summaryLayout = new SpringLayout();
        rightPanelSummary = new JPanel(summaryLayout);
        rightPanelSummary.setBackground(GrayType03);
        rightPanelSummary.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, GrayType01));
        rightPanelSummary.setBounds(this.getWidth() / 3, 0, this.getWidth() * 2 / 3, this.getHeight());

        JLabel summaryTitle = new JLabel("Done!");
        JLabel summarySub01 = new JLabel("Correct/Total");
        JLabel summarySub02 = new JLabel("Accuracy");
        JLabel summarySlash = new JLabel("/");
        JLabel summarySep00 = SeparatorFactory.builder(80);
        summaryCorrectCount = new JLabel("14");
        summaryAllCount     = new JLabel("15");
        summaryAccuracy     = new JLabel("93%");
        JButton summaryContinue = new JButton(new ImageIcon(MediaHandler.imageContinue));
        Font summaryFont = new Font("Consolas", Font.BOLD, 60);

        summaryTitle.setFont(new Font("Consolas", Font.PLAIN, 40));
        summarySub01.setFont(consolas02);
        summarySub02.setFont(consolas02);
        summarySub01.setForeground(GrayType00);
        summarySub02.setForeground(GrayType00);
        summarySlash.setFont(new Font("Consolas", Font.PLAIN, 50));
        summarySlash.setForeground(BlackType02);
        summaryCorrectCount.setFont(summaryFont);
        summaryCorrectCount.setForeground(BlackType02);
        summaryAllCount.setFont(summaryFont);
        summaryAllCount.setForeground(BlackType02);
        summaryAccuracy.setFont(summaryFont);
        summaryAccuracy.setForeground(BlackType02);
        summaryContinue.setBorderPainted(false);
        summaryContinue.setFocusPainted(false);
        summaryContinue.setContentAreaFilled(false);
        summaryContinue.setPressedIcon(new ImageIcon(MediaHandler.imageContinueHL));
        summaryContinue.addActionListener(e -> rightLayout.show(rightPanel, "dailyTest"));

        rightPanelSummary.add(summaryTitle);
        rightPanelSummary.add(summarySub01);
        rightPanelSummary.add(summarySub02);
        rightPanelSummary.add(summarySlash);
        rightPanelSummary.add(summarySep00);
        rightPanelSummary.add(summaryCorrectCount);
        rightPanelSummary.add(summaryAllCount);
        rightPanelSummary.add(summaryAccuracy);
        rightPanelSummary.add(summaryContinue);

        summaryLayout.putConstraint(SpringLayout.SOUTH, summaryTitle, -100, SpringLayout.VERTICAL_CENTER, rightPanelSummary);
        summaryLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, summaryTitle, 0, SpringLayout.HORIZONTAL_CENTER, rightPanelSummary);

        summaryLayout.putConstraint(SpringLayout.VERTICAL_CENTER, summarySub01, -40, SpringLayout.VERTICAL_CENTER, rightPanelSummary);
        summaryLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, summarySub01, -rightPanelSummary.getWidth() / 6, SpringLayout.HORIZONTAL_CENTER, rightPanelSummary);
        summaryLayout.putConstraint(SpringLayout.VERTICAL_CENTER, summarySub02, -40, SpringLayout.VERTICAL_CENTER, rightPanelSummary);
        summaryLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, summarySub02, rightPanelSummary.getWidth() / 6, SpringLayout.HORIZONTAL_CENTER, rightPanelSummary);

        summaryLayout.putConstraint(SpringLayout.NORTH, summarySlash, 0, SpringLayout.SOUTH, summarySub01);
        summaryLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, summarySlash, 0, SpringLayout.HORIZONTAL_CENTER, summarySub01);

        summaryLayout.putConstraint(SpringLayout.VERTICAL_CENTER, summarySep00, -15, SpringLayout.VERTICAL_CENTER, rightPanelSummary);
        summaryLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, summarySep00, 0, SpringLayout.HORIZONTAL_CENTER, rightPanelSummary);
        summaryLayout.putConstraint(SpringLayout.NORTH, summaryAllCount, -5, SpringLayout.SOUTH, summarySub01);
        summaryLayout.putConstraint(SpringLayout.WEST, summaryAllCount, -5, SpringLayout.EAST, summarySlash);
        summaryLayout.putConstraint(SpringLayout.NORTH, summaryCorrectCount, -5, SpringLayout.SOUTH, summarySub01);
        summaryLayout.putConstraint(SpringLayout.EAST, summaryCorrectCount, 5, SpringLayout.WEST, summarySlash);
        summaryLayout.putConstraint(SpringLayout.NORTH, summaryAccuracy, -5, SpringLayout.SOUTH, summarySub02);
        summaryLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, summaryAccuracy, 0, SpringLayout.HORIZONTAL_CENTER, summarySub02);
        summaryLayout.putConstraint(SpringLayout.NORTH, summaryContinue, 80, SpringLayout.VERTICAL_CENTER, rightPanelSummary);
        summaryLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, summaryContinue, 0, SpringLayout.HORIZONTAL_CENTER, rightPanelSummary);
    }

    //---------------METHOD--------------//
    //---------------METHOD--------------//
    //---------------METHOD--------------//
    //---------------METHOD--------------//

    private void triggerManager()
    {
        wordListTemp.clear();
        filterWordListToTemp(); //apply search bar
        arrangeTempListBySelection(); //apply comboBox & checkBox
        applyListToModel(wordListTemp);
    }

    private void arrangeTempListBySelection() //just arrange temp list
    {
        String arrangeStr = (String) wordListArrangement.getSelectedItem();
        boolean isAscending = !wordListArrangementOrder.isSelected();

        if (arrangeStr == null)
            return;

        switch (arrangeStr)
        {
            case "NAME":
            {
                arrangeTempList(1, isAscending);
                break;
            }
            case "DATE":
            {
                arrangeTempList(2, isAscending);
                break;
            }
            case "PROF":
            {
                arrangeTempList(3, isAscending);
                break;
            }
            case "ORIG":
            {
                if (!isAscending)
                {
                    for (int i = 0; (i != wordListTemp.size() - 1 - i && i < wordListTemp.size() - 1 - i); i++)
                        Collections.swap(wordListTemp, i, wordListTemp.size() - 1 - i);
                }
                break;
            }
            default:
        }
    }

    private void arrangeTempList(int arrangeMode, boolean isAscending) //just arrange temp list
    {
        for (int i = 0; i < wordListTemp.size() - 1; i++)
        {
            for (int j = i + 1; j < wordListTemp.size(); j++)
            {
                int result;
                switch (arrangeMode)
                {
                    case 1:
                    {
                        result = wordListTemp.get(i).name.compareToIgnoreCase(wordListTemp.get(j).name);
                        if (result == 0) result = Float.compare(wordListTemp.get(i).score, wordListTemp.get(j).score); //normally this won't happen
                    }break;
                    case 2:
                    {
                        result = wordListTemp.get(i).registrationTime.compareTo(wordListTemp.get(j).registrationTime);
                        if (result == 0) result = wordListTemp.get(i).name.compareToIgnoreCase(wordListTemp.get(j).name);
                    }break;
                    case 3:
                    {
                        result = Float.compare(wordListTemp.get(i).score, wordListTemp.get(j).score);
                        if (result == 0) result = wordListTemp.get(i).name.compareToIgnoreCase(wordListTemp.get(j).name);
                    }break;
                    default: result = 0;
                }

                if (result > 0 && isAscending)
                    Collections.swap(wordListTemp, i, j);
                else if (result < 0 && !isAscending)
                    Collections.swap(wordListTemp, i, j);
            }
        }
    }

    private void filterWordListToTemp()
    {
        String target = wordListSearch.getText();
        boolean isSearchBarEmpty = target == null || target.equals("");

        if (!isSearchBarEmpty || wordListOnlyCET4.isSelected() || wordListOnlyCET6.isSelected()) //if search bar contains sth or checkBox is on
        {
            for (Word temp : wordList) //loop through wordList
            {
                boolean allowToKeep = false;

                //Check Search Bar
                if (!isSearchBarEmpty)
                {
                    if (target.matches("^\\d.*$")) //search by date
                    {
                        String tarYear, tarMonth, tarDay, tempYear, tempMonth, tempDay;
                        Pattern pattern = Pattern.compile("^(\\d+)[\\D]*?(\\d+)?[\\D]*?(\\d+)?$");
                        Matcher tarMatcher = pattern.matcher(target), tempMatcher = pattern.matcher(temp.registrationTime);
                        if (tarMatcher.find() && tempMatcher.find())
                        {
                            tarYear = tarMatcher.group(1);
                            tarMonth = tarMatcher.group(2) != null ? tarMatcher.group(2) : "";
                            tarDay = tarMatcher.group(3) != null ? tarMatcher.group(3) : "";
                            tempYear = tempMatcher.group(1);
                            tempMonth = tempMatcher.group(2) != null ? tempMatcher.group(2) : "";
                            tempDay = tempMatcher.group(3) != null ? tempMatcher.group(3) : "";

                            if (!tarYear.equals("") && !tarMonth.equals("") && !tarDay.equals("")) //Full Formation
                            {
                                if (tarYear.equals(tempYear) && tarMonth.equals(tempMonth) && tarDay.equals(tempDay))
                                    allowToKeep = true;
                            }
                            else if (!tarYear.equals("") && !tarMonth.equals("")) //Year-Month Formation
                            {
                                if (tarYear.equals(tempYear) && tarMonth.equals(tempMonth))
                                    allowToKeep = true;
                                else if (tarYear.equals(tempYear)) //Month String Contains Day Info
                                {
                                    if (tarMonth.length() > 2)
                                    {
                                        if (tarMonth.substring(0, 1).equals(tempMonth) && tarMonth.substring(1).equals(tempDay))
                                            allowToKeep = true;
                                        else if (tarMonth.substring(0, 2).equals(tempMonth) && tarMonth.substring(2).equals(tempDay))
                                            allowToKeep = true;
                                    }
                                }
                            }
                            else if (!tarYear.equals("")) //Year Formation / Single String Formation
                            {
                                if (tarYear.length() <= tempYear.length()) //Year Formation
                                {
                                    if (tarYear.equals(tempYear))
                                        allowToKeep = true;
                                }
                                else // String Formation
                                {
                                    String tempDate = tempYear + tempMonth + tempDay;
                                    if (tarYear.length() <= tempDate.length() && tempDate.substring(0, tarYear.length()).equals(tarYear))
                                        allowToKeep = true;
                                }
                            }
                        }
                    }
                    else //search by name
                    {
                        if (temp.name.length() > target.length()) //if this word is long enough
                        {
                            for (int i = 0; i < temp.name.length(); i++) //loop through this word by checking substring
                            {
                                if (temp.name.substring(i).length() < target.length()) //if substring isn't long enough, end looping
                                    break;
                                String tempSubString = temp.name.substring(i, i + target.length());
                                if (tempSubString.equals(target))
                                {
                                    allowToKeep = true;
                                    break;
                                }
                            }
                        }
                        else if (temp.name.equals(target))
                            allowToKeep = true;
                    }
                }
                else
                    allowToKeep = true;

                //Check CET
                if ((wordListOnlyCET4.isSelected() && !temp.isCET4) || (wordListOnlyCET6.isSelected() && !temp.isCET6))
                    allowToKeep = false;

                if (allowToKeep)
                    wordListTemp.add(temp);
            }
        }
        else
            wordListTemp.addAll(wordList);
    }

    private void applyListToModel(List<Word> list)
    {
        int oldRowCount = wordListModel.getRowCount();
        for (int i = 0; i < oldRowCount; i++)
            wordListModel.removeRow(0);

        for (Word temp : list)
        {
            Vector<Object> tempVec = new Vector<>();
            tempVec.add(temp.name);
            tempVec.add(temp.translatedName);
            tempVec.add(temp.registrationTime);
            tempVec.add(temp.proficiency);
            tempVec.add(temp.isCET4);
            tempVec.add(temp.isCET6);
            wordListModel.addRow(tempVec);
        }
    }

    //image animation handler//
    private void switchAnimationManager(String target) //judge direct → update current page → executeAnimation()
    {
        if (target.equals(panelCurrent))
            return;

        if (isTimerSwitchEnable)
            return;

        page = 0;
        panelTarget = target;

        Iterator<String> iterator = pageList.iterator();
        while (iterator.hasNext())
        {
            String temp = iterator.next();
            if (temp.equals(panelCurrent))
            {
                direct = 0;
                while (iterator.hasNext())
                {
                    if (iterator.next().equals(panelTarget))
                    {
                        direct = 1;
                        break;
                    }
                    else
                        page++;
                }
                break;
            }
            else if (temp.equals(panelTarget))
            {
                direct = 1;
                while (iterator.hasNext())
                {
                    if (iterator.next().equals(panelCurrent))
                    {
                        direct = 0;
                        break;
                    }
                    else
                        page++;
                }
                break;
            }
        }

        //update image
        switch (panelCurrent)
        {
            case "addWord": rightPanel.paintComponents(imageAddWord.getGraphics()); break;
            case "wordList": rightPanel.paintComponents(imageWordList.getGraphics()); break;
            case "dailyTest":
            {
                if (!isOtherPanelShowing)
                    rightPanel.paintComponents(imageDailyTest.getGraphics());
                else
                    isOtherPanelShowing = false;
            } break;
        }

        rightPanel.paintComponents(imageTemp.getGraphics());
        rightPanelGlass.image01 = imageTemp;

        if (page == 0)
        {
            switch (panelTarget)
            {
                case "addWord": rightPanelGlass.image02 = imageAddWord; break;
                case "wordList": rightPanelGlass.image02 = imageWordList; break;
                case "dailyTest": rightPanelGlass.image02 = imageDailyTest; break;
            }
        }
        else
            rightPanelGlass.image02 = imageWordList;

        rightPanelGlass.setDirect(direct);
        progress = FRAMES * (page + 1);
        rightPanelGlass.isSwitchActive = true;
        isTimerSwitchEnable = true;
    }

    private void switchMoveAtOnce()
    {
//        double dependent = 1 - Math.pow(1.3, progress - 25); // coordinate settlement by exponent fun.
//        double dependent;
//        if (progress <= FRAMES / 2) // coordinate settlement by cosine fun.
//            dependent = (1 + Math.cos(progress * Math.PI / FRAMES)) / 2;
//        else if (progress >= FRAMES * (page + 0.5))
//            dependent = (1 + Math.cos((progress - FRAMES * page) * Math.PI / FRAMES)) / 2;
//        else
//        {
//            System.out.println("Non-Cos");
//            dependent = (FRAMES - (progress % FRAMES)) / (float) FRAMES;
//        }
        double dependent;
        if (progress >= FRAMES * (page + 1) / 2) // coordinate settlement by acceleration simulator
            dependent = Math.pow(progress - FRAMES * (page + 1), 2) * 2 * (page + 1) / Math.pow(FRAMES * (page + 1), 2); // (2 / FULL^2)(x - FULL)^2
        else
        {
            if (page == 0)
                dependent = 1 - Math.pow(progress, 2) * 2 * (page + 1) / Math.pow(FRAMES * (page + 1), 2); // -(2 / FULL^2)x + 1
            else
                dependent = 1 - Math.pow(progress, 2) * 2 * (page + 1) / Math.pow(FRAMES * (page + 1), 2); // -(2 / FULL^2)x + 1
        }

        int deviate;
        //change image when page > 0
        if (page > 0 && progress == FRAMES * (page + 1) / 2 - 1)
        {
            rightPanelGlass.image01 = imageWordList;
            rightPanelGlass.image02 = panelTarget.equals("addWord") ? imageAddWord : imageDailyTest;
        }

        if (direct == 0 || direct == 1)
        {
            if (progress == 1)
                deviate = rightPanelGlass.getHeight();
            else
                deviate = (int) Math.ceil((dependent * rightPanelGlass.getHeight()));
        }
        else
        {
            if (progress == 1)
                deviate = rightPanelGlass.getWidth();
            else
                deviate = (int) Math.ceil((dependent * rightPanelGlass.getWidth()));
        }
//        System.out.println("Progress: "+progress+", dependent: "+dependent+", deviate: "+deviate);
        switch (direct)
        {
            case 0: rightPanelGlass.deviateY = deviate; break;
            case 1: rightPanelGlass.deviateY = -deviate; break;
            case 2: rightPanelGlass.deviateX = deviate; break;
            case 3: rightPanelGlass.deviateX = -deviate; break;
        }

        rightPanelGlass.repaint();
    }

    private void addWordManager()
    {
        String name = textFieldName.getText().toLowerCase();
        String trans = textFieldTrans.getText();
        int proficiency = sliderProf.getValue();
        boolean isCET4 = checkBoxIsCET4.isSelected();
        boolean isCET6 = checkBoxIsCET6.isSelected();

        nameExclamation.setVisible(false);
        transExclamation.setVisible(false);
        if (name.equals("") || !name.matches("^(\\w+-?)+(\\w+)$"))
        {
            nameExclamation.setVisible(true);
            if (trans == null || trans.equals(""))
                transExclamation.setVisible(true);
            return;
        }
        if (trans == null || trans.equals(""))
        {
            transExclamation.setVisible(true);
            return;
        }

        for (Word temp : wordList)
        {
            if (temp.name.equals(name))
                return;
        }

        Word target = new Word(name.toLowerCase(), trans, proficiency, isCET4, isCET6);
        if (FileHandler.checkFile())
            FileHandler.writeFile(target);
        wordList.add(target);
        triggerManager();

        textFieldName.setText("");
        textFieldTrans.setText("");
        sliderProf.setValue(0);
        checkBoxIsCET4.setSelected(false);
        checkBoxIsCET6.setSelected(false);

        dialogOnAddWordSucceed.dialogTitle.setText("\"" + target.name.substring(0, 1).toUpperCase() + target.name.substring(1) + "\" Added !");
        dialogOnAddWordSucceed.setVisible(true);
    }

    private void dailyTestManager()
    {
        if (FileHandler.isTodayTestDone())
        {
            isUpdateScore = false;
            dialogOnRedoTest.setVisible(true);
            //wait//
            if (!dialogOnRedoTest.result)
                return;
        }
        else
            isUpdateScore = true;

        int type;
        boolean isCurrentList = Objects.requireNonNull(dailyTestRange.getSelectedItem()).toString().equals("Read Current");

        if (isCurrentList)
            type = DataHandler.checkWordCount(wordListTemp, sliderQuantity.getValue());
        else
            type = DataHandler.checkWordCount(wordList, sliderQuantity.getValue());

        if (type == 3)
        {
            dialogOnNotEnoughWords.setVisible(true);
            return;
        }
        else
        {
            FileHandler.setTodayTestDone();
            isTesting = true;
        }

        this.type = type;

        wordListTest.clear();
        wordListTest = DataHandler.generateTestList(isCurrentList ? wordListTemp : wordList, sliderQuantity.getValue(), type);
//        for (Word temp : wordListTest)
//            System.out.println("Name: "+temp.name+", Score: "+temp.score);
        wordIterator = wordListTest.listIterator();

        if (type == 0 || type == 1)
        {
            choiceIsAvailable = true;
            nextWordForChoice();
            rightLayout.show(rightPanel, "choice");
            isOtherPanelShowing = true;
        }
        else
        {
            spellFieldIsAvailable = true;
            nextWordForSpell();
            rightLayout.show(rightPanel, "spell");
            isOtherPanelShowing = true;
        }
    }

    private void nextWordForChoice()
    {
        Word wrong01, wrong02;
        choiceTarget = wordIterator.next();
        while (true)
        {
            int i = Math.abs(DataHandler.rand.nextInt()) % wordListTest.size();
            if (!wordListTest.get(i).name.equals(choiceTarget.name))
            {
                wrong01 = wordListTest.get(i);
                break;
            }
        }
        while (true)
        {
            int i = Math.abs(DataHandler.rand.nextInt()) % wordListTest.size();
            if (!wordListTest.get(i).name.equals(choiceTarget.name) && !wordListTest.get(i).name.equals(wrong01.name))
            {
                wrong02 = wordListTest.get(i);
                break;
            }
        }

        if (wordIterator.previousIndex() < 5 * sliderQuantity.getValue())
        {
            randomKey = Math.abs(DataHandler.rand.nextInt()) % 3;
            switch (randomKey)
            {
                case 0:
                {
                    choiceA.setText(choiceTarget.translatedName);
                    choiceB.setText(wrong01.translatedName);
                    choiceC.setText(wrong02.translatedName);
                } break;
                case 1:
                {
                    choiceA.setText(wrong01.translatedName);
                    choiceB.setText(choiceTarget.translatedName);
                    choiceC.setText(wrong02.translatedName);
                } break;
                case 2:
                {
                    choiceA.setText(wrong01.translatedName);
                    choiceB.setText(wrong02.translatedName);
                    choiceC.setText(choiceTarget.translatedName);
                } break;
            }
            choiceTitle.setText(choiceTarget.name);
        }
        else if (wordIterator.previousIndex() < 10 * sliderQuantity.getValue())
        {
            randomKey = Math.abs(DataHandler.rand.nextInt()) % 3;
            switch (randomKey)
            {
                case 0:
                {
                    choiceA.setText(choiceTarget.name);
                    choiceB.setText(wrong01.name);
                    choiceC.setText(wrong02.name);
                } break;
                case 1:
                {
                    choiceA.setText(wrong01.name);
                    choiceB.setText(choiceTarget.name);
                    choiceC.setText(wrong02.name);
                } break;
                case 2:
                {
                    choiceA.setText(wrong01.name);
                    choiceB.setText(wrong02.name);
                    choiceC.setText(choiceTarget.name);
                } break;
            }
            choiceTitle.setText(choiceTarget.translatedName);
        }
    }

    private void nextWordForSpell()
    {
        spellTarget = wordIterator.next();

        spellTitle.setText(spellTarget.translatedName);
        spellTip.setText(spellTarget.name.substring(0, 1).toUpperCase());
        spellContent = spellTarget.name;
    }

    private void onChoiceRadioButtonClicked(int selectedKey)
    {
        if (!choiceIsAvailable)
            return;

        switch (randomKey) //show answer
        {
            case 0: choiceA.setForeground(GreenType01); break;
            case 1: choiceB.setForeground(GreenType01); break;
            case 2: choiceC.setForeground(GreenType01); break;
        }
        if (selectedKey == randomKey)
        {
            for (int i = 0; i < wordList.size(); i++) // score handler
            {
                if (wordList.get(i).name.equals(choiceTarget.name))
                {
                    if (isUpdateScore)
                    {
                        switch (wordList.get(i).proficiency)
                        {
                            case 0: wordList.get(i).changeScore(5.0F); break;
                            case 1: wordList.get(i).changeScore(3.0F); break;
                            case 2: wordList.get(i).changeScore(1.5F); break;
                        }
                        if (FileHandler.checkFile())
                            FileHandler.overrideListToFile(wordList);
                    }
                    summaryCorrectCountInt++;
                    break;
                }
            }
        }
        else
        {
            switch (selectedKey)
            {
                case 0: choiceA.setForeground(RedType01); break;
                case 1: choiceB.setForeground(RedType01); break;
                case 2: choiceC.setForeground(RedType01); break;
            }
            for (int i = 0; i < wordList.size(); i++) // score handler
            {
                if (wordList.get(i).name.equals(choiceTarget.name))
                {
                    if (isUpdateScore)
                    {
                        switch (wordList.get(i).proficiency)
                        {
                            case 0: wordList.get(i).changeScore(-0.5F); break;
                            case 1: wordList.get(i).changeScore(-1.0F); break;
                            case 2: wordList.get(i).changeScore(-3.5F); break;
                        }
                        if (FileHandler.checkFile())
                            FileHandler.overrideListToFile(wordList);
                    }
                    break;
                }
            }
        }
        choiceIsAvailable = false;
        choiceNext.setEnabled(true);
    }

    private void onSpellFieldTriggered()
    {
        if (!spellFieldIsAvailable)
            return;

        String typedContent = spellField.getText().toLowerCase();
        spellField.setText(spellContent);
        if (typedContent.equals(spellContent))
        {
            spellField.setForeground(GreenType01);
            for (int i = 0; i < wordList.size(); i++) // score handler
            {
                if (wordList.get(i).name.equals(spellTarget.name))
                {
                    if (isUpdateScore)
                    {
                        switch (wordList.get(i).proficiency)
                        {
                            case 2: wordList.get(i).changeScore(5.0F); break;
                            case 3: wordList.get(i).changeScore(3.0F); break;
                            case 4: wordList.get(i).changeScore(1.0F); break;
                        }
                        if (FileHandler.checkFile())
                            FileHandler.overrideListToFile(wordList);
                    }
                    summaryCorrectCountInt++;
                    break;
                }
            }
        }
        else
        {
            spellField.setForeground(RedType01);
            for (int i = 0; i < wordList.size(); i++) // score handler
            {
                if (wordList.get(i).name.equals(choiceTarget.name))
                {
                    if (isUpdateScore)
                    {
                        switch (wordList.get(i).proficiency)
                        {
                            case 2: wordList.get(i).changeScore(-0.5F); break;
                            case 3: wordList.get(i).changeScore(-4.0F); break;
                            case 4: wordList.get(i).changeScore(-8.0F); break;
                        }
                        if (FileHandler.checkFile())
                            FileHandler.overrideListToFile(wordList);
                    }
                    break;
                }
            }
        }
        spellFieldIsAvailable = false;
        spellNext.setEnabled(true);
    }

    private void onNextButtonClicked(boolean isTaskForChoice)
    {
        if (isTaskForChoice)
        {
            choiceIsAvailable = true;
            choiceA.setSelected(false);
            choiceA.setForeground(BlackType01);
            choiceB.setSelected(false);
            choiceB.setForeground(BlackType01);
            choiceC.setSelected(false);
            choiceC.setForeground(BlackType01);
            choiceNext.setEnabled(false);
        }
        else
        {
            spellFieldIsAvailable = true;
            spellField.setForeground(BlackType01);
            spellField.setText("");
            spellNext.setEnabled(false);
        }

        if (wordIterator.hasNext())
        {
            switch (type)
            {
                case 0:
                {
                    if (wordIterator.nextIndex() < 10 * sliderQuantity.getValue())
                        nextWordForChoice();
                    else if (wordIterator.nextIndex() == 10 * sliderQuantity.getValue())
                    {
                        nextWordForSpell();
                        spellFieldIsAvailable = true;
                        rightLayout.show(rightPanel, "spell");
                    }
                    else
                        nextWordForSpell();
                } break;
                case 1: nextWordForChoice(); break;
                case 2: nextWordForSpell();  break;
            }
        }
        else // finish test
        {
            triggerManager();
            isTesting = false;

            float i = summaryCorrectCountInt / (float) wordListTest.size() * 100;
            summaryCorrectCount.setText(String.valueOf(summaryCorrectCountInt));
            summaryAllCount.setText(String.valueOf(wordListTest.size()));
            summaryAccuracy.setText(i == 100 ? "PASS" : String.format("%.1f%%", i));
            summaryCorrectCountInt = 0;
            rightLayout.show(rightPanel, "summary");
        }
    }

    private void onSwitchButtonClicked(String target)
    {
        if (isTesting)
        {
            dialogOnQuitTest.setVisible(true);
            //wait//
            if (!dialogOnQuitTest.result) // cancel quiting
                return;
            isTesting = false;
            if (target.equals("dailyTest"))
            {
                rightLayout.show(rightPanel, "dailyTest");
                return;
            }
        }
        switchAnimationManager(target);
    }

    //keyboard handler//
    private void onDeleteKeyReleased()
    {
        dialogOnDelete.setVisible(true);
        //wait//
        if (dialogOnDelete.result)
        {
            int rowNum = worldListTable.getSelectedRow();
            String deleteName = wordListModel.getValueAt(rowNum, 0).toString();

            //Memory Process
            Iterator<Word> iterator = wordList.iterator();
            while (iterator.hasNext())
            {
                if (iterator.next().name.equals(deleteName))
                {
                    iterator.remove();
                    break;
                }
            }

            //File Process
            if (FileHandler.checkFile())
                FileHandler.overrideListToFile(wordList);

            triggerManager();
        }
    }

    //-------METHOD FOR MAIN CLASS-------//
    //-------METHOD FOR MAIN CLASS-------//
    //-------METHOD FOR MAIN CLASS-------//

    void initPanelImage()
    {
        rightLayout.show(rightPanel, "addWord");
        rightPanel.paintComponents(imageAddWord.getGraphics());
        rightLayout.show(rightPanel, "dailyTest");
        rightPanel.paintComponents(imageDailyTest.getGraphics());
        rightLayout.show(rightPanel, "wordList");
        rightPanel.paintComponents(imageWordList.getGraphics());
        panelCurrent = "wordList";
//        try
//        {
//            ImageIO.write(imageAddWord, "png", new File("./resources/addWord.png"));
//            ImageIO.write(imageWordList, "png", new File("./resources/wordList.png"));
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
    }

    //---------------ASSETS--------------//
    //---------------ASSETS--------------//
    //---------------ASSETS--------------//

    private static class HeaderRenderer extends JLabel implements TableCellRenderer
    {
        HeaderRenderer()
        {
            super();
            this.setPreferredSize(new Dimension(0, 0));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            return this;
        }
    }

    private static class RegularRenderer extends JLabel implements TableCellRenderer, ListCellRenderer<String>
    {
        RegularRenderer(Font font)
        {
            this.setOpaque(true);
            this.setHorizontalAlignment(SwingConstants.CENTER);
            this.setFont(font);
        }

        private void textConfig(Object value)
        {
            String temp = value.toString();
            if (Character.isLetter(temp.charAt(0)))
                temp = temp.substring(0, 1).toUpperCase() + temp.substring(1);
            this.setText(temp);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            textConfig(value);

            if (isSelected)
            {
                this.setBackground(table.getSelectionBackground());
                this.setForeground(table.getSelectionForeground());
            }
            else
            {
                this.setBackground(table.getBackground());
                this.setForeground(table.getForeground());
            }

            return this;
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus)
        {
            textConfig(value);

            this.setPreferredSize(new Dimension(this.getWidth(), 20));

            if (isSelected)
            {
                this.setBackground(GrayType01);
                this.setForeground(list.getSelectionForeground());
            }
            else
            {
                this.setBackground(GrayType02);
                this.setForeground(list.getForeground());
            }

            return this;
        }
    }

    private static class ProficiencyRenderer extends JLabel implements TableCellRenderer
    {
        ProficiencyRenderer()
        {
            this.setIcon(MediaHandler.imageIconE);
            this.setPreferredSize(new Dimension(MediaHandler.imageA.getWidth(), MediaHandler.imageA.getHeight()));
            this.setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            switch ((int) value)
            {
                case 0: this.setIcon(MediaHandler.imageIconE);break;
                case 1: this.setIcon(MediaHandler.imageIconD);break;
                case 2: this.setIcon(MediaHandler.imageIconC);break;
                case 3: this.setIcon(MediaHandler.imageIconB);break;
                case 4: this.setIcon(MediaHandler.imageIconA);break;
                default: this.setIcon(MediaHandler.imageIconE);break;
            }

            if (isSelected)
            {
                this.setOpaque(true);
                this.setBackground(table.getSelectionBackground());
            }
            else
            {
                this.setOpaque(false);
                this.setBackground(table.getBackground());
            }

            return this;
        }
    }

    private static class BooleanRenderer extends JLabel implements TableCellRenderer
    {
        BooleanRenderer()
        {
            this.setPreferredSize(new Dimension(MediaHandler.imageTrue.getWidth(), MediaHandler.imageTrue.getHeight()));
            this.setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            if ((boolean) value)
                this.setIcon(MediaHandler.imageIconTrue);
            else
                this.setIcon(null);

            if (isSelected)
            {
                this.setOpaque(true);
                this.setBackground(table.getSelectionBackground());
            }
            else
            {
                this.setOpaque(false);
                this.setBackground(table.getBackground());
            }

            return this;
        }
    }

    @Override
    protected void processWindowEvent(WindowEvent e)
    {
        if ((e.getID() == WindowEvent.WINDOW_CLOSING) && isTesting)
        {
            dialogOnQuitTest.setVisible(true);
            //wait//
            if (!dialogOnQuitTest.result)
                return;
        }
        else if (e.getID() == WindowEvent.WINDOW_CLOSING)
        {
            timer.cancel();
            System.gc();
        }

        super.processWindowEvent(e);
    }
}