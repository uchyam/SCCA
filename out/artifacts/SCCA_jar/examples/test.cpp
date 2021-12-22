#include "LineTraceArea.h"

void LineTraceArea::runLineTraceArea()
{
  const SectionParam* param;
  bool isLeftEdge;  // true:左エッジ,false:右エッジ

  if(IS_LEFT_COURSE) {
    // Lコースの場合
    runLineTraceAreaShortcut();
  } else {
    // Rコースの場合
    param = RIGHT_COURSE_INFO.begin();

    //エッジの設定
    isLeftEdge = !IS_LEFT_COURSE;

    // LineTracerにエッジを与えてインスタンス化する
    LineTracer lineTracer(isLeftEdge);

    // LRに応じて各区間を順番に走らせる
    for(int section = 0; section < (IS_LEFT_COURSE ? LEFT_SECTION_SIZE : RIGHT_SECTION_SIZE);
        section++) {
      // Linetracerクラスのrun関数に区間の情報を渡して走行させる
      lineTracer.run(param[section].sectionDistance, param[section].sectionTargetBrightness,
                     param[section].sectionPwm, param[section].sectionPidGain);
    }
  }
}

void LineTraceArea::runLineTraceAreaShortcut()
{
  const SectionParam* param;
  bool isLeftEdge;  // true:左エッジ,false:右エッジ

  //エッジの設定
  isLeftEdge = !IS_LEFT_COURSE;

  // LineTracerにエッジを与えてインスタンス化する
  LineTracer lineTracer(isLeftEdge);
  StraightRunner straightRunner;
  Measurer measurer;
  Controller controller;

  int targetBrightness = 12;
  int initialDistance = 0;
  int currentDistance = 0;

  int curveDistance1 = 777;
  int straightDistance1 = 440;
  int curveDistance2 = 777;
  int straightDistance2 = 1114;
  int curveDistance3 = 700;
  int straightDistance3 = 460;

  lineTracer.run(150, targetBrightness, 60, PidGain(0.3, 0.01, 0.01));
  lineTracer.run(1415, targetBrightness, 100, PidGain(3.512, 0.102, 0.221));

  //第一カーブ
  initialDistance = Mileage::calculateMileage(measurer.getRightCount(), measurer.getLeftCount());
  while(true) {
    currentDistance = Mileage::calculateMileage(measurer.getRightCount(), measurer.getLeftCount());
    if(currentDistance - initialDistance >= curveDistance1) {
      break;
    }
    controller.setRightMotorPwm(99);
    controller.setLeftMotorPwm(65);
    controller.sleep();
  }

  straightRunner.runStraightToDistance(straightDistance1, 100);

  //第二カーブ
  initialDistance = Mileage::calculateMileage(measurer.getRightCount(), measurer.getLeftCount());
  while(true) {
    currentDistance = Mileage::calculateMileage(measurer.getRightCount(), measurer.getLeftCount());
    if(currentDistance - initialDistance >= curveDistance2) {
      break;
    }
    controller.setRightMotorPwm(99);
    controller.setLeftMotorPwm(66);
    controller.sleep();
  }

  lineTracer.run(straightDistance2, targetBrightness, 100, PidGain(3, 1.21, 1));

  //第三カーブ
  initialDistance = Mileage::calculateMileage(measurer.getRightCount(), measurer.getLeftCount());
  while(true) {
    currentDistance = Mileage::calculateMileage(measurer.getRightCount(), measurer.getLeftCount());
    if(currentDistance - initialDistance >= curveDistance3) {
      break;
    }
    controller.setRightMotorPwm(63);
    controller.setLeftMotorPwm(99);
    controller.sleep();
  }

  //外れた時、黒線に乗るまで左に旋回
  int count = 0;
  initialDistance = Mileage::calculateMileage(measurer.getRightCount(), measurer.getLeftCount());
  COLOR color = ColorJudge::getColor(measurer.getRawColor());
  if(color == COLOR::WHITE) {
    while(true) {
      COLOR color = ColorJudge::getColor(measurer.getRawColor());
      if(color == COLOR::BLACK) {
        break;
      }
      controller.setRightMotorPwm(90);
      controller.setLeftMotorPwm(80);
      controller.sleep();
    }
  }
  //外れた時、黒線に乗るまでの走行距離
  count = Mileage::calculateMileage(measurer.getRightCount(), measurer.getLeftCount())
          - initialDistance;

  lineTracer.run(straightDistance3 - count, targetBrightness, 100, PidGain(5.5, 1, 1.5));
}