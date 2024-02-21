package org.example.captcha;

import lombok.Getter;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static org.example.util.ConfigLoader.getPathDownloadedCaptcha;

@Getter
public class PuzzleDetector {
    private double x;
    static {
        System.loadLibrary("opencv_java480");
    }

    public void detectPuzzleOnImage(String fileName) {
        // Загрузите изображение
        Mat imgOld = Imgcodecs.imread("%s/%s".formatted(getPathDownloadedCaptcha(), fileName));
        Mat template = Imgcodecs.imread("target.png");
        Size newSize = new Size(338, 195);

        Mat img = new Mat();
        // Измените размер изображения
        Imgproc.resize(imgOld, img, newSize);

        // Создайте новый матрицу для результата
        int result_cols = img.cols() - template.cols() + 1;
        int result_rows = img.rows() - template.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

        // Выполните сопоставление шаблона
        Imgproc.matchTemplate(img, template, result, Imgproc.TM_CCOEFF_NORMED);

        // Нормализуйте результат
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        // Найдите минимальное и максимальное значение и их местоположения
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        x = mmr.maxLoc.x + (double)template.cols() / 2.0;
    }
}


