/**
 * @file DataOutputStream.cpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
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
        /** Vetor de bytes onde serão guardados os dados. */
        vetor_de_bytes bytes;
        iterador cursor;

    public:
        // Construtores

        /**
         * @brief Constrói um novo objeto DataOutputStream com um tamanho inicial
         * de buffer.
         * 
         * @param previsaoDaQuantidadeDeBytes Tamanho inicial do buffer.
         */

        DataOutputStream(int previsaoDaQuantidadeDeBytes) :
            bytes( vetor_de_bytes() ),
            cursor( bytes.begin() )
        {
            bytes.reserve(previsaoDaQuantidadeDeBytes);
        }

        // Métodos

        vetor_de_bytes obterVetor()
        {
            return bytes;
        }

        /**
         * @brief Mostra os bytes do vetor como inteiros.
         */
        
        void print()
        {
            if (!bytes.empty())
            {
                iterador i = bytes.begin();
                iterador fim = bytes.end();

                cout << (int) *i; // Trate esse iterador como um ponteiro

                for (i++; i < fim; i++)
                {
                    cout << "," << (int) *i;
                }
                
                cout << endl;
            }
        }
        
        /**
         * @brief Escreve bytes no vetor da classe.
         * 
         * @tparam tipo Tipo do que se deseja escrever.
         * @param ptrValor Ponteiro para o primeiro byte do valor a ser escrito.
         * @param tamanhoDoValor Quantidade de bytes a serem escritos. Caso esse
         * parâmetro não seja fornecido, o seu valor será sizeof(tipo).
         * 
         * @return DataOutputStream& Retorna uma referência para este objeto.
         */

        template<typename tipo>
        DataOutputStream &escreverPtr(tipo *ptrValor, int tamanhoDoValor = sizeof(tipo))
        {
            // reinterpret_cast faz a conversão do ponteiro para "tipo_byte *".
            // Isso é feito para que eu possa iterar sobre os bytes do valor.
            tipo_byte *inicio = reinterpret_cast<tipo_byte *>(ptrValor);

            // como inicio aponta para o primeiro byte do valor e inicio + tamanhoDoValor
            // apontará para a posição após o último byte do valor, o .insert() copia
            // todos bytes do valor para o final do vetor bytes.
            bytes.insert(bytes.begin() + bytes.size(), inicio, inicio + tamanhoDoValor);

            // cursor += tamanhoDoValor;

            return *this; // retorna uma referência para este objeto.
        }

        /**
         * @brief Escreve tipos primitivos e objetos com tamanho pré definido no
         * vetor da classe.
         * 
         * @tparam tipo Tipo do que se deseja escrever.
         * @param valor Valor que se deseja escrever.
         * @param tamanhoDoValor Tamanho em bytes que o valor gasta. Caso esse parâmetro
         * não seja fornecido, o seu valor será sizeof(tipo).
         * 
         * @return DataOutputStream& Retorna uma referência para este objeto.
         */

        template<typename tipo>
        DataOutputStream &escrever(tipo &valor, int tamanhoDoValor = sizeof(tipo))
        {
            return escreverPtr(&valor);
        }

        /**
         * @brief Escreve uma string no vetor da classe. As strings têm um tratamento
         * especial pois é necessário escrever primeiro o tamanho delas antes de
         * escrever os seus caracteres.
         * 
         * @param str String a ser escrita.
         * @return DataOutputStream& Retorna uma referência para este objeto.
         */

        DataOutputStream &escreverString(string &str)
        {
            str_size_type tamanho = str.length();

            escrever(tamanho);

            return escreverPtr(const_cast<char *>( str.c_str() ), tamanho);
        }
};

// Operadores
template<typename tipo>
DataOutputStream &operator<<(DataOutputStream &dataOutputStream, tipo variavel)
{
    return dataOutputStream.escrever(variavel);
}

template<typename tipo>
DataOutputStream &operator<<(DataOutputStream &dataOutputStream, tipo &variavel)
{
    return dataOutputStream.escrever(variavel);
}

DataOutputStream &operator<<(DataOutputStream &dataOutputStream, string &variavel)
{
    return dataOutputStream.escreverString(variavel);
}