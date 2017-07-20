package com.datePicker;

import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;
import org.jvnet.substance.skin.SubstanceCremeCoffeeLookAndFeel;

/**
 * 日期控件
 *
 * @author lancw
 * @since 2011-12-12
 * @version 1.0
 */
public class DatePicker extends JDialog {

    /**
     * 构造函数
     *
     * @param parent 父窗体
     * @param modal 是否为模态窗口
     * @param textField 日期输入框（回显）
     */
    public DatePicker(Frame parent, boolean modal, JTextComponent textField) {
        super(parent, modal);
        if (textField == null) {
            throw new IllegalArgumentException("textField is null");
        } else {
            this.textField = textField;
        }
        init(parent);
    }

    /**
     * 构造函数
     *
     * @param parent 父窗体
     * @param modal 是否为模态窗口
     * @param textField 日期输入框（回显）
     * @param textField_Lunar 农历输入框（回显）
     */
    public DatePicker(Frame parent, boolean modal, JTextComponent textField, JTextComponent textField_Lunar) {
        super(parent, modal);
        if (textField == null) {
            throw new IllegalArgumentException("textField is null");
        } else {
            this.textField = textField;
        }
        if (textField_Lunar != null) {
            this.textField_Lunar = textField_Lunar;
        } else {
            throw new IllegalArgumentException("textField_Lunar is null");
        }
        init(parent);
    }

