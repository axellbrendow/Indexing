/**
 * @file DataInputStream.cpp
 * @author Axell Brendow ( breno.axel@gmail.com ) ( https://github.com/axell-brendow )
 * @brief Classe de intermediação entre a entrada de dados e a sua variável.
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 */

#include "tipos.hpp"

#include <iostream>

class DataInputStream
{
    public:
        /**
         * @brief O tipo __gnu_cxx::__normal_iterator é apenas uma classe que tem um único campo que é um ponteiro.
         * Objetos construídos por meio desta classe podem ser usados como se fossem ponteiros, ou seja, aceitam
         * operações aritméticas, lógicas, relacionais, dereferenciamento e algumas outras. Na dúvida, finja que um
         * objeto dessa classe é um ponteiro :)
         * 
         * <p>Na referência do site [cplusplus](http://www.cplusplus.com/reference/iterator/), essa classe se encaixa
         * como um iterador do tipo Random Access.</p>
         * 
         * @see http://www.cplusplus.com/reference/iterator/
         */
        typedef __gnu_cxx::__normal_iterator<tipo_byte *, vetor_de_bytes> iterador;

    private:
        // Campos
        vetor_de_bytes &bytes;
        iterador cursor;
        const iterador posicaoFinal;

    public:
        // Construtores
        DataInputStream(vetor_de_bytes &bytes) : bytes(bytes), cursor(bytes.begin()), posicaoFinal(bytes.end()) { }

        // Métodos
        bool estaNoFim()
        {
            return cursor >= posicaoFinal;
        }
        
        bool throwEstaNoFim()
        {
            bool noFim = estaNoFim();

            if (noFim) throw "Fim do vetor de bytes";

            return noFim;
        }

        // Operadores
        explicit operator bool() // Valor retornado ao usar um objeto desta classe num if
        {
            return !estaNoFim();
        }

        // Métodos
        tipo_byte lerByte()
        {
            tipo_byte valor = 0;

            if (!throwEstaNoFim())
            {
                // Copia, para "valor", os bytes entre o "cursor" e o "cursor" + sizeof(tipo_byte)
                copy(cursor, cursor += sizeof(tipo_byte), &valor);
            }

            return valor;
        }

        char lerChar()
        {
            char valor = 0;

            if (!throwEstaNoFim())
            {
                // Copia, para "valor", os bytes entre o "cursor" e o "cursor" + sizeof(char)
                copy(cursor, cursor += sizeof(char), &valor);
            }

            return valor;
        }

        short lerShort()
        {
            short valor = 0;

            if (!throwEstaNoFim())
            {
                // Copia, para "valor", os bytes entre o "cursor" e o "cursor" + sizeof(short)
                copy(cursor, cursor += sizeof(short), &valor);
            }

            return valor;
        }

        int lerInt()
        {
            int valor = 0;

            if (!throwEstaNoFim())
            {
                // Copia, para "valor", os bytes entre o "cursor" e o "cursor" + sizeof(int)
                copy(cursor, cursor += sizeof(int), &valor);
            }

            return valor;
        }

        long lerLong()
        {
            long valor = 0;

            if (!throwEstaNoFim())
            {
                // Copia, para "valor", os bytes entre o "cursor" e o "cursor" + sizeof(long)
                copy(cursor, cursor += sizeof(long), &valor);
            }

            return valor;
        }

        string lerString()
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
tipo_byte &operator>>(DataInputStream &dataInputStream, tipo_byte &variavel)
{
    if (dataInputStream) // checa se é possível ler alguma coisa
    {
        variavel = dataInputStream.lerByte();
    }

    return variavel;
}

char &operator>>(DataInputStream &dataInputStream, char &variavel)
{
    if (dataInputStream) // checa se é possível ler alguma coisa
    {
        variavel = dataInputStream.lerChar();
    }

    return variavel;
}

short &operator>>(DataInputStream &dataInputStream, short &variavel)
{
    if (dataInputStream) // checa se é possível ler alguma coisa
    {
        variavel = dataInputStream.lerShort();
    }

    return variavel;
}

int &operator>>(DataInputStream &dataInputStream, int &variavel)
{
    if (dataInputStream) // checa se é possível ler alguma coisa
    {
        variavel = dataInputStream.lerInt();
    }

    return variavel;
}

long &operator>>(DataInputStream &dataInputStream, long &variavel)
{
    if (dataInputStream) // checa se é possível ler alguma coisa
    {
        variavel = dataInputStream.lerLong();
    }

    return variavel;
}

string &operator>>(DataInputStream &dataInputStream, string &variavel)
{
    if (dataInputStream) // checa se é possível ler alguma coisa
    {
        variavel = dataInputStream.lerString();
    }

    return variavel;
}