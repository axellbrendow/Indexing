/**
 * @file mytest.cpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * @brief Arquivo para testes
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 */

#include "tipos.hpp"
#include "DataInputStream.cpp"
#include "DataOutputStream.cpp"

#include <fstream>
#include <iostream>
#include <cstring>

using namespace std;

/**
 * @brief Classe para estudantes.
 * 
 */

class Student
{
    public:
        const int sizeofSize_t = sizeof(size_t);
        const int sizeofInt = sizeof(int);
        string name;
        int age;
        
        Student(string name, int age) : name(name), age(age) {}

        /**
         * @brief This generates bytes.
         * 
         * @param abc a number.
         * 
         * @return tipo_byte* vector of bytes.
         */

        vetor_de_bytes &generateBytes()
        {
            // size_t strlen = name.length();

            // vetor_de_bytes bytes(sizeofSize_t + strlen + sizeofInt);

            // auto iteradorDosBytes = bytes.cbegin();

            // copy(&strlen, &strlen + 1, iteradorDosBytes += sizeofSize_t);
            // copy(name.begin(), name.end(), iteradorDosBytes += strlen);
            // copy(&age, &age + 1, iteradorDosBytes += sizeofInt);

            // for (auto &&byte : bytes)
            // {
            //     cout << byte;
            // }
            // cout << endl;

            // return bytes;
        }
};

int main()
{
	// Student one("axell", 19);
    // one.generateBytes();
	// ofstream ofs("fifthgrade.ros", ios::binary);
    
	// ofs.write((char *) &one, sizeof(one));

    DataOutputStream out(sizeof(int) + sizeof(float));

    int age;
    float height;

    out << 19 << 256;

    vetor_de_bytes vetor = out.obterVetor();

    out.print();

    DataInputStream in( vetor );

    in >> age >> height;

    cout << "age = " << age << ", height = " << height << endl;

	return 0;
}