#include <cassert>
#include <iostream>
#include <string>

struct Student{
char name;
int age;
};

int global_var = 123;

struct Student{
char name;
int age;
};//Studentに対するコメント

int func(int a, int b){

int c;

c = a+b;

int d;

d = 2 * c;

return a+b;
}

char name;
int age;//ageに対するコメント

age = age + 2;

int sum(int a, int b){
  if(a > 0){
  a = a + 1;
  } else {
  a = a - 1;
  }
  return a + b;
}

int func2(int &a){}
