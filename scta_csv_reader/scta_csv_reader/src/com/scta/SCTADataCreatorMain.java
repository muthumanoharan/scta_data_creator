package com.scta;


public class SCTADataCreatorMain {
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt
            .EventQueue
            .invokeLater(new Runnable() {
                public void run() {
                    new SCTADataCreatorInputScreen().setVisible(true);
                }
            });
    }
}
