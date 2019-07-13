/**
 * @file DataOutputStream.cpp
 * @author Axell Brendow ( breno.axel@gmail.com ) ( https://github.com/axell-brendow )
 * @brief Classe de intermediação entre a sua variável e a saída de dados.
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 */

#include "tipos.hpp"

#include <iostream>

class DataOutputStream
{
    private:
        // Campos
        vetor_de_bytes bytes;

    public:
        // Construtores
        DataOutputStream(int previsaoDaQuantidadeDeBytes) : bytes( vetor_de_bytes(previsaoDaQuantidadeDeBytes) ) { }

        // Métodos
        tipo_byte escreverByte(tipo_byte valor)
        {
            // Copia, para "valor", os bytes entre o "cursor" e o "cursor" + sizeof(tipo_byte)
            tipo_byte destino = 0;

            copy(&valor, &valor + 1, &destino);

            return valor;
        }

        char escreverChar()
        {
            char valor = 0;

            if (!throwEstaNoFim())
            {
                // Copia, para "valor", os bytes entre o "cursor" e o "cursor" + sizeof(char)
                copy(cursor, cursor += sizeof(char), &valor);
            }

            return valor;
        }

        short escreverShort(short valor)
        {
            // Copia, para "valor", os bytes entre o "cursor" e o "cursor" + sizeof(short)
            short destino = 0;

            copy(&valor, &valor + 1, &destino);

            return valor;
        }

        int escreverInt()
        {
            int valor = 0;

            if (!throwEstaNoFim())
            {
                // Copia, para "valor", os bytes entre o "cursor" e o "cursor" + sizeof(int)
                copy(cursor, cursor += sizeof(int), &valor);
            }

            return valor;
        }

        long escreverLong()
        {
            long valor = 0;

            if (!throwEstaNoFim())
            {
                // Copia, para "valor", os bytes entre o "cursor" e o "cursor" + sizeof(long)
                copy(cursor, cursor += sizeof(long), &valor);
            }

            return valor;
        }

        string escreverString()
        {
            string valor = 0;

            if (!throwEstaNoFim())
            {
                size_t tamanho = 0;
                copy(cursor, cursor += sizeof(size_t), &tamanho); // Lê o tamanho da string (tamanho em bytes)

                char str[tamanho + 1];

                // Copia, para "str", os bytes entre o "cursor" e o "cursor" + "tamanho"
                copy(cursor, cursor += tamanho, str);
                
                str[tamanho] = '\0'; // Termina a string

                valor = string(str);
            }

            return valor;
        }
};

// Operadores
tipo_byte &operator>>(DataOutputStream &dataOutputStream, tipo_byte &variavel)
{
    if (dataOutputStream) // checa se é possível escrever alguma coisa
    {
        variavel = dataOutputStream.lerByte();
    }

    return variavel;
}

char &operator>>(DataOutputStream &dataOutputStream, char &variavel)
{
    if (dataOutputStream) // checa se é possível escrever alguma coisa
    {
        variavel = dataOutputStream.lerChar();
    }

    return variavel;
}

short &operator>>(DataOutputStream &dataOutputStream, short &variavel)
{
    if (dataOutputStream) // checa se é possível escrever alguma coisa
    {
        variavel = dataOutputStream.lerShort();
    }

    return variavel;
}

int &operator>>(DataOutputStream &dataOutputStream, int &variavel)
{
    if (dataOutputStream) // checa se é possível escrever alguma coisa
    {
        variavel = dataOutputStream.lerInt();
    }

    return variavel;
}

long &operator>>(DataOutputStream &dataOutputStream, long &variavel)
{
    if (dataOutputStream) // checa se é possível escrever alguma coisa
    {
        variavel = dataOutputStream.lerLong();
    }

    return variavel;
}

string &operator>>(DataOutputStream &dataOutputStream, string &variavel)
{
    if (dataOutputStream) // checa se é possível escrever alguma coisa
    {
        variavel = dataOutputStream.lerString();
    }

    return variavel;
}