package fitbit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * DisplayWindow.java
 * Team Hungry
 * CSCI 360-01
 * Ugly (functional) UI
 */

class DisplayWindow implements ActionListener {

    private int age = 0;
    private int weight = 0;
    private boolean sex = true;
    private boolean mode = true;
    private int subscreen = 0;

    private JPanel displayPanel;
    private JLabel time, date, heartrate, steps, activity, caloriesBurned, editScreen, editToolbar;
    private JButton sideButton;
    private JButton frontButton;
    private JButton editButton;

    //one instance of everything, kept here
    private DataExpert dataExpert = new DataExpert();

    //constant updates
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private DisplayWindow() {
        //create window
        JFrame displayFrame = new JFrame("FITBIT DEMO");
        displayFrame.setDefaultCloseOperation((JFrame.EXIT_ON_CLOSE));
        displayFrame.setLocation(250, 250);
        displayFrame.setSize(300, 400);

        //create and set up panel
        displayPanel = new JPanel();

        //add widgets
        addWidgets();

        //add panel to window
        displayFrame.getContentPane().add(displayPanel, BorderLayout.CENTER);

        //display the window
        displayFrame.setVisible(true);
    }

    private void addWidgets() {
        //labels
        time = new JLabel("Time", SwingConstants.CENTER);
        date = new JLabel("Date", SwingConstants.CENTER);
        heartrate = new JLabel("Heart Rate", SwingConstants.CENTER);
        steps = new JLabel("Steps", SwingConstants.CENTER);
        activity = new JLabel("Activity", SwingConstants.CENTER);
        caloriesBurned = new JLabel("Calories Burned", SwingConstants.CENTER);
        editScreen = new JLabel("Settings Mode", SwingConstants.CENTER);
        editToolbar = new JLabel(" ", SwingConstants.CENTER);

        //fonts
        time.setFont(new Font("Comic Sans MS", Font.PLAIN, 60));
        date.setFont(new Font("Comic Sans MS", Font.PLAIN, 60));
        heartrate.setFont(new Font("Comic Sans MS", Font.PLAIN, 60));
        steps.setFont(new Font("Comic Sans MS", Font.PLAIN, 34));
        activity.setFont(new Font("Comic Sans MS", Font.PLAIN, 34));
        caloriesBurned.setFont(new Font("Comic Sans MS", Font.PLAIN, 34));
        editScreen.setFont(new Font("Comic Sans MS", Font.PLAIN, 40));
        editToolbar.setFont(new Font("Comic Sans MS", Font.PLAIN, 34));

        //buttons
        sideButton = new JButton("Menu Button");
        frontButton = new JButton("Change Button");
        editButton = new JButton("Editmode Button");
        sideButton.setMnemonic(KeyEvent.VK_LEFT);
        sideButton.addActionListener(this);
        frontButton.setMnemonic(KeyEvent.VK_RIGHT);
        frontButton.addActionListener(this);
        editButton.setMnemonic(KeyEvent.VK_V);
        editButton.addActionListener(this);

        //pop it all to the container
        displayPanel.add(sideButton);
        displayPanel.add(frontButton);
        displayPanel.add(editButton);
        displayPanel.add(time);
        displayPanel.add(date);
        displayPanel.add(heartrate);
        displayPanel.add(steps);
        displayPanel.add(activity);
        displayPanel.add(caloriesBurned);
        displayPanel.add(editScreen);
        displayPanel.add(editToolbar);

        //hide the things that should be hidden
        date.setVisible(false);
        heartrate.setVisible(false);
        activity.setVisible(false);
        caloriesBurned.setVisible(false);
        editScreen.setVisible(false);
        editToolbar.setVisible(false);
        frontButton.setVisible(false);

        //start the updates
        this.update();
    }

