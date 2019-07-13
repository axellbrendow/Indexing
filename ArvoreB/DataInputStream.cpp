/**
 * @file DataInputStream.cpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * @brief Classe de intermediação entre a entrada de dados e a sua variável.
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 */

#include "tipos.hpp"

#include <iostream>

class DataInputStream
{
    public:
        // Campos
        /** Vetor onde estão os dados a serem extraídos. */
        vetor_de_bytes bytes;
        iterador cursor;
        const iterador posicaoFinal;

    public:
        // Construtores

        /**
         * @brief Constrói um novo objeto DataInputStream tomando como entrada de
         * dados o vetor de bytes recebido.
         * 
         * @param bytes Entrada de onde os dados serão extraídos.
         */

        DataInputStream(vetor_de_bytes bytes) :
            bytes(bytes),
            cursor(this->bytes.begin()),
            posicaoFinal(this->bytes.end()) { }

        /**
         * @brief Constrói um novo objeto DataInputStream tomando como entrada de
         * dados o vetor de bytes recebido.
         * 
         * @param bytes Entrada de onde os dados serão extraídos.
         */

        // DataInputStream(vetor_de_bytes &bytes) :
        //     bytes(bytes), cursor(bytes.begin()), posicaoFinal(bytes.end()) { }

        // Métodos

        /**
         * @brief Checa se todos os dados do vetor foram consumidos.
         * 
         * @return true Caso não haja mais dados para se consumir.
         * @return false Caso haja mais dados para se consumir.
         */

        bool estaNoFim()
        {
            return cursor >= posicaoFinal;
        }

        /**
         * @brief Checa se todos os dados do vetor foram consumidos.
         * 
         * @return true Caso não haja mais dados para se consumir.
         * @return false Caso haja mais dados para se consumir.
         * 
         * @throws "Fim do vetor de bytes" Lança a exceção caso não haja mais dados
         * para se consumir.
         */

        bool throwEstaNoFim()
        {
            bool noFim = estaNoFim();

            if (noFim) throw "Fim do vetor de bytes";

            return noFim;
        }

        // Operadores

        /**
         * @brief Valor retornado ao usar um objeto desta classe numa expressão booleana.
         * 
         * @return true Retorna true caso haja dados para se consumir.
         * @return false Retorna false caso não haja dados para se consumir.
         */

        explicit operator bool()
        {
            return !estaNoFim();
        }

        // Métodos
        
        /**
         * @brief Lê bytes do vetor e os coloca a partir de onde o ponteiro aponta.
         * 
         * @tparam tipo Tipo do valor apontado pelo ponteiro.
         * @param ptr Ponteiro para onde o valor deve ser colocado.
         * @param tamanhoDoValor Quantidade de bytes do valor.
         */

        template<typename tipo>
        void lerParaOPonteiro(tipo *ptr, int tamanhoDoValor = sizeof(tipo))
        {
            if (!throwEstaNoFim())
            {
                // Copia, para "valor", os bytes entre o "cursor" e
                // o "cursor" + tamanhoDoValor
                copy(cursor, cursor += tamanhoDoValor, reinterpret_cast<tipo_byte *>(ptr));
            }
        }
        
        /**
         * @brief Lê um tipo primitivo ou um objeto com tamanho pré definido.
         * 
         * @tparam tipo Tipo do valor a ser lido.
         * @param tamanhoDoValor Quantidade de bytes do valor.
         * 
         * @return tipo Retorna uma cópia do valor lido.
         */

        template<typename tipo>
        tipo ler(int tamanhoDoValor = sizeof(tipo))
        {
            tipo valor = 0;

            lerParaOPonteiro(&valor, tamanhoDoValor);

            return valor;
        }

        tipo_byte lerByte()
        {
            return ler<tipo_byte>();
        }

        char lerChar()
        {
            return ler<char>();
        }

        short lerShort()
        {
            return ler<short>();
        }

        int lerInt()
        {
            return ler<int>();
        }

        long lerLong()
        {
            return ler<long>();
        }

        string lerString()
        {
            str_size_type tamanho = ler<str_size_type>();

            char str[tamanho];

            lerParaOPonteiro(str, tamanho);
            
            return string(str, tamanho);
        }
};

// Operadores
template<typename tipo>
DataInputStream &operator>>(DataInputStream &dataInputStream, tipo &variavel)
{
    variavel = dataInputStream.ler<tipo>();

    return dataInputStream;
}

DataInputStream &operator>>(DataInputStream &dataInputStream, string &variavel)
{
    variavel = dataInputStream.lerString();

    return dataInputStream;
}