/**
 * @file tipos.hpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * @brief Classe de intermediação entre a entrada de dados e a sua variável.
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 */

#pragma once

#include <vector>

using namespace std;

#define debug cout << "debug" << endl;
#define debugv(value) cout << "debug = " << value << endl;

/**
 * Links úteis doxygen:<br>
 * 
 * https://flcwiki.desy.de/How%20to%20document%20your%20code%20using%20doxygen#How_to_create_a_configuration_file<br>
 * https://gist.github.com/ugovaretto/261bd1d16d9a0d2e76ee<br>
 */

/**
 * Links úteis smart pointers:<br>
 * 
 * https://docs.microsoft.com/pt-br/cpp/cpp/smart-pointers-modern-cpp?view=vs-2019<br>
 * https://docs.microsoft.com/pt-br/cpp/cpp/how-to-create-and-use-shared-ptr-instances?view=vs-2019<br>
 */

/**
 * @brief Tipo criado apenas para padronização da criação de variáveis que guardam
 * tamanhos de strings.
 */

typedef int str_size_type;

/**
 * @brief Em C++, usamos unsigned char para lidar com bytes. As vezes usa-se char também.
 * De qualquer forma, o intervalo de valores do tipo unsigned char é o mesmo do
 * intervalo de um byte, 0 a 255.
 */

typedef unsigned char tipo_byte;

/**
 * @brief Tipo destinado a ser usado para trabalhar com vetores de bytes.
 */

typedef vector<tipo_byte> vetor_de_bytes;

/**
 * @brief Tipo de iterador para vetores de bytes.
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