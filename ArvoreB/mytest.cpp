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

#include <fstream>
#include <iostream>
#include <cstring>
#include <vector>

using namespace std;

/**
 * Links úteis doxygen:<br>
 * 
 * https://flcwiki.desy.de/How%20to%20document%20your%20code%20using%20doxygen#How_to_create_a_configuration_file<br>
 * https://gist.github.com/ugovaretto/261bd1d16d9a0d2e76ee<br>
 */

/**
 * Em C++, usamos unsigned char para lidar com bytes, afinal o range do tipo unsigned char é entre 0 e 255.
 */
typedef unsigned char byte_type;
/**
 * Oh my gudness
 */
typedef vector<byte_type> byte_vector;

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
         * @return byte_type* vector of bytes.
         */

        byte_type *generateBytes(int abc)
        {
            byte_vector bytes(sizeof(int) + name.length() + sizeof(int));
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