package com.wxy.utils.watermark;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

public class PictureWaterMarkUtil {
    /**
     * @param srcImgPath 源图片路径
     * @param tarImgPath 保存的图片路径
     * @param waterMarkContent 水印内容
     * @param markContentColor 水印颜色
     * @param font 水印字体
     */
    public static void addWaterMark(String srcImgPath, String tarImgPath) {

        try {
            // 读取原图片信息
            File srcImgFile = new File(srcImgPath);//得到文件
            Image srcImg = ImageIO.read(srcImgFile);//文件转化为图片
            int srcImgWidth = srcImg.getWidth(null);//获取图片的宽
            int srcImgHeight = srcImg.getHeight(null);//获取图片的高
            // 加水印
            BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufImg.createGraphics();
            g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
            Color color= new Color(0,0,0,80);  //水印图片色彩以及透明度
            g.setColor(color); //根据图片的背景设置水印颜色
            Font font = new Font("微软雅黑", Font.ITALIC,80);
            g.setFont(font);//设置字体
            //设置倾斜
            g.rotate(Math.toRadians(-30), bufImg.getWidth() / 2,
                    bufImg.getHeight() / 2);
            //设置水印的坐标
            //右下角
            // int x = srcImgWidth - getWatermarkLength(waterMarkContent, g) - 3;
            // int y = srcImgHeight - 3;
            //中间
            String waterMarkContent="测试水印";  //水印内容
            int x = (srcImgWidth - getWatermarkLength(waterMarkContent, g)) / 2;
            int y = srcImgHeight / 2;
            g.drawString(waterMarkContent, x, y);  //画出水印
            g.dispose();
            // 输出图片
            FileOutputStream outImgStream = new FileOutputStream(tarImgPath);
            ImageIO.write(bufImg, "jpg", outImgStream);
            System.out.println("添加水印完成");
            outImgStream.flush();
            outImgStream.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public static int getWatermarkLength(String waterMarkContent, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).charsWidth(waterMarkContent.toCharArray(), 0, waterMarkContent.length());
    }
    public static void main(String[] args) {
        String srcImgPath="C:\\Users\\wxy\\Pictures\\CpoxxF5Mv3yAH74mAACOiTd9pO4462.jpg"; //源图片地址
        String tarImgPath="C:\\Users\\wxy\\Pictures\\1.jpg"; //待存储的地址
        addWaterMark(srcImgPath, tarImgPath);
        System.out.println("添加成功");
    }
}
