/**
 * @file tipos.hpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * @brief Arquivo com typedefs e helpers para debug.
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 */

#pragma once

#include <iostream>
#include <vector>

using namespace std;

/**
 * @brief Criado apenas para padronizar o tipo usado para declarar variáveis que
 * guardam tamanhos de strings.
 */
typedef int str_size_type;

/**
 * @brief Criado apenas para padronizar o tipo usado para declarar ponteiros que
 * guardam o endereço de entidades e/ou registros dentro de um arquivo.
 */
typedef long file_ptr_type;

/**
 * @brief Em C++, usamos unsigned char para lidar com bytes. As vezes usa-se char também.
 */
typedef unsigned char tipo_byte;

/**
 * @brief Tipo destinado a ser usado para trabalhar com vetores de bytes.
 */
typedef vector<tipo_byte> vetor_de_bytes;

/**
 * @brief Tipo de iterador para vetores de bytes.
 * 
 * <p>Iteradores são usados para obter, alterar e andar sobre elementos de um
 * container (ex.: arranjo).</p>
 * 
 * <p>O tipo vetor_de_bytes::iterator é apenas uma classe que tem um único
 * campo que é um ponteiro. Objetos construídos por meio dessa classe podem ser
 * usados como se fossem ponteiros, ou seja, aceitam operações aritméticas, lógicas,
 * relacionais, dereferenciamento e algumas outras. Na dúvida, finja que um objeto
 * dessa classe é um ponteiro :)</p>
 * 
 * <p>Na referência do site [cplusplus](http://www.cplusplus.com/reference/iterator/),
 * essa classe se encaixa como um iterador do tipo Random Access.</p>
 * 
 * @see http://www.cplusplus.com/reference/iterator/
 */
typedef vetor_de_bytes::iterator iterador;
