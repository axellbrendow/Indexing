#include <iostream>
#include <vector>

#include "PaginaB.cpp"

using namespace std;

int main()
{
    PaginaB pagina(0);

    cout << pagina << endl;

    vector<unsigned char> byteArray;

    byteArray.push_back(0x41);
    byteArray.push_back(0x42);
    byteArray.push_back(0x43);
    byteArray.push_back(0x44);

    for (auto &&element : byteArray)
    {
        cout << element;
    }

    cout << endl;
}
