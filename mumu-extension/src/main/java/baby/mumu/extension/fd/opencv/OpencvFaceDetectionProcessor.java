/*
 * Copyright (c) 2024-2024, kaiyu.shan@outlook.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package baby.mumu.extension.fd.opencv;

import baby.mumu.extension.fd.FaceDetection;
import baby.mumu.extension.fd.FaceDetectionProcessor;
import java.util.Optional;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;


/**
 * opencv人脸检测处理器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.5
 */
public class OpencvFaceDetectionProcessor implements FaceDetectionProcessor {

  private final CascadeClassifier faceDetector;

  public OpencvFaceDetectionProcessor(CascadeClassifier faceDetector) {
    this.faceDetector = faceDetector;
  }

  @Override
  public Long numberOfFaces(FaceDetection faceDetection) {
    return Optional.ofNullable(faceDetection).map(faceDetectionNonNull -> {
      Result result = getResult(faceDetectionNonNull);
      faceDetector.detectMultiScale(result.grayImage, result.faces);
      return result.faces.size();
    }).orElse(0L);
  }

  @Override
  public void drawBorder(FaceDetection faceDetection) {
    Optional.ofNullable(faceDetection).ifPresent(faceDetectionNonNull -> {
      Result result = getResult(
          faceDetectionNonNull);
      // 绘制矩形框在检测到的人脸周围
      try (RectVector faces = result.faces()) {
        faceDetector.detectMultiScale(result.grayImage(), faces);
        for (int i = 0; i < faces.size(); i++) {
          Rect face = faces.get(i);
          opencv_imgproc.rectangle(result.image(), face, Scalar.RED, 2,
              opencv_imgproc.LINE_8, 0);
        }
        opencv_imgcodecs.imwrite(faceDetection.getImageOutputAbsolutePath(), result.image());
      }
    });
  }

  @Contract("_ -> new")
  private static @NotNull Result getResult(@NotNull FaceDetection faceDetectionNonNull) {
    String imagePath = faceDetectionNonNull.getImageAbsolutePath();
    Mat image = opencv_imgcodecs.imread(imagePath);
    if (image.empty()) {
      throw new RuntimeException("Unable to load image: " + imagePath);
    }
    Mat grayImage = new Mat();
    opencv_imgproc.cvtColor(image, grayImage, opencv_imgproc.COLOR_BGR2GRAY);
    RectVector faces = new RectVector();
    return new Result(image, grayImage, faces);
  }

  private record Result(Mat image, Mat grayImage, RectVector faces) {

  }
}
