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
import java.util.Objects;

public class Notify extends JDialog {
    private Animator animator;
    private boolean showing;
    private Thread thread;
    private JLabel lblTitle;
    private JTextArea lblMessage;
    private JLabel lblIcon;
    private JPanel pane;
    private JScrollPane scroll;
    private JProgressBar progressBar;
    private JButton btnExit;
    private static Notify notify;
    private final Notification notification;
    public static JFrame jFrame;
    public static ChangeListener changeListener;

    public static void sendNotify(Notification notification) {
        if (jFrame != null) {
            if (notify != null) {
                notify.dispose();
            }
            notify = new Notify(notification);
            if (changeListener != null) {
                changeListener.onAdd(notification);
            }
            notify.showNotification();
        }
    }

    public Notify(Notification notification) {
        super(jFrame);
        this.notification = notification;
        this.$$$setupUI$$$();
        this.initComponents();
        this.initAnimator();
        this.loadData();
        btnExit.addActionListener(e -> {
            if (changeListener != null) {
                changeListener.onClose(notification);
            }
            closeNotification();
        });
    }

    private void initComponents() {
        this.setContentPane(this.pane);
        this.setUndecorated(true);
        this.setFocusableWindowState(false);
        this.pack();
    }

    public void loadData() {
        switch (this.notification.getType()) {
            case SUCCESS -> {
                this.lblIcon.setIcon(new ImageIcon(Objects.requireNonNull(App.class.getResource("Icons/x32/sucess.png"))));
                this.pane.setBackground(new Color(3, 176, 15));
                this.progressBar.setBackground(new Color(3, 176, 15));
            }
            case INFO -> {
                this.lblIcon.setIcon(new ImageIcon(Objects.requireNonNull(App.class.getResource("Icons/x32/info.png"))));
                this.pane.setBackground(new Color(44, 135, 204));
                this.progressBar.setBackground(new Color(44, 135, 204));
            }
            case ERROR -> {
                this.lblIcon.setIcon(new ImageIcon(Objects.requireNonNull(App.class.getResource("Icons/x32/error.png"))));
                this.pane.setBackground(new Color(219, 63, 50));
                this.progressBar.setBackground(new Color(219, 63, 50));
            }
            case WARNING -> {
                this.lblIcon.setIcon(new ImageIcon(Objects.requireNonNull(App.class.getResource("Icons/x32/warning.png"))));
                this.pane.setBackground(new Color(227, 181, 13));
                this.progressBar.setBackground(new Color(227, 181, 13));
            }
        }

        this.lblMessage.setBackground(this.pane.getBackground());
        this.lblTitle.setText(this.notification.getTitle());
        this.lblMessage.setText(this.notification.getMessage());
    }

