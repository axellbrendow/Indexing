/**
 * @file helpersArvore.hpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * @brief Arquivo com typedefs e helpers para a árvore B.
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 */

#pragma once

#include "templates/serializavel.hpp"

using namespace std;

/**
 * @brief Navega até o fim do arquivo e obtém a localização do ponteiro de get
 * por meio da função tellg().
 * 
 * @param arquivo Arquivo a ser processado.
 * 
 * @return size_t Tamanho do arquivo em bytes.
 */
size_t obterTamanhoEmBytes(fstream& arquivo)
{
    arquivo.seekg(0, fstream::end);

    return arquivo.tellg();
}

// Especialização para classes abstratas
// https://stackoverflow.com/questions/24936862/c-template-specialization-for-subclasses-with-abstract-base-class
template<typename TIPO, bool = is_base_of<Serializavel, TIPO>::value>
struct Medidor
{
    static size_t obterTamanhoEmBytes()
    {
        // static_assert -> https://www.geeksforgeeks.org/understanding-static_assert-c-11/
        // is_fundamental<> checa se o tipo do seu parâmetro é primitivo
        // https://stackoverflow.com/questions/580922/identifying-primitive-types-in-templates
        static_assert(
            is_fundamental<TIPO>::value,
            "Os tipos da árvore devem ser primitivos caso não herdem de Serializavel."
        );

        return sizeof(TIPO);
    }
};

// Especialização para classes abstratas
// https://stackoverflow.com/questions/24936862/c-template-specialization-for-subclasses-with-abstract-base-class
template<typename TIPO>
struct Medidor<TIPO, true> // true quando TIPO herdar de Serializavel
{
    static size_t obterTamanhoEmBytes()
    {
        return TIPO().obterTamanhoMaximoEmBytes();
    }
};

template<typename TIPO_DAS_CHAVES, typename TIPO_DOS_DADOS>
void obterTamanhoEmBytesDaChaveEDoDado(
    int& maximoDeBytesParaAChave,
    int& maximoDeBytesParaODado
)
{
    // static_assert -> https://www.geeksforgeeks.org/understanding-static_assert-c-11/
    // is_default_constructible -> http://www.cplusplus.com/reference/type_traits/is_default_constructible/?kw=is_default_constructible
    static_assert(is_default_constructible<TIPO_DAS_CHAVES>::value,
        "O tipo das chaves da árvore deve ter um construtor sem parâmetros."
    );

    static_assert(is_default_constructible<TIPO_DOS_DADOS>::value,
        "O tipo dos dados da árvore deve ter um construtor sem parâmetros."
    );

    maximoDeBytesParaAChave = Medidor<TIPO_DAS_CHAVES>::obterTamanhoEmBytes();
    maximoDeBytesParaODado = Medidor<TIPO_DOS_DADOS>::obterTamanhoEmBytes();
}