    public void actionPerformed(ActionEvent event) {
        //change menu
        if (event.getSource() == editButton){
            if(mode){
                time.setVisible(false);
                date.setVisible(false);
                heartrate.setVisible(false);
                steps.setVisible(false);
                activity.setVisible(false);
                caloriesBurned.setVisible(false);
                editScreen.setVisible(true);
                editToolbar.setVisible(true);
                frontButton.setVisible(true);
            }else{
                time.setVisible(true);
                steps.setVisible(true);
                frontButton.setVisible(false);
                editScreen.setVisible(false);
                editToolbar.setVisible(false);
            }
            mode = !mode;
        }

        //main menu
        if (mode) {
            if (event.getSource() == sideButton) {
                System.out.println("SIDE BUTTON PRESSED");

                //hides everything you shouldn't see, dynamically
                if (time.isVisible()) {
                    time.setVisible(!time.isVisible());
                    steps.setVisible(!steps.isVisible());
                    date.setVisible(!date.isVisible());
                    activity.setVisible(!activity.isVisible());
                } else if (date.isVisible()) {
                    date.setVisible(!date.isVisible());
                    activity.setVisible(!activity.isVisible());
                    heartrate.setVisible(!heartrate.isVisible());
                    caloriesBurned.setVisible(!caloriesBurned.isVisible());
                } else if (heartrate.isVisible()) {
                    heartrate.setVisible(!heartrate.isVisible());
                    caloriesBurned.setVisible(!caloriesBurned.isVisible());
                    time.setVisible(!time.isVisible());
                    steps.setVisible(!steps.isVisible());
                }
            } else if (event.getSource() == frontButton) {
                System.out.println("FRONT BUTTON PRESSED");
            } else if (event.getSource() == editButton) {
                System.out.println("EDIT BUTTON PRESSED");
            }

        //edit menu
        }else{
            if (event.getSource() == sideButton){
                System.out.println("EDIT SIDE BUTTON PRESSED");
                subscreen = (subscreen + 1) % 4;
                System.out.println(subscreen);
                if(subscreen == 0){
                    editScreen.setText(" Settings Mode ");
                }else if(subscreen == 1){
                    editScreen.setText("  Enter Sex:    ");
                }else if(subscreen == 2){
                    editScreen.setText("  Enter Weight: ");
                }else if(subscreen == 3){
                    editScreen.setText("  Enter Age:    ");
                }
            } else if (event.getSource() == frontButton){
                System.out.println(subscreen);
                System.out.println("EDIT FRONT BUTTON PRESSED");
                if(subscreen == 1){
                    System.out.println("CHANGE SEX");
                    sex = !sex;
                }else if(subscreen == 2){
                    System.out.println("CHANGE WEIGHT");
                    weight = ((weight + 10) % 210);
                }else if(subscreen == 3){
                    System.out.println("CHANGE AGE");
                    age = (age + 5) % 75;
                }
            }
        }
    }

    //updates
    private void update(){
        final Runnable updoot = new Runnable() {public void run() { updateEverything(); }};
        final ScheduledFuture<?> updootHandle = scheduler.scheduleAtFixedRate(updoot, 100, 100, TimeUnit.MILLISECONDS);
    }

    //get data
    private void updateEverything(){
        updootSettings();
        updootData();
        updootTime();
    }

    //updoot time
    private void updootTime(){
        time.setText(dataExpert.getTime());
        date.setText(dataExpert.getDate());
        heartrate.setText("BPM: " + dataExpert.getHeartrate());
        steps.setText("Steps: " + dataExpert.getSteps());
        activity.setText("Goal %: " + String.format("%.2f", Double.parseDouble(dataExpert.getActivity())));
        caloriesBurned.setText("Calories: " + String.format("%.2f", Double.parseDouble(dataExpert.getCalories())));
    }

    //updoot settings
    private void updootSettings(){
        if(subscreen == 0){
            editToolbar.setText(" ");
        }else if(subscreen == 1){
            if(sex) editToolbar.setText("M");
            else editToolbar.setText("F");
        }else if(subscreen == 2){
            editToolbar.setText("" +(weight+100));
        }else if(subscreen == 3){
            editToolbar.setText("" +(age+5));
        }
    }

    //updoot data
    private void updootData(){
        dataExpert.setUserData(age+5, weight+100, sex);
    }


    //create and display window
    private static void showGUI() {
        //JFrame.setDefaultLookAndFeelDecorated(true);
        DisplayWindow window = new DisplayWindow();
    }
    public static void runUI() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {public void run() { showGUI(); }});
    }
}