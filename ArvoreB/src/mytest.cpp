/**
 * @file mytest.cpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * @brief Arquivo para testes
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 */

#pragma once

#include "templates/tipos.hpp"
#include "streams/DataInputStream.hpp"
#include "streams/DataOutputStream.hpp"

#include <fstream>
#include <iostream>
#include <cstring>

using namespace std;

/**
 * @brief Classe para estudantes.
 */

class Student
{
    public:
        string nome;
        int idade;
        
        Student(string nome, int idade) : nome(nome), idade(idade) {}

        DataOutputStream gerarOutputStream()
        {
            DataOutputStream out(sizeof(str_size_type) + nome.length() + sizeof(int));

            return out << nome << idade;
        }

        Student &imprimir()
        {
            cout << "nome = " << nome << ", idade = " << idade << endl;

            return *this;
        }

        DataOutputStream imprimirVetor()
        {
            auto out = gerarOutputStream();

            cout << out;

            return out;
        }
};

int main()
{
	Student one("axell", 19);
    
    auto vetor = one.imprimirVetor();
    
	ofstream ofs("fifthgrade.ros", ios::binary);
    
	ofs << one.gerarOutputStream();

    ofs.close();

	return 0;
}