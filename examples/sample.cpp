/**
 * @file LineTraceArea.h
 * @brief ライントレースエリアを攻略するクラス
 * @author Hisataka-Hagiyama,uchyam
 */

#ifndef LINETRACEAREA_H
#define LINETRACEAREA_H
#include "array"
#include "LineTracer.h"
#include "Pid.h"

#include "StraightRunner.h"
#include "Mileage.h"
#include "Measurer.h"
#include "Controller.h"

/**
 * Lコース/Rコース向けの設定を定義
 * デフォルトはLコース
 */
#if defined(MAKE_RIGHT)
static constexpr bool IS_LEFT_COURSE = false;  // Rコース
#else
static constexpr bool IS_LEFT_COURSE = true;  // Lコース
#endif

struct SectionParam {
  double sectionDistance;       //区間の走行距離
  int sectionTargetBrightness;  //区間の目標輝度
  int sectionPwm;               //区間のPWM値
  PidGain sectionPidGain;       //区間のPIDゲイン
};

class LineTraceArea {
 public:

  static void runLineTraceArea();

  static void runLineTraceAreaShortcut();

 private:
  static const int LEFT_SECTION_SIZE = 8;
  static const int RIGHT_SECTION_SIZE = 8;
  static const std::array<SectionParam, LEFT_SECTION_SIZE> LEFT_COURSE_INFO;
  static const std::array<SectionParam, RIGHT_SECTION_SIZE> RIGHT_COURSE_INFO;

  LineTraceArea();
};

#endif

/*???*/
typedef struct employee {
    char name[12];
    double hours;
    int wage;
} EMPLOYEE;