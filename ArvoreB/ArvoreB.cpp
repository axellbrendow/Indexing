/**
 * @file ArvoreB.cpp
 * @author Axell Brendow (breno.axel@gmail.com)
 * @brief Arquivo para testes
 * @version 0.1
 * @date 2019-07-11
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 * 
 */

#include "types.hpp"
#include "PaginaB.cpp"

#include <iostream>

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