    /**
     * 初始化
     *
     * @param parent
     */
    private void init(Frame parent) {
        initComponents();
        setResizable(false);
        setUndecorated(true);
        setTitle("日期控件 1.0");
        setSize(268, 202);
        table.setRowHeight(20);
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultRenderer(Object.class, new CellRanderer(this));
        setLocationRelativeTo(parent);
        maxDate.set(2049, 11, 31);
        minDate.set(1900, 2, 1);
        initCalendar();
        displaySelectedMonth();
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        year_box.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    itemChange(e.getItem().toString().replace("年", ""), true);
                }
            }
        });
        month_box.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    itemChange(e.getItem().toString().replace("月", ""), false);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(new SubstanceCremeCoffeeLookAndFeel());
                    JFrame.setDefaultLookAndFeelDecorated(true);
                    JDialog.setDefaultLookAndFeelDecorated(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                DatePicker d = new DatePicker(null, true, new JTextField());
                d.setDateRange("-10d,+1d");//范围30个月前到30天后
                d.setVisible(true);
            }
        });
    }

    /**
     * 初始化日历
     */
    public void initCalendar() {
        int max = calendar.getActualMaximum(Calendar.DATE);//本月最大天数
        int month = calendar.get(Calendar.MONTH);//当前月份
        int year = calendar.get(Calendar.YEAR);//当前年份
        TableModel model = table.getModel();
        calendar.set(Calendar.DATE, 1);
        Calendar last = (Calendar) calendar.clone();
        last.set(Calendar.MONTH, month - 1);
        Calendar next = (Calendar) calendar.clone();
        next.set(Calendar.MONTH, month + 1);
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;//当前星期
        int count = 0;
        for (int i = 0, j = 0; i < 42; i++) {
            int value = i + 1 - day;
            if ((j >= day && value < max) || (count > 0 && value <= max)) {
                String style = "";
                String disable = checkRange(year, month, value);
                if (isToday(year, month, value)) {
                    style = "color:red;";
                } else if (j == 0 || j == 6) {
                    style = "color:#FF1493;";
                }
                if (!disable.equals("")) {
                    style = "color:#999999;";
                }
                String str = "<html><p " + disable + " style='text-align:center;width:30px;" + style + "'>" + value + "</p></html>";
                model.setValueAt(str, count, j);
            } else if (j < day && count == 0) {
                int m = last.getActualMaximum(Calendar.DATE);
                String str = "<html><p id='last' disable='true' style='text-align:center;width:30px;color:#999999;'>" + (m - day + i + 1) + "</html>";
                model.setValueAt(str, count, j);
            } else if (value >= max) {
                String str = "<html><p id='next' disable='true' style='text-align:center;width:30px;color:#999999;'>" + (i - max + 1 - day) + "</html>";
                model.setValueAt(str, count, j);
            }
            j++;
            if (j % 7 == 0) {
                j = 0;
                count++;
            }
        }
    }

    /**
     * 检查日期是否超出范围
     *
     * @param y
     * @param m
     * @param d
     * @return
     */
    private String checkRange(int y, int m, int d) {
        Calendar temp = Calendar.getInstance();
        temp.set(y, m, d);
        if (compareDate(temp, minDate, "yyyyMMdd") < 0 || compareDate(temp, maxDate, "yyyyMMdd") > 0) {
            return "disable='true'";
        }
        return "";
    }

    /**
     * 初始化日期范围
     */
    private void initRangeDate() {
        Calendar now = Calendar.getInstance();
        if (max_year != -1) {
            maxDate.set(Calendar.YEAR, now.get(Calendar.YEAR) + max_year);
        }
        if (max_month != -1) {
            if (max_year == -1) {
                maxDate.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            }
            maxDate.set(Calendar.MONTH, now.get(Calendar.MONTH) + max_month);
        }
        if (max_date != -1) {
            if (max_year == -1) {
                maxDate.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            }
            if (max_month == -1) {
                maxDate.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
            } else {
                maxDate.set(Calendar.MONTH, maxDate.get(Calendar.MONTH) + 1);
            }
            maxDate.set(Calendar.DATE, now.get(Calendar.DATE) + max_date);
        }
        //初始化最小日期
        if (min_year != -1) {
            minDate.set(Calendar.YEAR, now.get(Calendar.YEAR) - min_year);
            if (min_month == -1 && min_date == -1) {
                minDate.set(Calendar.MONTH, 0);
                minDate.set(Calendar.DATE, 1);
            }
        }
        if (min_month != -1) {
            if (min_year == -1) {
                minDate.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            }
            minDate.set(Calendar.MONTH, now.get(Calendar.MONTH) - min_month);
        }
        if (min_date != -1) {
            if (min_year == -1) {
                minDate.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            }
            if (min_month == -1) {
                minDate.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
            }
            minDate.set(Calendar.DATE, now.get(Calendar.DATE) - min_date);
        }
    }

    /**
     * 设置可选择的日期范围
     * <p>
     * 参数格式：+0Y,-1M,+2D
     * <p>
     * Y、M、D分别表示年、月、日。+表示比当前日期大，-表示比当前日期小。
     * <p>
     * 若同时设置日期上限和下限参数可以是："-2Y,-3M,-2D,+1Y,+1M,+1D"这个字符串。
     * <p>
     * 上例参数表示：最大可选日期为当前日期后一年零一个月多一天，最小可选日期为比当前日期前两年零三个月多两天
     * <p>
     * 参数顺序不限，大小写不限。若参数中对同一值进行多次设置，效果为后一次。如："+1M,+2M"此时最大可选日期为当前日期后两个月
     * <p>
     * 参数格式错误时不生效，也不报错。
     *
     * @param patten
     */
    public void setDateRange(String patten) {
        if (patten == null || patten.trim().equals("")) {
            return;
        }
        String[] temp = patten.toLowerCase().split(",");
        if (temp.length > 6) {
            return;
        }
        for (int i = 0; i < temp.length; i++) {
            if (temp[i].endsWith("y")) {
                if (temp[i].startsWith("+")) {
                    max_year = Integer.parseInt(temp[i].replace("+", "").replace("y", ""));
                } else if (temp[i].startsWith("-")) {
                    min_year = Integer.parseInt(temp[i].replace("-", "").replace("y", ""));
                }
            } else if (temp[i].endsWith("m")) {
                if (temp[i].startsWith("+")) {
                    max_month = Integer.parseInt(temp[i].replace("+", "").replace("m", ""));
                } else if (temp[i].startsWith("-")) {
                    min_month = Integer.parseInt(temp[i].replace("-", "").replace("m", ""));
                }
            } else if (temp[i].endsWith("d")) {
                if (temp[i].startsWith("+")) {
                    max_date = Integer.parseInt(temp[i].replace("+", "").replace("d", ""));
                } else if (temp[i].startsWith("-")) {
                    min_date = Integer.parseInt(temp[i].replace("-", "").replace("d", ""));
                }
            }
        }
        initRangeDate();
        initCalendar();
        displaySelectedMonth();
    }

    /**
     * 下拉框改变事件
     *
     * @param value
     * @param isYear
     */
    private void itemChange(String value, boolean isYear) {
        if (isYear) {
            calendar.set(Calendar.YEAR, Integer.parseInt(value));
        } else {
            calendar.set(Calendar.MONTH, Integer.parseInt(value) - 1);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int minM = minDate.get(Calendar.MONTH);
        int maxM = maxDate.get(Calendar.MONTH);
        if (year == minDate.get(Calendar.YEAR) && month < minM) {
            calendar.set(Calendar.MONTH, minM);
            month_box.setSelectedItem(++minM + "月");
        } else if (year == maxDate.get(Calendar.YEAR) && month > maxM) {
            calendar.set(Calendar.MONTH, maxM);
            month_box.setSelectedItem(++maxM + "月");
        }
        displaySelectedMonth();
        initCalendar();
    }

    /**
     * 刷新下拉框
     */
    public void displaySelectedMonth() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        DefaultComboBoxModel year_model = new DefaultComboBoxModel();
        DefaultComboBoxModel month_model = new DefaultComboBoxModel();
        int maxY = maxDate.get(Calendar.YEAR);
        int maxM = maxDate.get(Calendar.MONTH);
        int minY = minDate.get(Calendar.YEAR);
        int minM = minDate.get(Calendar.MONTH);
        int count = (maxY - minY + 2) > 60 ? 60 : (maxY - minY + 2);
        int temp = year - count / 2;
        if (count < 30) {
            temp = minY;
        }
        if (temp < 1900) {
            temp = 1900;
        }
        for (int i = 0; i < count; i++) {
            if (temp >= minY && temp <= maxY) {
                year_model.addElement(temp + "年");
            }
            temp++;
            if (temp > 2049 || temp > maxY) {
                break;
            }
        }
        int start = minY == year ? minM + 1 : 1, num = maxY == year ? maxM + 1 : 12;
        for (int i = start; i <= num; i++) {
            month_model.addElement(i + "月");
        }
        year_model.setSelectedItem(year + "年");
        month_model.setSelectedItem(month + "月");
        year_box.setModel(year_model);
        month_box.setModel(month_model);
    }

    /**
     * 判断是否为当日
     *
     * @param month
     * @param date
     * @return
     */
    private boolean isToday(int year, int month, int date) {
        Calendar c = Calendar.getInstance();
        return month == c.get(Calendar.MONTH) && date == c.get(Calendar.DATE) && year == c.get(Calendar.YEAR);
    }

    /**
     * 设置两个日期控件范围联动 用于限制起始日期不能大于结束日期及结束日期不能小于起始日期 如果from 或 to 其中一个为null 设置无效
     *
     * @param from 起始日期
     * @param to 结束日期
     */
    public static void dateRange(final DatePicker from, final DatePicker to) {
        if (from == null || to == null) {
            return;
        }
        from.addWindowListener(new WindowAdapter() {
            private void setValue(WindowEvent e) {
                if (from.getSelectedDate() != null) {
                    to.setMinDate(from.getSelectedDate());
                    to.initCalendar();
                    to.displaySelectedMonth();
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {
                setValue(e);
            }
        });
        to.addWindowListener(new WindowAdapter() {
            private void setValue(WindowEvent e) {
                if (to.getSelectedDate() != null) {
                    from.setMaxDate(to.getSelectedDate());
                    from.initCalendar();
                    from.displaySelectedMonth();
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {
                setValue(e);
            }
        });
    }

    /**
     * 获取当前日期控件所选择的日期
     *
     * @return
     */
    public Calendar getSelectedDate() {
        return selectDate;
    }

    public void setMaxDate(Calendar maxDate) {
        this.maxDate = maxDate;
    }

    public void setMinDate(Calendar minDate) {
        this.minDate = minDate;
    }

    /**
     * 比较两个日期的大小
     *
     * @param com
     * @param to
     * @param patten 比较的级别 如果只比较年份patten=yyyy 如果比较年月日patten=yyyyMMdd
     * 等等。不得带“-”、“/”等字符
     * @return 如果com小于to返回-1,如果com等于to返回0,如果com大于to返回1
     */
    public int compareDate(Calendar com, Calendar to, String patten) {
        SimpleDateFormat sdf = new SimpleDateFormat(patten);
        BigInteger cur = new BigInteger(sdf.format(com.getTime()));
        BigInteger min = new BigInteger(sdf.format(to.getTime()));
        return cur.compareTo(min);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btn_minus = new javax.swing.JButton();
        year_box = new javax.swing.JComboBox();
        btn_plus = new javax.swing.JButton();
        btn_today = new javax.swing.JButton();
        month_box = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();

        btn_minus.setText("<");
        btn_minus.setFocusable(false);
        btn_minus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_minusActionPerformed(evt);
            }
        });

        year_box.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        year_box.setFocusable(false);

        btn_plus.setText(">");
        btn_plus.setFocusable(false);
        btn_plus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_plusActionPerformed(evt);
            }
        });

        btn_today.setText("今");
        btn_today.setFocusable(false);
        btn_today.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_todayActionPerformed(evt);
            }
        });

        month_box.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        month_box.setFocusable(false);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][]{
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String[]{
                "日", "一", "二", "三", "四", "五", "六"
            }) {

                Class[] types = new Class[]{
                    java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
                };

                public Class getColumnClass(int columnIndex) {
                    return types[columnIndex];
                }

                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            });
            table.setCellSelectionEnabled(true);
            table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            table.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    tableMouseClicked(evt);
                }
            });
            jScrollPane1.setViewportView(table);

            javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(btn_minus, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(year_box, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(month_box, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btn_plus, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btn_today, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(71, Short.MAX_VALUE))
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_minus)
                        .addComponent(year_box, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_today)
                        .addComponent(btn_plus)
                        .addComponent(month_box, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(79, Short.MAX_VALUE))
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(70, Short.MAX_VALUE))
            );
        }// </editor-fold>//GEN-END:initComponents

    private void btn_minusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_minusActionPerformed
        if (compareDate(calendar, minDate, "yyyyMM") <= 0) {
            return;
        }
        calendar.add(Calendar.MONTH, -1);
        initCalendar();
        displaySelectedMonth();
    }//GEN-LAST:event_btn_minusActionPerformed

    private void btn_plusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_plusActionPerformed
        if (compareDate(calendar, maxDate, "yyyyMM") >= 0) {
            return;
        }
        calendar.add(Calendar.MONTH, 1);
        initCalendar();
        displaySelectedMonth();
    }//GEN-LAST:event_btn_plusActionPerformed

    private void btn_todayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_todayActionPerformed
        calendar.setTime(new Date());
        displaySelectedMonth();
        initCalendar();
    }//GEN-LAST:event_btn_todayActionPerformed

    private void tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableMouseClicked
        if (evt.getClickCount() == 2) {
            int column = table.getSelectedColumn();
            int row = table.getSelectedRow();
            if (column >= 0 && row >= 0) {
                String str = table.getValueAt(row, column).toString();
                if (str.indexOf("disable='true'") > 0) {
                    return;
                }
                str = str.replaceAll("</?[a-zA=Z]+[^>]*?>", "").replaceAll("[\u4e00-\u9fa5]", "");
                int date = Integer.parseInt(str);
                int year = Integer.parseInt(year_box.getSelectedItem().toString().replace("年", ""));
                int month = Integer.parseInt(month_box.getSelectedItem().toString().replace("月", ""));
                if (format == null) {
                    format = new SimpleDateFormat("yyyy-MM-dd");
                }
                selectDate = Calendar.getInstance();
                selectDate.set(Calendar.YEAR, year);
                selectDate.set(Calendar.MONTH, month - 1);
                selectDate.set(Calendar.DATE, date);
                if (textField != null) {
                    textField.setText(format.format(selectDate.getTime()));
                }
                if (textField_Lunar != null) {
                    textField_Lunar.setText(table.getToolTipText(evt).replace("农历", "").replaceAll("</?[a-zA=Z]+[^>]*?>", ""));
                }
                dispose();
            }
        }
    }//GEN-LAST:event_tableMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_minus;
    private javax.swing.JButton btn_plus;
    private javax.swing.JButton btn_today;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JComboBox month_box;
    private javax.swing.JTable table;
    public javax.swing.JComboBox year_box;
    // End of variables declaration//GEN-END:variables
    private JTextComponent textField;
    private JTextComponent textField_Lunar;
    private SimpleDateFormat format;
    private Calendar maxDate = Calendar.getInstance();
    private Calendar minDate = Calendar.getInstance();
    private Calendar calendar = Calendar.getInstance();
    private Calendar selectDate;
    private int max_year = -1, max_month = -1, max_date = -1, min_year = -1, min_month = -1, min_date = -1;
    final static String[] week = new String[]{
        "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"
    };
    public static String chineseNumber[]
            = {
                "正月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "腊月"
            };
}
