package com.datePicker;

import javax.swing.table.DefaultTableCellRenderer;

/**
 * 日历控件CellRenderer
 *
 * @since 2011-12-17
 * @author lancw
 */
public class CellRanderer extends DefaultTableCellRenderer {

    DatePicker main;

    public CellRanderer(DatePicker main) {
        this.main = main;
    }

    @Override
    protected void setValue(Object value) {
        super.setValue(value);
        String date_str = value.toString();
        boolean is_last = date_str.indexOf("id='last'") > 0;//是否为上个月
        boolean is_next = date_str.indexOf("id='next'") > 0;//是否为下个月
        date_str = date_str.replaceAll("</?[a-zA=Z]+[^>]*?>", "");
        if (date_str.trim().equals("")) {
            setEnabled(false);
            return;
        }
        int year = Integer.parseInt(main.year_box.getSelectedItem().toString().replace("年", ""));
        int month = Integer.parseInt(main.month_box.getSelectedItem().toString().replace("月", ""));
        int date = Integer.parseInt(date_str.replaceAll("[\u4e00-\u9fa5]", ""));
        if (is_last) {
            month -= 1;
        }
        if (is_next) {
            month += 1;
        }
        String d = Lunar.getLunarDate(year, month, date);
        String str = "<html><p style='text-align:center;width:70px;'>农历" + DatePicker.chineseNumber[(int) Lunar.month - 1] + d + "</p></html>";
        setToolTipText(str);
    }
}
