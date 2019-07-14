/**
 * @file mytest.cpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * @brief Arquivo para testes
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 */

#pragma once

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

        vetor_de_bytes generateBytes()
        {
            DataOutputStream out(sizeof(str_size_type) + name.length() + sizeof(int));

            return (out << name << age).obterVetor();
        }
};

int main()
{
	// Student one("axell", 19);
    // one.generateBytes();
	// ofstream ofs("fifthgrade.ros", ios::binary);
    
	// ofs.write((char *) &one, sizeof(one));

    DataOutputStream out(sizeof(int) + sizeof(float));

    string name;
    int age;
    float height;

    out << "me" << 19 << 1.8f;
    
    DataInputStream in( out.obterVetor() );

    in >> name >> age >> height;

    cout << "name = " << name << ", age = " << age << ", height = " << height << endl;

	return 0;
}