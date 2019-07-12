/**
 * @file mytest.cpp
 * @author Axell Brendow (breno.axel@gmail.com)
 * @brief Arquivo para testes
 * @version 0.1
 * @date 2019-07-11
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 * 
 */

#include "types.hpp"

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

        tipo_byte *generateBytes(int abc)
        {
            vetor_de_bytes bytes(sizeof(int) + name.length() + sizeof(int));
        }
};

int main()
{
    char c = 'a';
	// Student one("axell", 19);
	// ofstream ofs("fifthgrade.ros", ios::binary);
    
	// ofs.write((char *) &one, sizeof(one));
    
	return 0;
}