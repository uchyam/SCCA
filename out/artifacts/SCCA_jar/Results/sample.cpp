2022-01-14 16:03:50
#include <iostream>

using namespace std;

//! SCCA Comment
int add(int, int);

// SCCA Comment
void printMax(int a, int b);

/**
 * SCCA Comment
 *@brief
 *@param
 *@return
 */
int main()
{
    //! SCCA Comment
    int sum;

    sum = add(100, 78);

    cout << "100 + 78 = " << sum << endl;

    return 0;
}

/**
 * SCCA Comment
 * @brief
 * @param a
 * @param b
 */
int add(int a, int b)
{
    return (a + b);
}
