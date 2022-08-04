package com.moreno;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Locale;

public class Notify extends JDialog {

    private Animator animator;
    private final JFrame fram;
    private boolean showing;
    private Thread thread;
    private int animate = 10;
    private Notify.Type type;
    private Notify.Location location;
    private JButton btnClose;
    private JLabel lblTittle;
    private JTextArea lblMessage;
    private JPanel contentPane;
    private JLabel lblIcon;
    private JPanel pane;
    private JPanel pane1;
    private String tittle;
    private String message;
    private static Notify notify;

    public static void sendNotify(JFrame jframe, Notify.Type type, Notify.Location location, String tittle, String message) {
        if (notify != null) {
            notify.dispose();
        }
        notify = new Notify(jframe, type, location, tittle, message);
        notify.showNotification();
    }

    public Notify(JFrame fram, Notify.Type type, Notify.Location location, String tittle, String message) {
        super(fram);
        this.fram = fram;
        this.type = type;
        this.location = location;
        this.message = message;
        this.tittle = tittle;
        $$$setupUI$$$();
        initComponents();
        initAnimator();
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeNotification();
            }
        });
    }

    private void initComponents() {
        setContentPane(contentPane);
        paint();
        setUndecorated(true);
        setFocusableWindowState(false);
        pack();
        if (type == Notify.Type.SUCCESS) {
            lblIcon.setIcon(new ImageIcon(App.class.getResource("Icons/x30/sucess.png")));
        } else if (type == Notify.Type.INFO) {
            lblIcon.setIcon(new ImageIcon(App.class.getResource("Icons/x30/info.png")));
        } else {
            lblIcon.setIcon(new ImageIcon(App.class.getResource("Icons/x30/warning.png")));
        }
        lblTittle.setText(tittle);
        lblMessage.setText(message);
    }

    private void initAnimator() {
        TimingTarget target = new TimingTargetAdapter() {
            private int x = 0;
            private int y = 0;
            private int top;
            private boolean top_to_bot;

            @Override
            public void begin() {
                if (!showing) {
                    int margin = 10;
                    if (location == Notify.Location.TOP_CENTER) {
                        x = fram.getX() + ((fram.getWidth() - getWidth()) / 2);
                        y = fram.getY() + margin;
                        top_to_bot = true;
                    } else if (location == Notify.Location.TOP_RIGHT) {
                        x = fram.getX() + fram.getWidth() - getWidth() - 2 * margin;
                        y = fram.getY() + margin;
                        top_to_bot = true;
                    } else if (location == Notify.Location.TOP_LEFT) {
                        x = fram.getX() + 2 * margin;
                        y = fram.getY() + margin;
                        top_to_bot = true;
                    } else if (location == Notify.Location.BOTTOM_CENTER) {
                        x = fram.getX() + ((fram.getWidth() - getWidth()) / 2);
                        y = fram.getY() + fram.getHeight() - getHeight() - margin;
                        top_to_bot = false;
                    } else if (location == Notify.Location.BOTTOM_RIGHT) {
                        x = fram.getX() + fram.getWidth() - getWidth() - 2 * margin;
                        y = fram.getY() + fram.getHeight() - getHeight() - margin;
                        top_to_bot = false;
                    } else if (location == Notify.Location.BOTTOM_LEFT) {
                        x = fram.getX() + 2 * margin;
                        y = fram.getY() + fram.getHeight() - getHeight() - margin;
                        top_to_bot = false;
                    } else {
                        x = fram.getX() + ((fram.getWidth() - getWidth()) / 2);
                        y = fram.getY() + ((fram.getHeight() - getHeight()) / 2);
                        top_to_bot = true;
                    }
                    top = y;
                    setLocation(x, y);
                    setVisible(true);
                }
            }

            @Override
            public void end() {
                showing = !showing;
                if (showing) {
                    thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sleep();
                            closeNotification();
                        }
                    });
                    thread.start();
                } else {
                    dispose();
                }
            }

            @Override
            public void timingEvent(float fraction) {
                if (showing) {
                    int y = (int) ((1f - fraction) * animate);
                    if (top_to_bot) {
                        setLocation(x, top + y);
                    } else {
                        setLocation(x, top - y);
                    }
                } else {
                    int y = (int) (fraction * animate);
                    if (top_to_bot) {
                        setLocation(x, top + y);
                    } else {
                        setLocation(x, top - y);
                    }
                }
            }
        };
        animator = new Animator(500, target);
        animator.setResolution(5);
    }

    public void showNotification() {
        animator.start();
    }

    public void closeNotification() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        if (animator.isRunning()) {
            if (!showing) {
                animator.stop();
                showing = true;
                animator.start();
            }
        } else {
            showing = true;
            animator.start();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
    }

    private void paint() {
        pane.setBackground(pane.getBackground().brighter());
        if (type == Type.SUCCESS) {
            contentPane.setBackground(new Color(18, 163, 24));
        } else if (type == Type.INFO) {
            contentPane.setBackground(new Color(28, 139, 206));
        } else {
            contentPane.setBackground(new Color(241, 196, 15));
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(1, 1, new Insets(0, 5, 0, 0), 0, 0));
        contentPane.setMaximumSize(new Dimension(400, 65));
        contentPane.setMinimumSize(new Dimension(400, 65));
        contentPane.setPreferredSize(new Dimension(400, 65));
        pane = new JPanel();
        pane.setLayout(new GridLayoutManager(1, 3, new Insets(0, 10, 0, 0), 10, -1));
        contentPane.add(pane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 2, new Insets(5, 0, 10, 0), 15, 0));
        panel1.setOpaque(false);
        pane.add(panel1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lblTittle = new JLabel();
        Font lblTittleFont = this.$$$getFont$$$(null, Font.BOLD, 14, lblTittle.getFont());
        if (lblTittleFont != null) lblTittle.setFont(lblTittleFont);
        lblTittle.setText("Título");
        panel1.add(lblTittle, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        lblMessage.setEditable(false);
        lblMessage.setEnabled(true);
        lblMessage.setLineWrap(true);
        lblMessage.setOpaque(false);
        panel1.add(lblMessage, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(150, 50), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.setOpaque(false);
        pane.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lblIcon = new JLabel();
        lblIcon.setIcon(new ImageIcon(getClass().getResource("/com/moreno/Icons/x30/sucess.png")));
        lblIcon.setText("");
        panel2.add(lblIcon, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.setOpaque(false);
        pane.add(panel3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnClose = new JButton();
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setIcon(new ImageIcon(getClass().getResource("/com/moreno/Icons/x24/cerrar.png")));
        btnClose.setPressedIcon(new ImageIcon(getClass().getResource("/com/moreno/Icons/x24/cerrar3.png")));
        btnClose.setRolloverIcon(new ImageIcon(getClass().getResource("/com/moreno/Icons/x24/cerrar2.png")));
        btnClose.setText("");
        panel3.add(btnClose, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(32, 32), new Dimension(32, 32), new Dimension(32, 32), 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        lblMessage = new JTextArea();
        lblMessage.setBorder(BorderFactory.createEmptyBorder());
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */


    public enum Type {
        SUCCESS, INFO, WARNING
    }

    public enum Location {
        TOP_CENTER, TOP_RIGHT, TOP_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT, BOTTOM_LEFT, CENTER
    }

}

