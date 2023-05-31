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
import java.awt.geom.RoundRectangle2D;
import java.util.Locale;

public class Notify extends JDialog {

    private Animator animator;
    private final JFrame jFrame;
    private boolean showing;
    private Thread thread;
    private final int animate = 10;
    private final TypeNotify type;
    private final LocationNotify locationNotify;
    private JLabel lblTitle;
    private JTextArea lblMessage;
    private JLabel lblIcon;
    private JPanel pane;
    private JScrollPane scroll;
    private JProgressBar progressBar;
    private JPanel pane1;
    private final String title;
    private final String message;
    private static Notify notify;
    private TimingTarget timingTarget;

    public static void sendNotify(JFrame jframe, TypeNotify type, LocationNotify locationNotify, String title, String message) {
        if (notify != null) {
            notify.dispose();
        }
        notify = new Notify(jframe, type, locationNotify, title, message);
        notify.showNotification();
    }

    public Notify(JFrame jFrame, TypeNotify type, LocationNotify locationNotify, String title, String message) {
        super(jFrame);
        this.jFrame = jFrame;
        this.type = type;
        this.locationNotify = locationNotify;
        this.message = message;
        this.title = title;
        $$$setupUI$$$();
        initComponents();
        initAnimator2();
    }

    private void initComponents() {
        setContentPane(pane);
        setUndecorated(true);
        setFocusableWindowState(false);
        paint();
        pack();
        loadData();
    }

    private void loadData() {
        if (type == TypeNotify.SUCCESS) {
            lblIcon.setIcon(new ImageIcon(App.class.getResource("Icons/x32/sucess.png")));
        } else if (type == TypeNotify.INFO) {
            lblIcon.setIcon(new ImageIcon(App.class.getResource("Icons/x32/info.png")));
        } else if (type == TypeNotify.ERROR) {
            lblIcon.setIcon(new ImageIcon(App.class.getResource("Icons/x32/error.png")));
        } else {
            lblIcon.setIcon(new ImageIcon(App.class.getResource("Icons/x32/warning.png")));
        }
        lblMessage.setBackground(pane.getBackground());
        lblTitle.setText(title);
        lblMessage.setText(message);
    }

