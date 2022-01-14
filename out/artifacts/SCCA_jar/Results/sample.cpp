2022-01-14 15:42:06
#include <iostream>

using namespace std;

int add(int, int);

// SCCA Comment
void printMax(int a,int b);

/**
     * SCCA Comment
     *@brief
     *@param
     *@return
     */
int main() {
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
int add(int a, int b) {
    return (a + b);
}
