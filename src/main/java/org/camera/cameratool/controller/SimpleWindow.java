package org.camera.cameratool.controller;

import javax.swing.*;

public class SimpleWindow {
    public static void main(String[] args) {
        // 创建窗口
        JFrame frame = new JFrame("简单窗口");
        frame.setSize(400, 300); // 设置窗口大小
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 关闭时退出程序

        // 创建按钮
        JButton button = new JButton("点击我");
        button.setBounds(150, 100, 100, 50); // 设置按钮位置和大小

        // 按钮点击事件
        button.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "按钮被点击了！");
        });

        // 添加按钮到窗口
        frame.setLayout(null); // 使用绝对布局
        frame.add(button);

        // 显示窗口
        frame.setVisible(true);
    }
}
