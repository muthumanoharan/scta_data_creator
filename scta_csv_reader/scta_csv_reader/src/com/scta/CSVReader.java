package com.scta;


public class CSVReader {
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt
            .EventQueue
            .invokeLater(new Runnable() {
                public void run() {
                    new scta_data_creator_input_screen().setVisible(true);
                }
            });
    }
}
