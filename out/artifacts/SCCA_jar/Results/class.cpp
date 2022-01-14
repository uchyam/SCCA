2022-01-14 15:48:41
//test
#include <cassert>
#include <iostream>
#include <string>

#define SIZE_OF_ARRAY(array) (sizeof(array)/sizeof(array[0]))

enum color {
  Red,    // 0
  Blue,   // 1
  Green,  // 2
};

int a;

class Student {
private:
    class Score {
    public:
        Score(int japanese, int math, int english);

    public:
        // 平�?点を返す
        int GetAverage() const;

    private:
        enum Subject {
            SUBJECT_JAPANESE,
            SUBJECT_MATH,
            SUBJECT_ENGLISH,

            SUBJECT_NUM,  // 総数を表すダミ�?�
        };

    private:
        int  mScores[SUBJECT_NUM];
    };

public:
    Student(const std::string& name, int japanese, int math, int english) :
        mName(name),
        mScore(japanese, math, english)
    {}

public:
    /**
     * SCCA Comment
     *@brief
     *@param
     *@return
     */
    inline const std::string& GetName() const
    {
        return mName;
    }

    // SCCA Comment
    inline int GetAverage() const
    {
        return mScore.GetAverage();
    }

private:
    const std::string  mName;
    const Score        mScore;
};

/**
 * SCCA Comment
 * @brief
 * @param japanese
 * @param math
 * @param english
 */
Student::Score::Score(int japanese, int math, int english)
{
    mScores[SUBJECT_JAPANESE] = japanese;
    mScores[SUBJECT_MATH]     = math;
    mScores[SUBJECT_ENGLISH]  = english;
}

/**
 * SCCA Comment
 * @brief
 */
int Student::Score::GetAverage() const
{
    int sum = 0;
    for (int i = 0; i < SIZE_OF_ARRAY(mScores); ++i) {
        sum += mScores[i];
    }
    return sum / SIZE_OF_ARRAY(mScores);
}


/**
 * SCCA Comment
 * @brief
 */
int main()
{
    Student student("Tanaka Miki", 92, 66, 75);

    std::cout << "Name: " << student.GetName() << "\n"
              << "  Average: " << student.GetAverage() << std::endl;
}
