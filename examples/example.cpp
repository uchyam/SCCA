#include <iostream>

int main() { return 0; }

bool findInFiles()
{
	const TCHAR* dir2Search = _findReplaceDlg.getDir2Search();

	findFilesInOut();
	if (!dir2Search[0] || !::PathFileExists(dir2Search))
	{
		return false;
	}
	string findString = "";

	gethurry();

	findInOne(int a, findString);

	bool isRecursive = _findReplaceDlg.isRecursive();
	bool isInHiddenDir = _findReplaceDlg.isInHiddenDir();

	if (a.size() == 0)
	{
		a.setFindInFilesDirFilter("dddd", TEXT("*.*"));
		a.getPatterns(findString);
	}

	return true;
}



/**
 * John		FindAndBreakTwoVerbs
 * Keith	NoVerbreak, voidReturn
 * Dylan	lastRealLine = True;
 */
void breakEverything(string foo, int bar) {


	string foob = FindAndBreakTwoVerbs("blahblahbreak");

	int breakDont = 3892;



	NoVerbreak();

	voidReturn("EqualsSign = true", 890);

	for (x in foo) {
		if (True) {
			return False
				lastRealLine = True;
		}
	}
}


/**
* John		getOtherInt
* Keith	intBet
* Dylan	int gg = getOtherInt(839201);
*/
void GetInt(int inted, int on) {
	intBet();
	int gg = getOtherInt(839201);

}

/**
* John		moveDirectory
* Keith	moveDirectory
* Dylan	moveDirectory(blah));
*/
void moveFiles(string blah) {
	moveDirectory(blah));
}


/**
* John		jumpString
* Keith
* Dylan	bool foo = jumpString(bar);
*/
int jumpInt(int bar) {
	bool foo = jumpString(bar);

}

/**
* John
* Keith
* Dylan	return True;
*/
int getTrue() {
	return true;
}

void EtRobocon2021::start()
{
    Controller controller;
    Measurer measurer;

    //タッチセンサが押されるまで待機
    while (!measurer.isPressed()) {
        controller.sleep();
    }

    //ライントレースエリア攻略開始
    LineTraceArea::runLineTraceArea();
    //ビンゴエリア攻略開始
    BingoArea::runBingoArea();
    //シミュレータへ競技の終了を通知する
    controller.notifyCompletedToSimulator();
}