<a href='https://www.aliyun.com/sale-season/2020/procurement-new-members?userCode=6vik1cql' target='_blank'>![image](https://www.vbox.top/aliyun730X233.jpg)</a>
# PackagePlugin
可视日历控件实现了日期范围限制、两个日期控件范围联动等功能。鼠标悬停可以显示农历。

## <a href='http://www.vbox.top?from=github' target='_blank'>更多小工具请前往我的博客</a>

# 软件简介 

工作中很多项目用到jquery的日期控件，功能很强大，使用也方便，所以就以学习及巩固java基础为目的写了个java版的日历控。实现了日期范围限制、两个日期控件范围联动等功能。鼠标悬停可以显示农历。


开发环境：jdk6+

开发工具：netbeans 7.0

用法示例：

```
//frame 为父窗口可以为空  true-模态窗口  beginDate文本框 双击日期时回填到此文本框
DatePicker beginDatePicker = new DatePicker(frame, true, beginDate);
DatePicker enDatePicker = new DatePicker(frame, true, endDate);
DatePicker.dateRange(beginDatePicker, enDatePicker);//设置联动
 
beginDatePicker.setLocation(p);
beginDatePicker.setVisible(true);
```

运行效果如下

![image](http://www.vbox.top/wp-content/uploads/2017/07/datePicker1.jpg)

# <a href='http://www.vbox.top?from=github' target='_blank'>更多小工具请前往我的博客</a>
