#include <iostream>

using namespace std;

int add(int, int);

void printMax(int a,int b);

int main() {
    int sum;

    sum = add(100, 78);

    cout << "100 + 78 = " << sum << endl;

    return 0;
}

int add(int a, int b) {
    return (a + b);
}