    private void initAnimator() {
        TimingTarget timingTarget = new TimingTargetAdapter() {
            int x = 0;
            int y = 0;
            int right;
            int top;

            public void begin() {
                if (!Notify.this.showing) {
                    try {
                        Notify.this.setOpacity(0.0F);
                    } catch (Exception ignored) {
                    }

                    switch (Notify.this.notification.getLocationNotify()) {
                        case TOP_CENTER -> {
                            this.x = jFrame.getX() + (jFrame.getWidth() - Notify.this.getWidth()) / 2;
                            this.y = jFrame.getY() + 5;
                        }
                        case TOP_RIGHT -> {
                            this.x = jFrame.getX() + jFrame.getWidth() - Notify.this.getWidth() - 10;
                            this.y = jFrame.getY() + 15;
                        }
                        case TOP_LEFT -> {
                            this.x = jFrame.getX() + 10;
                            this.y = jFrame.getY() + 15;
                        }
                        case BOTTOM_CENTER -> {
                            this.x = jFrame.getX() + (jFrame.getWidth() - Notify.this.getWidth()) / 2;
                            this.y = jFrame.getY() + jFrame.getHeight() - Notify.this.getHeight() - 10;
                        }
                        case BOTTOM_RIGHT -> {
                            this.x = jFrame.getX() + jFrame.getWidth() - Notify.this.getWidth() - 10;
                            this.y = jFrame.getY() + jFrame.getHeight() - Notify.this.getHeight() - 20;
                        }
                        case BOTTOM_LEFT -> {
                            this.x = jFrame.getX() + 10;
                            this.y = jFrame.getY() + jFrame.getHeight() - Notify.this.getHeight() - 20;
                        }
                    }

                    this.top = this.y;
                    this.right = this.x;
                    Notify.this.setLocation(this.x, this.y);
                    Notify.this.setShape(new RoundRectangle2D.Double(0.0, 0.0, Notify.this.getWidth(), Notify.this.getHeight(), 8.0, 8.0));
                    Notify.this.setVisible(true);
                }

            }

            public void end() {
                Notify.this.showing = !Notify.this.showing;
                if (Notify.this.showing) {
                    Notify.this.thread = new Thread(() -> {
                        Notify.this.sleep();
                        Notify.this.closeNotification();
                    });
                    Notify.this.thread.start();
                } else {
                    Notify.this.dispose();
                }

            }

            public void timingEvent(float fraction) {
                float alpha;
                if (Notify.this.showing) {
                    alpha = 1.0F - fraction;
                    switch (Notify.this.notification.getLocationNotify()) {
                        case TOP_CENTER -> {
                            this.y = (int) ((1.0F - fraction) * 10.0F);
                            Notify.this.setLocation(this.x, this.top + this.y);
                        }
                        case TOP_RIGHT, BOTTOM_RIGHT -> {
                            this.x = (int) ((1.0F - fraction) * 10.0F);
                            Notify.this.setLocation(this.right - this.x, this.y);
                        }
                        case TOP_LEFT, BOTTOM_LEFT -> {
                            this.x = (int) ((1.0F - fraction) * 10.0F);
                            Notify.this.setLocation(this.right + this.x, this.y);
                        }
                        case BOTTOM_CENTER -> {
                            this.y = (int) ((1.0F - fraction) * 10.0F);
                            Notify.this.setLocation(this.x, this.top - this.y);
                        }
                    }
                } else {
                    alpha = fraction;
                    switch (Notify.this.notification.getLocationNotify()) {
                        case TOP_CENTER -> {
                            this.y = (int) (fraction * 10.0F);
                            Notify.this.setLocation(this.x, this.top + this.y);
                        }
                        case TOP_RIGHT, BOTTOM_RIGHT -> {
                            this.x = (int) (fraction * 10.0F);
                            Notify.this.setLocation(this.right - this.x, this.y);
                        }
                        case TOP_LEFT, BOTTOM_LEFT -> {
                            this.x = (int) (fraction * 10.0F);
                            Notify.this.setLocation(this.right + this.x, this.y);
                        }
                        case BOTTOM_CENTER -> {
                            this.y = (int) (fraction * 10.0F);
                            Notify.this.setLocation(this.x, this.top - this.y);
                        }
                    }
                }

                Notify.this.setShape(new RoundRectangle2D.Double(0.0, 0.0, Notify.this.getWidth(), Notify.this.getHeight(), 8.0, 8.0));

                try {
                    Notify.this.setOpacity(alpha);
                } catch (Exception ignored) {
                }

            }
        };
        this.animator = new Animator(500, timingTarget);
        this.animator.setResolution(5);
    }

    public void showNotification() {
        this.animator.start();
    }

    public void closeNotification() {
        if (this.thread != null && this.thread.isAlive()) {
            this.thread.interrupt();
        }

        if (this.animator.isRunning()) {
            if (!this.showing) {
                this.animator.stop();
                this.showing = true;
                this.animator.start();
            }
        } else {
            this.showing = true;
            this.animator.start();
        }

    }

    private void sleep() {
        while (true) {
            try {
                this.progressBar.setValue(this.progressBar.getValue() - 1);
                Thread.sleep(35L);
                if (this.progressBar.getValue() != 0) {
                    continue;
                }
            } catch (InterruptedException ignored) {
            }

            return;
        }
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
        panel1.setLayout(new GridLayoutManager(1, 3, new Insets(8, 8, 0, 8), 10, 0));
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
        btnExit = new JButton();
        btnExit.setBorderPainted(false);
        btnExit.setContentAreaFilled(false);
        btnExit.setIcon(new ImageIcon(getClass().getResource("/com/moreno/Icons/x24/cerrar.png")));
        btnExit.setPressedIcon(new ImageIcon(getClass().getResource("/com/moreno/Icons/x24/cerrar3.png")));
        btnExit.setRolloverIcon(new ImageIcon(getClass().getResource("/com/moreno/Icons/x24/cerrar2.png")));
        panel1.add(btnExit, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_NORTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(24, 24), new Dimension(24, 24), new Dimension(24, 24), 0, false));
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
    }
}