    private void initAnimator2() {
        timingTarget = new TimingTargetAdapter() {
            int x = 0;
            int y = 0;
            int right;
            int top;
            final int margin = 10;

            @Override
            public void begin() {
                if (!showing) {
                    try {
                        setOpacity(0f);
                    } catch (Exception ignored) {
                    }
                    switch (locationNotify) {
                        case TOP_CENTER:
                            x = jFrame.getX() + ((jFrame.getWidth() - getWidth()) / 2);
                            y = jFrame.getY() + margin / 2;
                            break;
                        case TOP_RIGHT:
                            x = jFrame.getX() + jFrame.getWidth() - getWidth() - margin;
                            y = jFrame.getY() + (margin * 3) / 2;
                            break;
                        case TOP_LEFT:
                            x = jFrame.getX() + margin;
                            y = jFrame.getY() + (margin * 3) / 2;
                            break;
                        case BOTTOM_CENTER:
                            x = jFrame.getX() + ((jFrame.getWidth() - getWidth()) / 2);
                            y = jFrame.getY() + jFrame.getHeight() - getHeight() - margin;
                            break;
                        case BOTTOM_RIGHT:
                            x = jFrame.getX() + jFrame.getWidth() - getWidth() - margin;
                            y = jFrame.getY() + jFrame.getHeight() - getHeight() - 2 * margin;
                            break;
                        case BOTTOM_LEFT:
                            x = jFrame.getX() + margin;
                            y = jFrame.getY() + jFrame.getHeight() - getHeight() - 2 * margin;
                            break;
                    }
                    top = y;
                    right = x;
                    setLocation(x, y);
                    setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
                    setVisible(true);
                }
            }

            @Override
            public void end() {
                showing = !showing;
                if (showing) {
                    thread = new Thread(() -> {
                        sleep();
                        closeNotification();
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
                    switch (locationNotify) {
                        case TOP_RIGHT:
                        case BOTTOM_RIGHT:
                            x = (int) ((1f - fraction) * animate);
                            setLocation(right - x, y);
                            break;
                        case TOP_LEFT:
                        case BOTTOM_LEFT:
                            x = (int) ((1f - fraction) * animate);
                            setLocation(right + x, y);
                            break;
                        case BOTTOM_CENTER:
                            y = (int) ((1f - fraction) * animate);
                            setLocation(x, top - y);
                            break;
                        case TOP_CENTER:
                            y = (int) ((1f - fraction) * animate);
                            setLocation(x, top + y);
                            break;
                    }
                } else {
                    alpha = fraction;
                    switch (locationNotify) {
                        case TOP_RIGHT:
                        case BOTTOM_RIGHT:
                            x = (int) (fraction * animate);
                            setLocation(right - x, y);
                            break;
                        case TOP_LEFT:
                        case BOTTOM_LEFT:
                            x = (int) (fraction * animate);
                            setLocation(right + x, y);
                            break;
                        case BOTTOM_CENTER:
                            y = (int) (fraction * animate);
                            setLocation(x, top - y);
                            break;
                        case TOP_CENTER:
                            y = (int) (fraction * animate);
                            setLocation(x, top + y);
                            break;
                    }
                }
                setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
                try {
                    setOpacity(alpha);
                } catch (Exception ignored) {
                }
            }
        };
        animator = new Animator(500, timingTarget);
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
            do {
                progressBar.setValue(progressBar.getValue() - 1);
                thread.sleep(35);
            } while (progressBar.getValue() != 0);
        } catch (InterruptedException ignored) {
        }
    }

    private void paint() {
        if (type == TypeNotify.SUCCESS) {
            pane.setBackground(new Color(3, 176, 15));
            progressBar.setBackground(new Color(3, 176, 15));
        } else if (type == TypeNotify.INFO) {
            pane.setBackground(new Color(44, 135, 204));
            progressBar.setBackground(new Color(44, 135, 204));
        } else if (type == TypeNotify.ERROR) {
            pane.setBackground(new Color(219, 63, 50));
            progressBar.setBackground(new Color(219, 63, 50));
        } else {
            pane.setBackground(new Color(227, 181, 13));
            progressBar.setBackground(new Color(227, 181, 13));
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
        pane = new JPanel();
        pane.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), 0, 0));
        pane.setMaximumSize(new Dimension(400, 90));
        pane.setMinimumSize(new Dimension(400, 90));
        pane.setPreferredSize(new Dimension(400, 90));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(8, 8, 0, 8), 10, 0));
        panel1.setOpaque(false);
        pane.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 2, new Insets(5, 0, 8, 0), 15, 0));
        panel2.setOpaque(false);
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lblTitle = new JLabel();
        Font lblTitleFont = this.$$$getFont$$$(null, Font.BOLD, 14, lblTitle.getFont());
        if (lblTitleFont != null) lblTitle.setFont(lblTitleFont);
        lblTitle.setText("Título");
        panel2.add(lblTitle, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scroll = new JScrollPane();
        scroll.setVerticalScrollBarPolicy(21);
        panel2.add(scroll, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        lblMessage = new JTextArea();
        lblMessage.setEditable(false);
        lblMessage.setLineWrap(true);
        lblMessage.setWrapStyleWord(true);
        scroll.setViewportView(lblMessage);
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.setOpaque(false);
        panel1.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lblIcon = new JLabel();
        lblIcon.setIcon(new ImageIcon(getClass().getResource("/com/moreno/Icons/x32/sucess.png")));
        lblIcon.setText("");
        panel3.add(lblIcon, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        progressBar = new JProgressBar();
        progressBar.setBorderPainted(false);
        progressBar.setForeground(new Color(-1));
        progressBar.setValue(100);
        pane.add(progressBar, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 10), null, null, 0, false));
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
        return pane;
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
}
