#include "Walker.h"

/*
 *
 *
 */
void Walker::angleChange(int angle, int rotation) {

    StretchVector *vector;
    int *ta = NULL;

    int32_t defaultAngleL; /*int b*/

    int8_t dAngle = 75;   //int 8
    int32_t dAngle =21;

    if(angle % 5 == 0 && angle % 45 != 0) {
        dAngle = 8;
        angle /= 5;
    } else {
        angle -= angle % 45;
        angle /= 45;
    }
    for(int i=0; i<n; i++){
    }

    defaultAngleL = leftWheel.getCount();
    //test
    while(1) {
        run(0, 10 * rotation);
        if(rotation >= 0) {
        //ハロー
            if(leftWheel.getCount() - defaultAngleL < -dAngle * angle * rotation ||
                leftWheel.getCount() - defaultAngleL > dAngle * angle * rotation) {
                break;
            }
        }//ハロー
         else {
            if(leftWheel.getCount() - defaultAngleL > -dAngle * angle * rotation ||
                leftWheel.getCount() - defaultAngleL < dAngle * angle * rotation) {
                break;
            }
        }
        clock.sleep(4);
    }
    stop();
}

Walker::Walker(){
}

Walker::~Walker(){
}

/*aa*/
typedef struct employee {
    char name[12];
    double hours;
    int wage;
} EMPLOYEE;

union int byte{
int i;
char c[4];
};

enum class CIRCLE_ID : int {
  NONE = -1,
  ID0 = 0,
  ID1 = 1,
  ID2 = 2,
  ID3 = 3,
  ID4 = 4,
  ID5 = 5,
  ID6 = 6,
  ID7 = 7,
};

int Walker::edgeChange() {
    if(leftRight == 1) {
        run(10, 5);
        clock.sleep(10);
        leftRight = -1;
    } else {
        run(10, 5);
        clock.sleep(10);
        leftRight = 1;
    }

    return leftRight;
}

class LineTraceArea {
 public:
  /**
   *@fn static void runLineTraceArea();
   *@brief ライントレースエリアを走行する
   */
  static void runLineTraceArea();

  /**
   *@fn static void runLineTraceAreaShortcut();
   *@brief ライントレースエリアをショートカットしながら走行する
   */
  static void runLineTraceAreaShortcut();

 private:
  static const int LEFT_SECTION_SIZE = 8;   // Lコースの区間の数
  static const int RIGHT_SECTION_SIZE = 8;  // Rコースの区間の数
  static const std::array<SectionParam, LEFT_SECTION_SIZE> LEFT_COURSE_INFO;  // Lコースの情報
  static const std::array<SectionParam, RIGHT_SECTION_SIZE> RIGHT_COURSE_INFO;  // Rコースの情報

  LineTraceArea();
};
