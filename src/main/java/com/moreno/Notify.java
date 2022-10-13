package com.moreno;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
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
    private int type;
    private int location;
    private JButton btnClose;
    private JLabel lblTittle;
    private JTextArea lblMessage;
    private JPanel contentPane;
    private JLabel lblIcon;
    private JPanel pane;
    private JScrollPane scroll;
    private JPanel pane1;
    private String tittle;
    private String message;
    private static Notify notify;

    public static void sendNotify(JFrame jframe, int type, int location, String tittle, String message) {
        if (notify != null) {
            notify.dispose();
        }
        notify = new Notify(jframe, type, location, tittle, message);
        notify.showNotification();
    }

    public Notify(JFrame fram, int type, int location, String tittle, String message) {
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
        lblMessage.setBackground(pane.getBackground());
        if (type == Type.SUCCESS) {
            lblIcon.setIcon(new ImageIcon(App.class.getResource("Icons/x30/sucess.png")));
        } else if (type == Type.INFO) {
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
                    try {
                        setOpacity(0f);
                    } catch (Exception ignored) {
                    }
                    int margin = 10;
                    if (location == Location.TOP_CENTER) {
                        x = fram.getX() + ((fram.getWidth() - getWidth()) / 2);
                        y = fram.getY() + margin;
                        top_to_bot = true;
                    } else if (location == Location.TOP_RIGHT) {
                        x = fram.getX() + fram.getWidth() - getWidth() - 2 * margin;
                        y = fram.getY() + margin;
                        top_to_bot = true;
                    } else if (location == Location.TOP_LEFT) {
                        x = fram.getX() + 2 * margin;
                        y = fram.getY() + margin;
                        top_to_bot = true;
                    } else if (location == Location.BOTTOM_CENTER) {
                        x = fram.getX() + ((fram.getWidth() - getWidth()) / 2);
                        y = fram.getY() + fram.getHeight() - getHeight() - margin;
                        top_to_bot = false;
                    } else if (location == Location.BOTTOM_RIGHT) {
                        x = fram.getX() + fram.getWidth() - getWidth() - 2 * margin;
                        y = fram.getY() + fram.getHeight() - getHeight() - margin;
                        top_to_bot = false;
                    } else if (location == Location.BOTTOM_LEFT) {
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
                float alpha;
                if (showing) {
                    alpha = 1f - fraction;
                    int y = (int) ((1f - fraction) * animate);
                    if (top_to_bot) {
                        setLocation(x, top + y);
                    } else {
                        setLocation(x, top - y);
                    }
                } else {
                    alpha = fraction;
                    int y = (int) (fraction * animate);
                    if (top_to_bot) {
                        setLocation(x, top + y);
                    } else {
                        setLocation(x, top - y);
                    }
                }
                try {
                    setOpacity(alpha);
                } catch (Exception ignored) {
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
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(1, 1, new Insets(0, 5, 0, 0), 0, 0));
        contentPane.setMaximumSize(new Dimension(400, 72));
        contentPane.setMinimumSize(new Dimension(400, 72));
        contentPane.setPreferredSize(new Dimension(400, 72));
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
        scroll = new JScrollPane();
        panel1.add(scroll, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        lblMessage = new JTextArea();
        lblMessage.setLineWrap(true);
        lblMessage.setWrapStyleWord(true);
        scroll.setViewportView(lblMessage);
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


    public class Type {
        public static final int SUCCESS = 0;
        public static final int INFO = 1;
        public static final int WARNING = 2;
    }

    public class Location {
        public static final int TOP_CENTER = 0;
        public static final int TOP_RIGHT = 1;
        public static final int TOP_LEFT = 2;
        public static final int BOTTOM_CENTER = 3;
        public static final int BOTTOM_RIGHT = 4;
        public static final int BOTTOM_LEFT = 5;
        public static final int CENTER = 6;
    }

}
