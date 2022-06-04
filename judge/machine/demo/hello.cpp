//
// Created by kmh on 2022/5/18.
//

#include <iostream>
#include <stdlib.h>
#include <unistd.h>

using namespace std;


int main() {
    string s;
    cin >> s;
    if (s.empty()) {
        ptr[0] = 1;
    }
//    sleep(2);
    if (ptr[0]) {
        cout << "hello" << endl;
    } else {
        cout << "hello1" << endl;
    }
    free(ptr);
    return 0;
}
